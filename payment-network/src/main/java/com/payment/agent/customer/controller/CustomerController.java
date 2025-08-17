package com.payment.agent.customer.controller;

import com.payment.agent.customer.dtos.CreateCustomerRequest;
import com.payment.agent.customer.dtos.CustomerResponse;
import com.payment.agent.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/customers")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name="app.role", havingValue = "agent")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String phoneNumber) {
        log.info("Looking up customer: {}", phoneNumber);

        try {
            CustomerResponse customer = customerService.getCustomerByPhone(phoneNumber);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            log.warn("Customer not found: {}", phoneNumber);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request) {
        log.info("Creating new customer: {}", request.getPhoneNumber());

        try {
            CustomerResponse customer = customerService.createCustomer(
                    request.getPhoneNumber(),
                    request.getName(),
                    request.getInitialBalance()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (RuntimeException e) {
            log.error("Failed to create customer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
