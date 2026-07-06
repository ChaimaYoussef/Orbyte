package com.ecommerce.backend_orbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String lastMessage;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @org.hibernate.annotations.UpdateTimestamp
    private LocalDateTime updatedAt;

    private UUID agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
