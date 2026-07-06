package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.common.exception.ResourceNotFoundException;
import com.ecommerce.backend_orbyte.dto.request.ConnectorRequest;
import com.ecommerce.backend_orbyte.dto.response.ConnectorResponse;
import com.ecommerce.backend_orbyte.entity.Connector;
import com.ecommerce.backend_orbyte.repository.ConnectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectorServiceImpl implements ConnectorService {

    private final ConnectorRepository connectorRepository;

    @Override
    public List<ConnectorResponse> findAll() {
        return connectorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ConnectorResponse findById(UUID id) {
        Connector connector = connectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Connector", "id", id));
        return toResponse(connector);
    }

    @Override
    public ConnectorResponse create(ConnectorRequest request) {
        Connector connector = Connector.builder()
                .name(request.getName())
                .type(com.ecommerce.backend_orbyte.entity.ConnectorType.valueOf(request.getType().toUpperCase().trim().replace(" ", "_").replace("-", "_")))
                .config(request.getConfig())
                .active(true)
                .build();
        return toResponse(connectorRepository.save(connector));
    }

    @Override
    public ConnectorResponse update(UUID id, ConnectorRequest request) {
        Connector connector = connectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Connector", "id", id));
        connector.setName(request.getName());
        connector.setType(com.ecommerce.backend_orbyte.entity.ConnectorType.valueOf(request.getType().toUpperCase().trim().replace(" ", "_").replace("-", "_")));
        connector.setConfig(request.getConfig());
        return toResponse(connectorRepository.save(connector));
    }

    @Override
    public void delete(UUID id) {
        if (!connectorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Connector", "id", id);
        }
        connectorRepository.deleteById(id);
    }

    private ConnectorResponse toResponse(Connector connector) {
        return ConnectorResponse.builder()
                .id(connector.getId())
                .name(connector.getName())
                .type(connector.getType().name())
                .status(connector.getStatus() != null ? connector.getStatus().name() : null)
                .docsIndexed(connector.getDocsIndexed())
                .lastSync(connector.getLastSync())
                .config(connector.getConfig())
                .active(connector.isActive())
                .createdAt(connector.getCreatedAt())
                .build();
    }
}
