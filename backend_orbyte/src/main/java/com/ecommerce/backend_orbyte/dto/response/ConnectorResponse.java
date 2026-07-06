package com.ecommerce.backend_orbyte.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConnectorResponse {
    private UUID id;
    private String name;
    private String type;
    private String status;
    private int docsIndexed;
    private LocalDateTime lastSync;
    private String config;
    private boolean active;
    private LocalDateTime createdAt;
}
