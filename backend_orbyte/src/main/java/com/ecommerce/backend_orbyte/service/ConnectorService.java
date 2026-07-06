package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.dto.request.ConnectorRequest;
import com.ecommerce.backend_orbyte.dto.response.ConnectorResponse;

import java.util.List;
import java.util.UUID;

public interface ConnectorService {
    List<ConnectorResponse> findAll();
    ConnectorResponse findById(UUID id);
    ConnectorResponse create(ConnectorRequest request);
    ConnectorResponse update(UUID id, ConnectorRequest request);
    void delete(UUID id);
}
