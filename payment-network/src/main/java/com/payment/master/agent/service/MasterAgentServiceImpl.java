package com.payment.master.agent.service;

import com.payment.agent.registration.dtos.AgentRegistrationRequest;
import com.payment.master.agent.dtos.AgentRegistrationResponse;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.enums.AgentStatus;
import com.payment.shared.domain.repositories.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterAgentServiceImpl implements MasterAgentService {

    private final AgentRepository agentRepository;

    @Override
    public AgentRegistrationResponse registerAgent(AgentRegistrationRequest request) {
        log.info("Received registration request from agent: {}", request.getAgentId());

        // Check if agent already exists
        if (agentRepository.existsById(request.getAgentId())) {
            log.warn("Agent {} already registered", request.getAgentId());
            Agent existing = agentRepository.findById(request.getAgentId()).get();
            return AgentRegistrationResponse.builder()
                    .apiKey(existing.getApiKey())
                    .status(existing.getStatus())
                    .message("Agent already registered")
                    .build();
        }

        // Generate API key
        String apiKey = UUID.randomUUID().toString();

        // Create agent entity
        Agent agent = Agent.builder()
                .id(request.getAgentId())
                .businessName(request.getBusinessName())
                .ownerName(request.getOwnerName())
                .phoneNumber(request.getPhoneNumber())
                .location(request.getLocation())
                .apiKey(apiKey)
                .status(AgentStatus.ACTIVE)
                .registeredAt(LocalDateTime.now())
                .build();

        // Save to database
        agentRepository.save(agent);

        log.info("Agent {} registered successfully with API key", request.getAgentId());

        return AgentRegistrationResponse.builder()
                .apiKey(apiKey)
                .status(AgentStatus.ACTIVE)
                .message("Registration successful")
                .build();
    }
}