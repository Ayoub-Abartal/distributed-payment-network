package com.payment.master.sync.receiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.payment.shared.sync.dtos.SyncResponse;
import com.payment.shared.sync.events.SyncEvent;
import com.payment.shared.sync.strategy.common.EntityType;
import com.payment.shared.sync.strategy.common.SyncResult;

import org.springframework.stereotype.Component;

import com.payment.master.sync.handler.EntitySyncHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Routes incoming sync events to the correct handler based on entity type.
 * Master-side equivalent of AgentSyncOrchestrator.
 * 
 * Receives events from agents and delegates processing to entity-specific handlers.
 */
@Component
@Slf4j
public class MasterSyncReceiver {
    
    private Map<EntityType,EntitySyncHandler<?>> handlerRegistry;

    /**
     * Creates receiver with entity configurations.
     * Builds a registry mapping entity types to their handlers.
     * 
     * @param configs list of entity configurations
     */
    public MasterSyncReceiver(List<MasterSyncEntityConfig<?>> configs){

        handlerRegistry = new HashMap<>();

        for(MasterSyncEntityConfig<?> config : configs ){
            handlerRegistry.put(config.getEntityType(), config.getHandler());
        }

    }

    /**
     * Receives and processes sync events for a specific entity type.
     * Routes to the appropriate handler and returns the result.
     * 
     * @param <T> the event type
     * @param entityType which entity type (TRANSACTION, CUSTOMER, etc.)
     * @param events list of events to process
     * @return sync response with success/failure details
     * @throws IllegalStateException if no handler exists for entity type
     */
    @SuppressWarnings("unchecked")
    public <T extends SyncEvent> SyncResponse receive(
                        EntityType entityType,
                        List<T> events){
            
            EntitySyncHandler<T> handler = (EntitySyncHandler<T>) handlerRegistry.get(entityType);
            
            if(handler == null){
                throw new IllegalStateException("No handler found for " + entityType);
            }

            SyncResult result = handler.handleBatch(events);

            SyncResponse response = toSyncResponse(result);
            
            return response;

    }
    
    /**
     * Converts handler result to HTTP response format.
     * 
     * @param syncResult the result from handler
     * @return sync response for agent
     */
    private SyncResponse toSyncResponse(SyncResult syncResult){
        String status ;
       
        if(syncResult.getFailedCount() == 0){
            status = "SUCCESS";
         } else if( syncResult.getSyncedCount() > 0 ){
            status = "PARTIAL_SUCCESS";
         } else {
            status="FAILED";
         }
        
        return SyncResponse.builder()
                .status(status)
                .synced(syncResult.getSyncedCount())
                .conflicts(syncResult.getFailedCount())
                .syncedIds(syncResult.getSyncedIds())
                .failedIds(syncResult.getFailedIds())
                .message(syncResult.getMessage())
                .build();
    }
}
