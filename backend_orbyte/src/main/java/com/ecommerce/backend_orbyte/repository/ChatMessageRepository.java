package com.ecommerce.backend_orbyte.repository;

import com.ecommerce.backend_orbyte.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findByChatSessionIdOrderByTimestampAsc(UUID chatSessionId);

    long countByChatSessionId(UUID chatSessionId);
}
