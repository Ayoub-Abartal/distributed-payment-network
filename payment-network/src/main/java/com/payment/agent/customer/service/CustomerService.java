package com.payment.agent.customer.service;

import com.payment.agent.customer.dtos.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse getOrCreateCustomer(String phoneNumber, String name);

    CustomerResponse getCustomerByPhone(String phoneNumber);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse createCustomer(String phoneNumber, String name, Double initialBalance);

    void updateBalance(String phoneNumber, Double amount);

    boolean hasBalance(String phoneNumber, Double amount);
}
