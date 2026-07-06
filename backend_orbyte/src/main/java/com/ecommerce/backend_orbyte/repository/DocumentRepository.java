package com.ecommerce.backend_orbyte.repository;

import com.ecommerce.backend_orbyte.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByConnectorId(UUID connectorId);

    List<Document> findByStatus(String status);

    long countByStatus(String status);
}
