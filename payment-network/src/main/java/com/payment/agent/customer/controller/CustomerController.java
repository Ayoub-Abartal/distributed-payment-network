package com.payment.agent.customer.controller;

import com.payment.agent.customer.dtos.CustomerResponse;
import com.payment.agent.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent/customers")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name="app.role", havingValue = "agent")
public class CustomerController {

    private final CustomerService customerService;

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
}
