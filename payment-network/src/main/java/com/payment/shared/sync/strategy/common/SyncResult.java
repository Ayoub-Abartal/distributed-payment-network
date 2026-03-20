package com.payment.shared.sync.strategy.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResult{

    private boolean success;
    private int syncedCount;
    private int failedCount;
    private String message;
    private SyncStrategyType usedStrategy;
    private List<String> errors;
    private List<String> syncedIds;
    private List<String> failedIds;

}