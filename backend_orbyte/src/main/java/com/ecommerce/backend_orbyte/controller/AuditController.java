package com.ecommerce.backend_orbyte.controller;

import com.ecommerce.backend_orbyte.entity.AuditLog;
import com.ecommerce.backend_orbyte.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditLog>> findAll() {
        return ResponseEntity.ok(auditService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(auditService.findByUserId(userId));
    }
}
