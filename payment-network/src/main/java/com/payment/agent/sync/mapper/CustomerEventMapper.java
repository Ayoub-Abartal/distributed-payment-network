package com.payment.agent.sync.mapper;

import com.payment.shared.domain.entity.Customer;

import com.payment.shared.sync.events.CustomerEvent;

public class CustomerEventMapper {
      
    public static CustomerEvent toEvent(Customer customer){
        return CustomerEvent.builder()
            .id(customer.getId())
            .agentId(customer.getAgentId())
            .phoneNumber(customer.getPhoneNumber())
            .name(customer.getName())
            .balance(customer.getBalance())
            .createdAt(customer.getCreatedAt())
            .lastTransactionAt(customer.getLastTransactionAt())
            .timestamp(customer.getCreatedAt())
            .eventType("CUSTOMER_CREATED")
            .build();
    } 
}

