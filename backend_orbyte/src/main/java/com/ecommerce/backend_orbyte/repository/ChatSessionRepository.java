package com.ecommerce.backend_orbyte.repository;

import com.ecommerce.backend_orbyte.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    List<ChatSession> findByUserId(UUID userId);

    List<ChatSession> findByUserIdOrderByTimestampDesc(UUID userId);
}
