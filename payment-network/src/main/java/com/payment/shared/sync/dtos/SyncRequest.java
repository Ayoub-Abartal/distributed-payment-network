package com.payment.shared.sync.dtos;

import com.payment.shared.sync.events.CustomerEvent;
import com.payment.shared.sync.events.TransactionEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Deprecated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {

    private String agentId;
    private List<TransactionEvent> transactions;
    private List<CustomerEvent> customers;
}