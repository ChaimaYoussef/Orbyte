package com.ecommerce.backend_orbyte.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO returned by the user endpoints.
 */
@Data
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String fullName;
    private String email;
    private String role;
    private String department;
    private String status;
    private boolean active;
    private LocalDateTime lastActive;
    private int messagesCount;
    private LocalDateTime createdAt;
}
