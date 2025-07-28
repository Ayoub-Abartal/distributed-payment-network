package com.payment.agent.sync.service;

public interface AgentSyncService {

    /**
     * Push pending transactions to master
     */
    void pushToMaster();
}