package com.ecommerce.backend_orbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class DocumentRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String source;

    private String status;

    private UUID connectorId;
}
