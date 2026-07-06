package com.ecommerce.backend_orbyte.repository;

import com.ecommerce.backend_orbyte.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {

    List<Agent> findByIsDefaultTrue();

    List<Agent> findAllByOrderByUsageCountDesc();
}
