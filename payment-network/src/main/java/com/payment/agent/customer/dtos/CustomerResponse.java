package com.payment.agent.customer.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String id;
    private String phoneNumber;
    private String name;
    private Double balance;
    private LocalDateTime createdAt;
    private LocalDateTime lastTransactionAt;
}
