package com.payment.agent.transaction.service;

import com.payment.agent.customer.service.CustomerService;
import com.payment.agent.transaction.dtos.TransactionRequest;
import com.payment.agent.transaction.dtos.TransactionResponse;
import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.TransactionRepository;
import com.payment.shared.enums.SyncStatus;
import com.payment.shared.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;

    @Value("${app.agent.id}")
    private String agentId;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Creating transaction for customer: {}, type: {}, amount: {}",
                request.getCustomerPhone(), request.getType(), request.getAmount());

        // Get or create customer
        customerService.getOrCreateCustomer(request.getCustomerPhone(), "Customer");

        // Validate withdrawal has sufficient balance
        if (request.getType() == TransactionType.WITHDRAWAL) {
            if (!customerService.hasBalance(request.getCustomerPhone(), request.getAmount())) {
                throw new RuntimeException("Insufficient balance for withdrawal");
            }
        }

        // Update customer balance
        Double balanceChange = request.getType() == TransactionType.DEPOSIT
                ? request.getAmount()
                : -request.getAmount();
        customerService.updateBalance(request.getCustomerPhone(), balanceChange);

        // Create transaction entity
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .agentId(agentId)
                .customerPhone(request.getCustomerPhone())
                .type(request.getType())
                .amount(request.getAmount())
                .status(SyncStatus.PENDING_SYNC)
                .timestamp(LocalDateTime.now())
                .build();

        // Save to local database
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction created successfully: {}", savedTransaction.getId());

        // Convert to response DTO
        return toResponse(savedTransaction, "Transaction created successfully. Pending sync to master.");
    }

    @Override
    public List<TransactionResponse> getLocalTransactions() {
        log.info("Fetching all local transactions for agent: {}", agentId);

        List<Transaction> transactions = transactionRepository.findByAgentId(agentId);

        return transactions.stream()
                .map(tx -> toResponse(tx, null))
                .collect(Collectors.toList());
    }

    private TransactionResponse toResponse(Transaction transaction, String message) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .agentId(transaction.getAgentId())
                .customerPhone(transaction.getCustomerPhone())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .timestamp(transaction.getTimestamp())
                .message(message)
                .build();
    }
}