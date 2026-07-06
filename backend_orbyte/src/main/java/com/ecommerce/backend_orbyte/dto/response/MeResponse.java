package com.ecommerce.backend_orbyte.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MeResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String role;
    private String department;
    private LocalDateTime lastActive;
}
