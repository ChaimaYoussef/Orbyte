package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.entity.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditService {
    List<AuditLog> findAll();
    List<AuditLog> findByUserId(UUID userId);
    AuditLog log(UUID userId, String action, String entity, String entityId, String details);
}
