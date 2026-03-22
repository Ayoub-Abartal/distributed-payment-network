package com.payment.shared.sync.dtos;

import java.util.List;

import com.payment.shared.sync.events.TransactionEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSyncRequest {
    private String agentId;
    List<TransactionEvent> transactions;
}
