package com.payment.master.sync.dtos;

import com.payment.shared.domain.entity.Customer;
import com.payment.shared.domain.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {

    private String agentId;
    private List<Transaction> transactions;
    private List<Customer> customers;
}