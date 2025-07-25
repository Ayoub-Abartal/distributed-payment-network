package com.payment.shared.domain.entity;

import com.payment.shared.enums.SyncStatus;
import com.payment.shared.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String id;  // UUID

    @Column(nullable = false)
    private String agentId;  // Which agent created this transaction

    @Column(nullable = false)
    private String customerPhone;  // Customer identifier

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;  // DEPOSIT or WITHDRAWAL

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus status;  // PENDING_SYNC, SYNCED, FAILED

    @Column(nullable = false)
    private LocalDateTime timestamp;  // For conflict resolution

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = SyncStatus.PENDING_SYNC;
        }
    }
}