package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.common.exception.ResourceNotFoundException;
import com.ecommerce.backend_orbyte.entity.AuditLog;
import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.repository.AuditLogRepository;
import com.ecommerce.backend_orbyte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> findByUserId(UUID userId) {
        return auditLogRepository.findAllByPerformedByOrderByCreatedAtDesc(userId);
    }

    @Override
    public AuditLog log(UUID userId, String action, String entity, String entityId, String details) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        AuditLog auditLog = AuditLog.builder()
                .performedBy(userId)
                .action(action)
                .entityName(entity)
                .entityId(entityId != null ? UUID.fromString(entityId) : null)
                .details(details)
                .build();
        return auditLogRepository.save(auditLog);
    }
}
