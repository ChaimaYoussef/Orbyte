package com.ecommerce.backend_orbyte.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ecommerce.backend_orbyte.entity.UserRole;
import com.ecommerce.backend_orbyte.entity.UserStatus;

/**
 * DTO for updating a user via the admin API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private String password; // optional, if provided will be encoded
    private UserRole role;
    private String department;
    private UserStatus status;
    private Boolean active;
}
