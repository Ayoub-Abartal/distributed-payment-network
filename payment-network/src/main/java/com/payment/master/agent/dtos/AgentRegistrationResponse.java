package com.payment.master.agent.dtos;

import com.payment.shared.enums.AgentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRegistrationResponse {

    private String apiKey;
    private AgentStatus status;
    private String message;
}