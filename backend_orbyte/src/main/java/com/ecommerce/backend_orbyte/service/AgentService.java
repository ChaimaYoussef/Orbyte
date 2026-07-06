package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.dto.request.AgentRequest;
import com.ecommerce.backend_orbyte.dto.response.AgentResponse;

import java.util.List;
import java.util.UUID;

public interface AgentService {
    List<AgentResponse> findAll();
    AgentResponse findById(UUID id);
    AgentResponse create(AgentRequest request);
    AgentResponse update(UUID id, AgentRequest request);
    void delete(UUID id);
}
