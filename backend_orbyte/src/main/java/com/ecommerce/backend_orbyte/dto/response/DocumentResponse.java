package com.ecommerce.backend_orbyte.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DocumentResponse {
    private UUID id;
    private String title;
    private String source;
    private String status;
    private String fileType;
    private UUID connectorId;
    private LocalDateTime createdAt;
}
