package com.payment.master.agent.service;

import com.payment.agent.registration.dtos.AgentRegistrationRequest;
import com.payment.master.agent.dtos.AgentRegistrationResponse;

public interface MasterAgentService {

    /**
     * Register a new agent and generate API key
     * @param request Agent registration details
     * @return Registration response with API key
     */
    AgentRegistrationResponse registerAgent(AgentRegistrationRequest request);
}