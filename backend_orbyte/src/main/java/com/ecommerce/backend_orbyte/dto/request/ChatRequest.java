package com.ecommerce.backend_orbyte.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatRequest {
    private UUID sessionId;

    private String title;

    private UUID userId;

    private UUID agentId;

    private String message;
}
