package com.ecommerce.backend_orbyte.controller;

import com.ecommerce.backend_orbyte.dto.request.ChatRequest;
import com.ecommerce.backend_orbyte.dto.response.ChatResponse;
import com.ecommerce.backend_orbyte.service.ChatService;
import com.ecommerce.backend_orbyte.repository.UserRepository;
import com.ecommerce.backend_orbyte.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ChatResponse>> findAll() {
        return ResponseEntity.ok(chatService.findAllSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(chatService.findSessionById(id));
    }

    private void populateUserId(ChatRequest request) {
        if (request.getUserId() == null && SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User) {
                request.setUserId(((User) principal).getId());
            } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                userRepository.findByEmail(email).ifPresent(u -> request.setUserId(u.getId()));
            }
        }
    }

    @PostMapping
    public ResponseEntity<ChatResponse> create(@Valid @RequestBody ChatRequest request) {
        populateUserId(request);
        return ResponseEntity.ok(chatService.createSession(request));
    }

    @PostMapping("/query")
    public ResponseEntity<com.ecommerce.backend_orbyte.dto.response.ChatMessageResponse> queryChat(@Valid @RequestBody ChatRequest request) {
        populateUserId(request);
        return ResponseEntity.ok(chatService.queryChat(request));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<com.ecommerce.backend_orbyte.dto.response.ChatMessageResponse>> getMessages(@PathVariable UUID id) {
        return ResponseEntity.ok(chatService.getMessagesBySessionId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        chatService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
