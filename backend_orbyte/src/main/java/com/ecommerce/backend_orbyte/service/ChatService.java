package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.dto.request.ChatRequest;
import com.ecommerce.backend_orbyte.dto.response.ChatResponse;
import com.ecommerce.backend_orbyte.dto.response.ChatMessageResponse;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    List<ChatResponse> findAllSessions();
    ChatResponse findSessionById(UUID id);
    ChatResponse createSession(ChatRequest request);
    void deleteSession(UUID id);
    ChatMessageResponse queryChat(ChatRequest request);
    List<ChatMessageResponse> getMessagesBySessionId(UUID sessionId);
}
