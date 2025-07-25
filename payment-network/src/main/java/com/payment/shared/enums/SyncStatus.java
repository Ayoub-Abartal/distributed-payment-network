package com.payment.shared.enums;

public enum SyncStatus {
    PENDING_SYNC,  // Transaction created locally, waiting to sync
    SYNCED,        // Successfully synced with master
    FAILED         // Sync failed (e.g., conflict, validation error)
}