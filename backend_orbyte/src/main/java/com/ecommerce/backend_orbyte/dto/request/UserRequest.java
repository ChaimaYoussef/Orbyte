package com.ecommerce.backend_orbyte.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String password;

    @NotBlank
    private String role;

    @NotBlank
    private String status;

    @NotBlank
    private String department;
}
