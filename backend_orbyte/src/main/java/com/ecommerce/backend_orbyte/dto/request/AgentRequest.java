package com.ecommerce.backend_orbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentRequest {
    @NotBlank
    private String name;

    private String description;

    private String icon;

    private String category;

    private boolean isDefault;

    private String model;

    private String systemPrompt;
}
