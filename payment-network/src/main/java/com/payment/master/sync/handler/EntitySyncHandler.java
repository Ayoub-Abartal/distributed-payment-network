package com.payment.master.sync.handler;

import java.util.List;

import com.payment.shared.sync.events.SyncEvent;
import com.payment.shared.sync.strategy.common.SyncResult;

/**
 * Handles processing of sync events received from agents.
 * Converts events to entities and saves them to the database.
 * 
 * @param <T> the event type (TransactionEvent, CustomerEvent, etc.)
 */
public interface EntitySyncHandler<T extends SyncEvent> {
    
    /**
     * Process a single event: convert to entity and save to database.
     * 
     * @param event the event to process
     * @return ID of the saved entity
     * @throws Exception if save fails
     */
    String handleEntity(T event);
    
    /**
     * Process multiple events in batch.
     * Tracks which entities succeeded and which failed.
     * 
     * @param events list of events to process
     * @return result with synced/failed IDs and counts
     */
    SyncResult handleBatch(List<T> events);

    /**
     * Extract the ID from an event.
     * Used for error tracking when processing fails.
     * 
     * @param event the event
     * @return the event's ID
     */
    String extractId(T event);
}
