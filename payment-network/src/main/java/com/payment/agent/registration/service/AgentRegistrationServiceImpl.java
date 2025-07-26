package com.payment.agent.registration.service;

import com.payment.agent.registration.dtos.AgentRegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class AgentRegistrationServiceImpl implements AgentRegistrationService {

    private final RestTemplate restTemplate;

    @Value("${app.master.url}")
    private String masterUrl;

    @Override
    public String registerWithMaster(AgentRegistrationRequest request) {
        log.info("Registering agent {} with master at {}", request.getAgentId(), masterUrl);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AgentRegistrationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    masterUrl + "/api/master/agents/register",
                    entity,
                    String.class
            );

            String apiKey = response.getBody();
            log.info("Successfully registered with master. API Key received.");

            return apiKey;

        } catch (Exception e) {
            log.error("Failed to register with master: {}", e.getMessage());
            throw new RuntimeException("Agent registration failed", e);
        }
    }
}