package com.payment.agent.customer.service;

import com.payment.agent.customer.dtos.CustomerResponse;
import com.payment.shared.domain.entity.Customer;
import com.payment.shared.domain.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponse getOrCreateCustomer(String phoneNumber, String name) {
        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    log.info("Creating new customer with phone: {}", phoneNumber);
                    Customer newCustomer = Customer.builder()
                            .phoneNumber(phoneNumber)
                            .name(name)
                            .balance(0.0)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return customerRepository.save(newCustomer);
                });

        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByPhone(String phoneNumber) {
        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + phoneNumber));

        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public void updateBalance(String phoneNumber, Double amount) {
        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + phoneNumber));

        customer.setBalance(customer.getBalance() + amount);
        customer.setLastTransactionAt(LocalDateTime.now());
        customerRepository.save(customer);

        log.info("Updated balance for {}: {} (change: {})", phoneNumber, customer.getBalance(), amount);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(String phoneNumber, String name, Double initialBalance) {
        // Check if customer already exists
        if (customerRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new RuntimeException("Customer already exists with phone: " + phoneNumber);
        }

        log.info("Creating new customer: {} with initial balance: {}", phoneNumber, initialBalance);
        Customer newCustomer = Customer.builder()
                .phoneNumber(phoneNumber)
                .name(name)
                .balance(initialBalance != null ? initialBalance : 0.0)
                .createdAt(LocalDateTime.now())
                .build();

        Customer saved = customerRepository.save(newCustomer);
        return mapToResponse(saved);
    }

    @Override
    public boolean hasBalance(String phoneNumber, Double amount) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(customer -> customer.getBalance() >= amount)
                .orElse(false);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .phoneNumber(customer.getPhoneNumber())
                .name(customer.getName())
                .balance(customer.getBalance())
                .createdAt(customer.getCreatedAt())
                .lastTransactionAt(customer.getLastTransactionAt())
                .build();
    }
}
