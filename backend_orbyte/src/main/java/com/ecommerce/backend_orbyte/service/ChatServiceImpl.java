package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.common.exception.ResourceNotFoundException;
import com.ecommerce.backend_orbyte.dto.request.ChatRequest;
import com.ecommerce.backend_orbyte.dto.response.ChatResponse;
import com.ecommerce.backend_orbyte.dto.response.ChatMessageResponse;
import com.ecommerce.backend_orbyte.entity.ChatSession;
import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.repository.ChatSessionRepository;
import com.ecommerce.backend_orbyte.repository.ChatMessageRepository;
import com.ecommerce.backend_orbyte.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final RagOrchestratorService ragOrchestratorService;
    private final ObjectMapper objectMapper;

    @Override
    public List<ChatResponse> findAllSessions() {
        return chatSessionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChatResponse findSessionById(UUID id) {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChatSession", "id", id));
        return toResponse(session);
    }

    @Override
    public ChatResponse createSession(ChatRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        ChatSession session = ChatSession.builder()
                .title(request.getTitle())
                .user(user)
                .agentId(request.getAgentId())
                .build();
        return toResponse(chatSessionRepository.save(session));
    }

    @Override
    public void deleteSession(UUID id) {
        if (!chatSessionRepository.existsById(id)) {
            throw new ResourceNotFoundException("ChatSession", "id", id);
        }
        chatSessionRepository.deleteById(id);
    }

    @Override
    public ChatMessageResponse queryChat(ChatRequest request) {
        return ragOrchestratorService.sendMessage(request);
    }

    @Override
    public List<ChatMessageResponse> getMessagesBySessionId(UUID sessionId) {
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("ChatSession", "id", sessionId);
        }
        return chatMessageRepository.findByChatSessionIdOrderByTimestampAsc(sessionId).stream()
                .map(msg -> {
                    List<ChatMessageResponse.SourceDTO> sourceDTOs = new ArrayList<>();
                    if (msg.getSources() != null && !msg.getSources().isEmpty()) {
                        try {
                            sourceDTOs = objectMapper.readValue(
                                    msg.getSources().get(0),
                                    new com.fasterxml.jackson.core.type.TypeReference<List<ChatMessageResponse.SourceDTO>>() {}
                            );
                        } catch (Exception e) {
                            // ignore parsing error
                        }
                    }
                    return ChatMessageResponse.builder()
                            .id(msg.getId())
                            .role(msg.getRole().name())
                            .content(msg.getContent())
                            .timestamp(msg.getTimestamp())
                            .thinking(msg.getThinking())
                            .thinkingUrl(msg.getThinkingUrl())
                            .sources(sourceDTOs)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ChatResponse toResponse(ChatSession session) {
        return ChatResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .userId(session.getUser().getId())
                .timestamp(session.getTimestamp())
                .agentId(session.getAgentId())
                .lastMessage(session.getLastMessage())
                .build();
    }
}
