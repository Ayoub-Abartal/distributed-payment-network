package com.payment.agent.sync.service;

import com.payment.master.sync.dtos.SyncRequest;
import com.payment.master.sync.dtos.SyncResponse;
import com.payment.shared.domain.entity.Customer;
import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.CustomerRepository;
import com.payment.shared.domain.repositories.TransactionRepository;
import com.payment.shared.enums.SyncStatus;
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

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class AgentSyncServiceImpl implements AgentSyncService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;

    @Value("${app.agent.id}")
    private String agentId;

    @Value("${app.master.url}")
    private String masterUrl;

    private String apiKey; // Stored after registration

    // TODO: This should be set from AgentStartupRunner after registration
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void pushToMaster() {
        if (apiKey == null) {
            log.warn("No API key available. Cannot sync. Agent may not be registered.");
            return;
        }

        // Get pending transactions
        List<Transaction> pending = transactionRepository.findByStatus(SyncStatus.PENDING_SYNC);

        // Get all customers (always sync latest customer data)
        List<Customer> customers = customerRepository.findAll();

        // Always sync to update last_seen_at, even with no transactions
        if (pending.isEmpty() && customers.isEmpty()) {
            log.debug("No data to sync - sending heartbeat to master");
        } else {
            log.info("Pushing {} transactions and {} customers to master", pending.size(), customers.size());
        }

        try {
            // Build sync request
            SyncRequest syncRequest = SyncRequest.builder()
                    .agentId(agentId)
                    .transactions(pending)
                    .customers(customers)
                    .build();

            // Set headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-Key", apiKey);

            HttpEntity<SyncRequest> entity = new HttpEntity<>(syncRequest, headers);

            // Send to master
            ResponseEntity<SyncResponse> response = restTemplate.postForEntity(
                    masterUrl + "/api/master/sync/receive",
                    entity,
                    SyncResponse.class
            );

            SyncResponse syncResponse = response.getBody();

            if (syncResponse != null && "SUCCESS".equals(syncResponse.getStatus())
                    || "PARTIAL_SUCCESS".equals(syncResponse.getStatus())) {

                // Mark transactions as synced
                pending.forEach(tx -> tx.setStatus(SyncStatus.SYNCED));
                transactionRepository.saveAll(pending);

                log.info("✅ Successfully synced {} transactions. Response: {}",
                        syncResponse.getSynced(), syncResponse.getMessage());
            } else {
                log.error("❌ Sync failed: {}", syncResponse != null ? syncResponse.getMessage() : "Unknown error");
            }

        } catch (Exception e) {
            log.error("❌ Failed to sync transactions: {}", e.getMessage());
        }
    }
}