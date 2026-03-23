package com.payment.master.sync.config;

import java.util.ArrayList;
import java.util.List;

import com.payment.shared.sync.strategy.common.EntityType;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.payment.master.sync.handler.EntitySyncHandler;
import com.payment.master.sync.receiver.MasterSyncEntityConfig;
import com.payment.master.sync.receiver.MasterSyncReceiver;

/**
 * Configures master-side sync receiver with auto-discovery of handlers.
 * Automatically finds all EntitySyncHandler beans and builds configs for them.
 * 
 * To add a new entity: just create a handler (e.g., ProductSyncHandler).
 * This config will auto-discover it and wire it up.
 */
@Configuration
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterSyncConfig {
    
    // Spring injects ALL EntitySyncHandler beans automatically!
    private final List<EntitySyncHandler<?>> allHandlers;
    

    public MasterSyncConfig(List<EntitySyncHandler<?>> allHandlers){
        this.allHandlers = allHandlers;
    }

    /**
     * Creates the master sync receiver bean.
     * Auto-discovers all handlers and builds configs for them.
     * 
     * @return configured receiver ready to process sync events
     */
    @Bean
    public MasterSyncReceiver masterSyncReceiver(){
        List<MasterSyncEntityConfig<?>> configs = new ArrayList<>();
        
        for(EntitySyncHandler<?> handler: allHandlers){
            MasterSyncEntityConfig<?> config = buildConfig(handler);
            configs.add(config);
        }

        return new MasterSyncReceiver(configs);
    }

    /**
     * Builds config for a handler by extracting entity type from class name.
     * 
     * @param handler the handler to build config for
     * @return config for this handler
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private MasterSyncEntityConfig buildConfig(EntitySyncHandler handler){
        EntityType entityType = determineEntityType(handler);
        String entityName = extractEntityName(handler);
        
        return MasterSyncEntityConfig.builder()
                    .entityType(entityType)
                    .handler(handler) 
                    .entityName(entityName)
                    .build();
    }

    
    /**
     * Determines entity type from handler class name.
     * TransactionSyncHandler → TRANSACTION
     * CustomerSyncHandler → CUSTOMER
     * 
     * @param handler the handler
     * @return the entity type
     * @throws IllegalStateException if handler type is unknown
     */
    private EntityType determineEntityType(EntitySyncHandler<?> handler) {
        String className = handler.getClass().getSimpleName();
        // "TransactionSyncHandler" → "TRANSACTION"  || "CustomerSyncHandler" → "CUSTOMER"
        
        if (className.contains("Transaction")) {
            return EntityType.TRANSACTION;
        } else if (className.contains("Customer")) {
            return EntityType.CUSTOMER;
        }

        // Add more as needed
        
        throw new IllegalStateException("Unknown handler type: " + className);
    }
    
    /**
     * Extracts entity name from handler class name.
     * TransactionSyncHandler → Transaction
     * 
     * @param handler the handler
     * @return the entity name
     */
    private String extractEntityName(EntitySyncHandler<?> handler) {
        String className = handler.getClass().getSimpleName();
        // "TransactionSyncHandler" → "Transaction"
        return className.replace("SyncHandler", "");
    }
}
