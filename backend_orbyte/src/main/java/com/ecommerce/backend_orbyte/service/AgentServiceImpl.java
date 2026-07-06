package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.common.exception.ResourceNotFoundException;
import com.ecommerce.backend_orbyte.dto.request.AgentRequest;
import com.ecommerce.backend_orbyte.dto.response.AgentResponse;
import com.ecommerce.backend_orbyte.entity.Agent;
import com.ecommerce.backend_orbyte.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    @Override
    public List<AgentResponse> findAll() {
        return agentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AgentResponse findById(UUID id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent", "id", id));
        return toResponse(agent);
    }

    @Override
    public AgentResponse create(AgentRequest request) {
        Agent agent = Agent.builder()
                .name(request.getName())
                .description(request.getDescription())
                .model(request.getModel())
                .systemPrompt(request.getSystemPrompt())
                .active(true)
                .build();
        return toResponse(agentRepository.save(agent));
    }

    @Override
    public AgentResponse update(UUID id, AgentRequest request) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent", "id", id));
        agent.setName(request.getName());
        agent.setDescription(request.getDescription());
        agent.setModel(request.getModel());
        agent.setSystemPrompt(request.getSystemPrompt());
        return toResponse(agentRepository.save(agent));
    }

    @Override
    public void delete(UUID id) {
        if (!agentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agent", "id", id);
        }
        agentRepository.deleteById(id);
    }

    private AgentResponse toResponse(Agent agent) {
        return AgentResponse.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .model(agent.getModel())
                .systemPrompt(agent.getSystemPrompt())
                .active(agent.isActive())
                .createdAt(agent.getCreatedAt())
                .build();
    }
}
