package com.payment.master.agent.controller;

import com.payment.agent.registration.dtos.AgentRegistrationRequest;
import com.payment.master.agent.dtos.AgentRegistrationResponse;
import com.payment.master.agent.service.MasterAgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master/agents")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterAgentController {

    private final MasterAgentService masterAgentService;

    @PostMapping("/register")
    public ResponseEntity<AgentRegistrationResponse> registerAgent(
            @Valid @RequestBody AgentRegistrationRequest request) {

        log.info("Registration request received for agent: {}", request.getAgentId());

        AgentRegistrationResponse response = masterAgentService.registerAgent(request);

        return ResponseEntity.ok(response);
    }
}