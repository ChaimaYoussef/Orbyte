package com.ecommerce.backend_orbyte.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.ecommerce.backend_orbyte.entity.UserRole;
import com.ecommerce.backend_orbyte.entity.UserStatus;

/**
 * Request payload for creating or updating a user via the admin API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    private String fullName;
    private String email;
    private String password; // should be encoded by the service
    private UserRole role;
    private String department;
    private UserStatus status;
}
