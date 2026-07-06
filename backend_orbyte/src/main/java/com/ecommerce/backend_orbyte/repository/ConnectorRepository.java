package com.ecommerce.backend_orbyte.repository;

import com.ecommerce.backend_orbyte.entity.Connector;
import com.ecommerce.backend_orbyte.entity.ConnectorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConnectorRepository extends JpaRepository<Connector, UUID> {

    List<Connector> findAllByStatus(ConnectorStatus status);
}
