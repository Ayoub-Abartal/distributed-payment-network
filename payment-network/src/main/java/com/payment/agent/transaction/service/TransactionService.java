package com.payment.agent.transaction.service;

import com.payment.agent.transaction.dtos.TransactionRequest;
import com.payment.agent.transaction.dtos.TransactionResponse;

import java.util.List;

public interface TransactionService {

    /**
     * Create a new transaction locally
     * @param request Transaction details
     * @return Created transaction with PENDING_SYNC status
     */
    TransactionResponse createTransaction(TransactionRequest request);

    /**
     * Get all local transactions
     * @return List of transactions
     */
    List<TransactionResponse> getLocalTransactions();
}