package com.ecommerce.backend_orbyte.dto.request;

import com.ecommerce.backend_orbyte.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteRequest {
    @Email(message = "L'email n'est pas valide")
    @NotBlank(message = "L'email est requis")
    private String email;

    @NotNull(message = "Le rôle est requis")
    private UserRole role;
}
