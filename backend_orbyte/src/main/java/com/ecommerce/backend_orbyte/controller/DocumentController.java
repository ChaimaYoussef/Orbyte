package com.ecommerce.backend_orbyte.controller;

import com.ecommerce.backend_orbyte.dto.request.DocumentRequest;
import com.ecommerce.backend_orbyte.dto.response.DocumentResponse;
import com.ecommerce.backend_orbyte.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> findAll() {
        return ResponseEntity.ok(documentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(documentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> update(@PathVariable UUID id, @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(documentService.update(id, request));
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<java.util.Map<String, Object>> upload(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "userRoles", required = false) List<String> userRoles,
            @RequestParam(value = "connectorId", required = false) UUID connectorId) {
        UUID jobId = documentService.uploadDocument(file, userRoles, connectorId);
        return ResponseEntity.ok(java.util.Map.of("jobId", jobId, "status", "PROCESSING"));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<java.util.Map<String, String>> getStatus(@PathVariable UUID jobId) {
        String status = documentService.getJobStatus(jobId);
        return ResponseEntity.ok(java.util.Map.of("jobId", jobId.toString(), "status", status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
