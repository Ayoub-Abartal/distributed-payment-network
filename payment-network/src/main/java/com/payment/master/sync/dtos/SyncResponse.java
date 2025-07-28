package com.payment.master.sync.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse {

    private int synced;           // Number of transactions synced
    private int conflicts;        // Number of conflicts detected
    private String status;        // SUCCESS, PARTIAL_SUCCESS, FAILED
    private String message;       // Human-readable message
}