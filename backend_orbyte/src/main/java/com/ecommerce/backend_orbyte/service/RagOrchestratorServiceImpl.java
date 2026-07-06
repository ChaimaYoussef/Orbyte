package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.client.AiApiClient;
import com.ecommerce.backend_orbyte.common.exception.ResourceNotFoundException;
import com.ecommerce.backend_orbyte.dto.request.ChatRequest;
import com.ecommerce.backend_orbyte.dto.response.ChatMessageResponse;
import com.ecommerce.backend_orbyte.entity.Agent;
import com.ecommerce.backend_orbyte.entity.ChatMessage;
import com.ecommerce.backend_orbyte.entity.ChatSession;
import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.repository.AgentRepository;
import com.ecommerce.backend_orbyte.repository.ChatMessageRepository;
import com.ecommerce.backend_orbyte.repository.ChatSessionRepository;
import com.ecommerce.backend_orbyte.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagOrchestratorServiceImpl implements RagOrchestratorService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final AiApiClient aiApiClient;
    private final AuditService auditService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(ChatRequest request) {
        log.info("Processing chat query for session: {}", request.getSessionId());

        ChatSession session = chatSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("ChatSession", "id", request.getSessionId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        ChatMessage userMessage = ChatMessage.builder()
                .chatSession(session)
                .role(ChatMessage.MessageRole.USER)
                .content(request.getMessage())
                .build();
        chatMessageRepository.save(userMessage);

        String systemPrompt = null;
        UUID agentId = request.getAgentId() != null ? request.getAgentId() : session.getAgentId();
        if (agentId != null) {
            Agent agent = agentRepository.findById(agentId).orElse(null);
            if (agent != null) {
                systemPrompt = agent.getSystemPrompt();
                log.info("Applying Agent system prompt for agent: {}", agent.getName());
            }
        }

        long startTime = System.currentTimeMillis();

        String realUsername = user.getEmail() != null ? user.getEmail() : "user_" + user.getId();
        String userClearance = mapRoleToClearance(user.getRole().name());

        AiApiClient.QueryRequest queryRequest = AiApiClient.QueryRequest.builder()
                .username(realUsername)
                .query(request.getMessage())
                .top_k(3)
                .clearance(userClearance)
                .build();

        AiApiClient.QueryResponse queryResponse = aiApiClient.query(queryRequest);
        long latencyMs = System.currentTimeMillis() - startTime;



        String lastMsg = queryResponse.getResponse();
        if (lastMsg != null && lastMsg.length() > 250) {
            lastMsg = lastMsg.substring(0, 247) + "...";
        }
        session.setLastMessage(lastMsg);

        // Auto-rename the session if it's the first message
        if (session.getTitle() == null || session.getTitle().equals("New Conversation")) {
            String newTitle = request.getMessage();
            if (newTitle.length() > 30) {
                newTitle = newTitle.substring(0, 27) + "...";
            }
            session.setTitle(newTitle);
        }

        chatSessionRepository.save(session);

        List<ChatMessageResponse.SourceDTO> sourceDTOs = new ArrayList<>();
        if (queryResponse.getChunks_used() != null) {
            for (AiApiClient.ChunkResult s : queryResponse.getChunks_used()) {
                String docId = s.getChunk_id() != null ? s.getChunk_id() : "unknown";
                sourceDTOs.add(ChatMessageResponse.SourceDTO.builder()
                        .label("Document: " + docId)
                        .url("#")
                        .snippet(s.getSnippet())
                        .build());
            }
        }

        String sourcesJson = "[]";
        try {
            sourcesJson = objectMapper.writeValueAsString(sourceDTOs);
        } catch (Exception e) {
            log.error("Failed to serialize sources list: {}", e.getMessage());
        }

        ChatMessage assistantMessage = ChatMessage.builder()
                .chatSession(session)
                .role(ChatMessage.MessageRole.ASSISTANT)
                .content(queryResponse.getResponse())
                .responseTimeMs(latencyMs)
                .sources(List.of(sourcesJson))
                .build();
        ChatMessage savedAssistantMessage = chatMessageRepository.save(assistantMessage);

        auditService.log(
                user.getId(),
                "CHAT_QUERY",
                "ChatSession",
                session.getId().toString(),
                String.format("User query: '%s'. Generated RAG answer in %dms.", request.getMessage(), latencyMs));

        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(savedAssistantMessage.getId())
                .role(savedAssistantMessage.getRole().name())
                .content(savedAssistantMessage.getContent())
                .timestamp(savedAssistantMessage.getTimestamp())
                .thinking(savedAssistantMessage.getThinking())
                .thinkingUrl(savedAssistantMessage.getThinkingUrl())
                .sources(sourceDTOs)
                .build();

        return response;
    }

    private String mapRoleToClearance(String springRole) {
        return switch (springRole) {
            case "ADMIN", "CURATOR" -> "Confidential";
            case "USER" -> "Internal";
            case "VIEWER" -> "Public";
            default -> "Public";
        };
    }
}
