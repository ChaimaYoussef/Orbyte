package com.ecommerce.backend_orbyte.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InviteResponse {
    private String email;
    private String token;
    private LocalDateTime expiresAt;
}
