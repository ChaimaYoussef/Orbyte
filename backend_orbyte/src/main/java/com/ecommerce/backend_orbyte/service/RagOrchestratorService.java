package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.dto.request.ChatRequest;
import com.ecommerce.backend_orbyte.dto.response.ChatMessageResponse;

public interface RagOrchestratorService {
    ChatMessageResponse sendMessage(ChatRequest request);
}
