package com.ecommerce.backend_orbyte.controller;

import com.ecommerce.backend_orbyte.dto.request.InviteRequest;
import com.ecommerce.backend_orbyte.dto.request.UserRequest;
import com.ecommerce.backend_orbyte.dto.response.InviteResponse;
import com.ecommerce.backend_orbyte.dto.response.UserResponse;
import com.ecommerce.backend_orbyte.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Create user")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @Operation(summary = "Update user")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Invite a new user (ADMIN only)")
    @PostMapping("/invite")
    public ResponseEntity<InviteResponse> inviteUser(@Valid @RequestBody InviteRequest request) {
        return ResponseEntity.ok(userService.inviteUser(request));
    }
}
