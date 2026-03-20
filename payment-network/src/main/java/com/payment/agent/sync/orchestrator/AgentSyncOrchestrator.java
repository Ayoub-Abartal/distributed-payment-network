package com.payment.agent.sync.orchestrator;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.payment.shared.sync.events.SyncEvent;
import com.payment.shared.sync.strategy.common.SyncResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates the synchronization of multiple entity types to the master node.
 * 
 * This orchestrator coordinates sync operations across different entity types
 * (e.g., Customer, Transaction) by executing them in a defined order and handling
 * dependencies between entities.
 * 
 * Key responsibilities:
 * 
 *   - Sorts entity configs by order to ensure proper sync sequence  
 *   - Fetches events for each entity using configured suppliers  
 *   - Delegates sync execution to entity-specific strategies  
 *   - Handles sync results through configured result handlers  
 *   - Stops sync chain on failure to prevent orphaned data  
 * 
 * Example usage:
 * 
 * AgentSyncOrchestrator orchestrator = new AgentSyncOrchestrator(configs);
 * orchestrator.syncAll(); // Syncs all configured entities in order
 * 
 * @see AgentSyncEntityConfig
 * @see SyncResult
 */
@Slf4j
@Component
public class AgentSyncOrchestrator {

    private final List<AgentSyncEntityConfig<?>> syncConfigs;

    /**
     * Constructs an orchestrator with the given entity configurations.
     * 
     * Configurations are automatically sorted by their order field to ensure
     * entities sync in the correct sequence (e.g., Customer before Transaction).
     * 
     * @param syncConfigs list of entity sync configurations
     */
    public AgentSyncOrchestrator(List<AgentSyncEntityConfig<?>> syncConfigs) {
        this.syncConfigs = syncConfigs.stream()
                            .sorted(Comparator.comparingInt(AgentSyncEntityConfig::getOrder))
                            .toList();
    }

    /**
     * Executes synchronization for all configured entities in order.
     * 
     * For each entity configuration:
     * 
     *   - Fetches events using the configured supplier
     *   - Skips if no events are available
     *   - Syncs events using the configured strategy
     *   - Handles the result using the configured handler
     *   - Stops the sync chain if sync fails (dependency handling)
     * 
     * If any entity sync fails, subsequent entities are not synced to prevent
     * data inconsistencies (e.g., transactions without customers)
     */
    public void syncAll() {
        for (AgentSyncEntityConfig<?> config : syncConfigs) {
            log.info("Syncing {}...", config.getEntityName());

            // Fetch events using supplier 
            List<? extends SyncEvent> events = config.getEventSupplier().get();
            
            // Skip if no events 
            if (events.isEmpty()) {
                log.debug("No {} to sync", config.getEntityName());
                continue;
            }

            // Sync using passed strategy   
            SyncResult result = syncEntity(config, events);
            
            // Handle result 
            config.getResultHandler().accept(result);

            // Stop on failure 
            if (!result.isSuccess()) {
                log.error("{} sync failed. Stopping.", config.getEntityName());
                break;
            }
        }
        
        log.info("Sync cycle complete");
    }

    /**
     * Helper method to perform type-safe sync operation.
     * 
     * This method handles the generic type casting required to call the
     * strategy's syncBatch method with the correct event type.
     * 
     * @param <T> the specific event type (e.g., TransactionEvent, CustomerEvent)
     * @param config the entity configuration containing the strategy
     * @param events the list of events to sync
     * @return the sync result containing success/failure information
     */
    @SuppressWarnings("unchecked")
    private <T extends SyncEvent> SyncResult syncEntity(
        AgentSyncEntityConfig<T> config,
        List<? extends SyncEvent> events
    ) {
        return config.getStrategy().syncBatch((List<T>) events);
    }
}
