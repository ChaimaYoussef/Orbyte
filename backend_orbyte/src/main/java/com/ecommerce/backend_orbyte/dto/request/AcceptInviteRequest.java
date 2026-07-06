package com.ecommerce.backend_orbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AcceptInviteRequest {
    @NotBlank(message = "Le token est requis")
    private String token;

    @NotBlank(message = "Le nom complet est requis")
    private String fullName;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
}
