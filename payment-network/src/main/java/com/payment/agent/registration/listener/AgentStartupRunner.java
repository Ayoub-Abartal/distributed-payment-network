package com.payment.agent.registration.listener;

import com.payment.agent.registration.dtos.AgentRegistrationRequest;
import com.payment.agent.registration.service.AgentRegistrationService;
import com.payment.agent.sync.service.AgentSyncServiceImpl;
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
    private final AgentSyncServiceImpl agentSyncService; // Changed to implementation to call setApiKey

    @Value("${app.agent.id}")
    private String agentId;

    @Value("${app.agent.name}")
    private String agentName;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("🚀 Agent {} starting up...", agentId);

        // Build registration request
        AgentRegistrationRequest request = AgentRegistrationRequest.builder()
                .agentId(agentId)
                .businessName(agentName)
                .ownerName("Default Owner")
                .phoneNumber("0600000000")
                .location("Morocco")
                .build();

        try {
            String apiKey = registrationService.registerWithMaster(request);
            log.info("✅ Agent registered successfully. API Key stored.");

            // Store API key for sync operations
            agentSyncService.setApiKey(apiKey);
            log.info("✅ API Key configured for sync operations");

        } catch (Exception e) {
            log.error("❌ Failed to register with master: {}", e.getMessage());
            log.error("Agent will continue running but sync will fail until registered");
        }
    }
}