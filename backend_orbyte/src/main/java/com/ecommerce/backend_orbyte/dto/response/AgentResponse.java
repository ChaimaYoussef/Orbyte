package com.ecommerce.backend_orbyte.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AgentResponse {
    private UUID id;
    private String name;
    private String description;
    private String icon;
    private String category;
    private boolean isDefault;
    private int usageCount;
    private String model;
    private String systemPrompt;
    private boolean active;
    private LocalDateTime createdAt;
}
