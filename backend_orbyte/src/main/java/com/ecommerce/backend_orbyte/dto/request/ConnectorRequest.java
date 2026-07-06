package com.ecommerce.backend_orbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConnectorRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private String config;
}
