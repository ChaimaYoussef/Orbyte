package com.ecommerce.backend_orbyte.controller;

import com.ecommerce.backend_orbyte.dto.request.ConnectorRequest;
import com.ecommerce.backend_orbyte.dto.response.ConnectorResponse;
import com.ecommerce.backend_orbyte.service.ConnectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/connectors")
@RequiredArgsConstructor
public class ConnectorController {

    private final ConnectorService connectorService;

    @GetMapping
    public ResponseEntity<List<ConnectorResponse>> findAll() {
        return ResponseEntity.ok(connectorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConnectorResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(connectorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ConnectorResponse> create(@Valid @RequestBody ConnectorRequest request) {
        return ResponseEntity.ok(connectorService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConnectorResponse> update(@PathVariable UUID id, @Valid @RequestBody ConnectorRequest request) {
        return ResponseEntity.ok(connectorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        connectorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
