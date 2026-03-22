package com.payment.master.sync.service;

import com.payment.shared.sync.dtos.SyncRequest;
import com.payment.shared.sync.dtos.SyncResponse;

public interface MasterSyncService {

    /**
     * Receive and process transactions from an agent
     * @param request Sync request with agent ID and transactions
     * @param apiKey API key for authentication
     * @return Sync response with status
     */
    @Deprecated
    SyncResponse receiveTransactions(SyncRequest request, String apiKey);  
}