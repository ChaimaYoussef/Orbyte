package com.ecommerce.backend_orbyte.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String action; // e.g., CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String entityName; // Name of the entity affected

    @Column(nullable = true)
    private UUID entityId; // ID of the affected entity

    @Column(nullable = false)
    private UUID performedBy; // User ID who performed the action

    @Column(columnDefinition = "TEXT")
    private String details; // Optional JSON or description of changes

    @CreationTimestamp
    private LocalDateTime createdAt;
}
