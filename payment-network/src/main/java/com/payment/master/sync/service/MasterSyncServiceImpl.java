package com.payment.master.sync.service;

import com.payment.master.sync.dtos.SyncRequest;
import com.payment.master.sync.dtos.SyncResponse;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.domain.entity.Customer;
import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.AgentRepository;
import com.payment.shared.domain.repositories.CustomerRepository;
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
    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public SyncResponse receiveTransactions(SyncRequest request, String apiKey) {
        int txCount = request.getTransactions() != null ? request.getTransactions().size() : 0;
        int custCount = request.getCustomers() != null ? request.getCustomers().size() : 0;
        log.info("Received sync request from agent: {} with {} transactions and {} customers",
                request.getAgentId(), txCount, custCount);

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
        int customersSynced = 0;

        // Process each transaction
        if (request.getTransactions() != null) {
            for (Transaction transaction : request.getTransactions()) {
                Optional<Transaction> existing = transactionRepository.findById(transaction.getId());

                if (existing.isEmpty()) {
                    // New transaction - save it with SYNCED status
                    transaction.setStatus(SyncStatus.SYNCED);
                    transactionRepository.save(transaction);
                    synced++;
                    log.debug("Saved new transaction: {}", transaction.getId());
                } else {
                    // Conflict - check timestamp (last-write-wins)
                    Transaction existingTx = existing.get();
                    if (transaction.getTimestamp().isAfter(existingTx.getTimestamp())) {
                        transaction.setStatus(SyncStatus.SYNCED);
                        transactionRepository.save(transaction);
                        synced++;
                        log.debug("Updated transaction with newer version: {}", transaction.getId());
                    } else {
                        conflicts++;
                        log.debug("Kept existing transaction (newer): {}", transaction.getId());
                    }
                }
            }
        }

        // Process each customer (upsert based on phone number)
        if (request.getCustomers() != null) {
            for (Customer customer : request.getCustomers()) {
                Optional<Customer> existing = customerRepository.findByPhoneNumber(customer.getPhoneNumber());

                if (existing.isEmpty()) {
                    // New customer
                    customerRepository.save(customer);
                    customersSynced++;
                    log.debug("Saved new customer: {}", customer.getPhoneNumber());
                } else {
                    // Update existing customer (always take latest data)
                    Customer existingCustomer = existing.get();
                    existingCustomer.setName(customer.getName());
                    existingCustomer.setBalance(customer.getBalance());
                    existingCustomer.setLastTransactionAt(customer.getLastTransactionAt());
                    customerRepository.save(existingCustomer);
                    customersSynced++;
                    log.debug("Updated customer: {}", customer.getPhoneNumber());
                }
            }
        }

        // Update agent's last seen timestamp
        agent.setLastSeenAt(LocalDateTime.now());
        agentRepository.save(agent);

        log.info("Sync completed for agent: {}. Synced: {} transactions, {} customers, Conflicts: {}",
                request.getAgentId(), synced, customersSynced, conflicts);

        return SyncResponse.builder()
                .synced(synced)
                .conflicts(conflicts)
                .status(conflicts > 0 ? "PARTIAL_SUCCESS" : "SUCCESS")
                .message(String.format("Synced %d transactions, %d customers, %d conflicts",
                        synced, customersSynced, conflicts))
                .build();
    }
}