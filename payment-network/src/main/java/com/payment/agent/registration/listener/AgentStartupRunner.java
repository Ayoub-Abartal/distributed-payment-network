package com.payment.agent.registration.listener;

import com.payment.agent.registration.dtos.AgentRegistrationRequest;
import com.payment.agent.registration.service.AgentRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class AgentStartupRunner {

    private final AgentRegistrationService registrationService;

    @Value("${app.agent.id}")
    private String agentId;

    @Value("${app.agent.name}")
    private String agentName;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Agent {} starting up...", agentId);

        // Build registration request
        AgentRegistrationRequest request = AgentRegistrationRequest.builder()
                .agentId(agentId)
                .businessName(agentName)
                .ownerName("Default Owner")  // Can be made configurable
                .phoneNumber("0600000000")   // Can be made configurable
                .location("Morocco")          // Can be made configurable
                .build();

        try {
            String apiKey = registrationService.registerWithMaster(request);
            log.info("Agent registered successfully. API Key stored.");
            // TODO: Store API key in memory for future sync calls
        } catch (Exception e) {
            log.error("Failed to register with master: {}", e.getMessage());
            log.error("Agent will continue running but sync will fail until registered");
        }
    }
}