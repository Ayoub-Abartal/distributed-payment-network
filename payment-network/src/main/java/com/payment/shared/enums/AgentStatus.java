package com.payment.shared.enums;

public enum AgentStatus {
    PENDING,    // Agent registered but not yet approved
    ACTIVE,     // Agent is active and can transact
    SUSPENDED,  // Agent temporarily disabled
    REJECTED    // Agent registration was rejected
}