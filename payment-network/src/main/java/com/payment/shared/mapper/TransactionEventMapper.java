package com.payment.shared.mapper;

import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.sync.events.TransactionEvent;

public class TransactionEventMapper {
    
    public static TransactionEvent toEvent(Transaction transaction){
        return TransactionEvent.builder()
            .id(transaction.getId())
            .agentId(transaction.getAgentId())
            .type(transaction.getType())
            .customerPhone(transaction.getCustomerPhone())
            .amount(transaction.getAmount())
            .timestamp(transaction.getTimestamp())
            .eventType("TRANSACTION_CREATED")
            .build();
    } 

       
    public static Transaction toEntity(TransactionEvent transactionEvent){
        return Transaction.builder()
            .id(transactionEvent.getId())
            .agentId(transactionEvent.getAgentId())
            .type(transactionEvent.getType())
            .customerPhone(transactionEvent.getCustomerPhone())
            .amount(transactionEvent.getAmount())
            .timestamp(transactionEvent.getTimestamp())
            .build();
    }
}
