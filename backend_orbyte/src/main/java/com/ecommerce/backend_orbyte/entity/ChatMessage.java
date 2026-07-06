package com.ecommerce.backend_orbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id", nullable = false)
    private ChatSession chatSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String thinking;

    private String thinkingUrl;

    private Long responseTimeMs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "chat_message_sources", joinColumns = @JoinColumn(name = "chat_message_id"))
    @Column(name = "source", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> sources = new ArrayList<>();

    public enum MessageRole {
        USER, ASSISTANT
    }
}
