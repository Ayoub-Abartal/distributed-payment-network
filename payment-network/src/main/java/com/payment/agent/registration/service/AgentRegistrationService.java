package com.payment.agent.registration.service;

import com.payment.agent.registration.dtos.AgentRegistrationRequest;

public interface AgentRegistrationService {

    /**
     * Register this agent with the master server
     * @param request Registration details
     * @return API key received from master
     */
    String registerWithMaster(AgentRegistrationRequest request);
}