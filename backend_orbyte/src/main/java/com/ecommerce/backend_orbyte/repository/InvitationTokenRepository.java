package com.ecommerce.backend_orbyte.repository;

import com.ecommerce.backend_orbyte.entity.InvitationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvitationTokenRepository extends JpaRepository<InvitationToken, UUID> {
    Optional<InvitationToken> findByToken(String token);
    Optional<InvitationToken> findByEmail(String email);
}
