package com.payment.master.sync.service;

import com.payment.master.sync.dtos.SyncRequest;
import com.payment.master.sync.dtos.SyncResponse;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.AgentRepository;
import com.payment.shared.domain.repositories.TransactionRepository;
import com.payment.shared.enums.SyncStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterSyncServiceImpl implements MasterSyncService {

    private final TransactionRepository transactionRepository;
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public SyncResponse receiveTransactions(SyncRequest request, String apiKey) {
        log.info("Received sync request from agent: {} with {} transactions",
                request.getAgentId(), request.getTransactions().size());

        // Validate API key
        Optional<Agent> agentOpt = agentRepository.findByApiKey(apiKey);
        if (agentOpt.isEmpty()) {
            log.warn("Invalid API key provided");
            return SyncResponse.builder()
                    .synced(0)
                    .conflicts(0)
                    .status("FAILED")
                    .message("Invalid API key")
                    .build();
        }

        Agent agent = agentOpt.get();

        // Verify agent ID matches
        if (!agent.getId().equals(request.getAgentId())) {
            log.warn("Agent ID mismatch. Expected: {}, Got: {}", agent.getId(), request.getAgentId());
            return SyncResponse.builder()
                    .synced(0)
                    .conflicts(0)
                    .status("FAILED")
                    .message("Agent ID mismatch")
                    .build();
        }

        int synced = 0;
        int conflicts = 0;

        // Process each transaction
        for (Transaction transaction : request.getTransactions()) {
            Optional<Transaction> existing = transactionRepository.findById(transaction.getId());

            if (existing.isEmpty()) {
                // New transaction - save it with SYNCED status
                transaction.setStatus(SyncStatus.SYNCED); // Add this line
                transactionRepository.save(transaction);
                synced++;
                log.debug("Saved new transaction: {}", transaction.getId());
            } else {
                // Conflict - check timestamp (last-write-wins)
                Transaction existingTx = existing.get();
                if (transaction.getTimestamp().isAfter(existingTx.getTimestamp())) {
                    transaction.setStatus(SyncStatus.SYNCED); // Add this line
                    transactionRepository.save(transaction);
                    synced++;
                    log.debug("Updated transaction with newer version: {}", transaction.getId());
                } else {
                    conflicts++;
                    log.debug("Kept existing transaction (newer): {}", transaction.getId());
                }
            }
        }

        // Update agent's last seen timestamp
        agent.setLastSeenAt(LocalDateTime.now());
        agentRepository.save(agent);

        log.info("Sync completed for agent: {}. Synced: {}, Conflicts: {}",
                request.getAgentId(), synced, conflicts);

        return SyncResponse.builder()
                .synced(synced)
                .conflicts(conflicts)
                .status(conflicts > 0 ? "PARTIAL_SUCCESS" : "SUCCESS")
                .message(String.format("Synced %d transactions, %d conflicts", synced, conflicts))
                .build();
    }
}