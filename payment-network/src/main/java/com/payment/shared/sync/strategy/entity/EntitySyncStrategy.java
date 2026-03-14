package com.payment.shared.sync.strategy.entity;

import java.util.List;

import com.payment.shared.sync.events.SyncEvent;
import com.payment.shared.sync.strategy.common.SyncResult;

public interface EntitySyncStrategy<T extends SyncEvent> {
     /**
     * Sync a single entity event
     * @param event The event to sync
     * @return SyncResult indicating success/failure
     */
    SyncResult syncEntity(T event);

     /**
     * Sync multiple entity events in batch
     * @param events List of events to sync
     * @return SyncResult with batch statistics
     */
    SyncResult syncBatch(List<T> events);
}
