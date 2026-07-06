package com.ecommerce.backend_orbyte.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChatResponse {
    private UUID id;
    private String title;
    private String lastMessage;
    private LocalDateTime timestamp;
    private UUID agentId;
    private UUID userId;
}
