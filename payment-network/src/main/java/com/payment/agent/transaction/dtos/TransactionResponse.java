package com.payment.agent.transaction.dtos;

import com.payment.shared.enums.SyncStatus;
import com.payment.shared.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private String id;
    private String agentId;
    private String customerPhone;
    private TransactionType type;
    private Double amount;
    private SyncStatus status;
    private LocalDateTime timestamp;
    private String message;
}