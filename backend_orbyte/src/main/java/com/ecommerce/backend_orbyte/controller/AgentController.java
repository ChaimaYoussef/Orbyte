package com.ecommerce.backend_orbyte.controller;

import com.ecommerce.backend_orbyte.dto.request.AgentRequest;
import com.ecommerce.backend_orbyte.dto.response.AgentResponse;
import com.ecommerce.backend_orbyte.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public ResponseEntity<List<AgentResponse>> findAll() {
        return ResponseEntity.ok(agentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(agentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AgentResponse> create(@Valid @RequestBody AgentRequest request) {
        return ResponseEntity.ok(agentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentResponse> update(@PathVariable UUID id, @Valid @RequestBody AgentRequest request) {
        return ResponseEntity.ok(agentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
