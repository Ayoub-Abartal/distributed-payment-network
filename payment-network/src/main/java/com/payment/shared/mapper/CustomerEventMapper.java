package com.payment.shared.mapper;

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

    public static Customer toEntity(CustomerEvent customerEvent){
        return Customer.builder()
            .id(customerEvent.getId())
            .agentId(customerEvent.getAgentId())
            .phoneNumber(customerEvent.getPhoneNumber())
            .name(customerEvent.getName())
            .balance(customerEvent.getBalance())
            .createdAt(customerEvent.getCreatedAt())
            .lastTransactionAt(customerEvent.getLastTransactionAt())
            .build();
    }
}

