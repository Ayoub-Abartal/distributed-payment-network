package com.payment.master.sync.receiver;

import com.payment.shared.sync.strategy.common.EntityType ;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.payment.master.sync.handler.EntitySyncHandler;
import com.payment.shared.sync.events.SyncEvent;

/**
 * Configuration for processing a specific entity type on master side.
 * Tells the receiver which handler to use for which entity type.
 * 
 * @param <T> the event type (TransactionEvent, CustomerEvent, etc.)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasterSyncEntityConfig<T extends SyncEvent> {
     private EntityType entityType;
     private EntitySyncHandler<T> handler;
     private String entityName;


}
