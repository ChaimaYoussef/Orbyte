package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.dto.request.DocumentRequest;
import com.ecommerce.backend_orbyte.dto.response.DocumentResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    List<DocumentResponse> findAll();
    DocumentResponse findById(UUID id);
    DocumentResponse create(DocumentRequest request);
    DocumentResponse update(UUID id, DocumentRequest request);
    void delete(UUID id);
    UUID uploadDocument(org.springframework.web.multipart.MultipartFile file, List<String> userRoles, UUID connectorId);
    String getJobStatus(UUID jobId);
}
