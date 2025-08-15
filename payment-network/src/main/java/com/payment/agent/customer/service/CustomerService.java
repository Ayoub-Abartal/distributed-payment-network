package com.payment.agent.customer.service;

import com.payment.agent.customer.dtos.CustomerResponse;

public interface CustomerService {

    CustomerResponse getOrCreateCustomer(String phoneNumber, String name);

    CustomerResponse getCustomerByPhone(String phoneNumber);

    void updateBalance(String phoneNumber, Double amount);

    boolean hasBalance(String phoneNumber, Double amount);
}
