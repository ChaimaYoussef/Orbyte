package com.ecommerce.backend_orbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "connectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Connector {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectorType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ConnectorStatus status = ConnectorStatus.DISCONNECTED;

    @Builder.Default
    private int docsIndexed = 0;

    private LocalDateTime lastSync;

    @Column(columnDefinition = "TEXT")
    private String config;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
