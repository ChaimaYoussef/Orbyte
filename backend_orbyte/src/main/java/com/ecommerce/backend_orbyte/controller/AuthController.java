package com.ecommerce.backend_orbyte.controller;

import com.ecommerce.backend_orbyte.dto.request.AcceptInviteRequest;
import com.ecommerce.backend_orbyte.dto.request.LoginRequest;
import com.ecommerce.backend_orbyte.dto.request.RefreshRequest;
import com.ecommerce.backend_orbyte.dto.response.LoginResponse;
import com.ecommerce.backend_orbyte.dto.response.MeResponse;
import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.service.AuthService;
import com.ecommerce.backend_orbyte.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Login user")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader.replace("Bearer ", ""));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get current authenticated user info")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        MeResponse response = MeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .lastActive(user.getLastActive())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Accept user invitation")
    @PostMapping("/accept-invite")
    public ResponseEntity<LoginResponse> acceptInvite(
            @Valid @RequestBody AcceptInviteRequest request) {
        return ResponseEntity.ok(userService.acceptInvite(request));
    }
}
