package com.payment.master.sync.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.payment.master.sync.handler.CustomerSyncHandler;
import com.payment.shared.sync.events.CustomerEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer for customer events.
 * Listens to customer-events topic and processes incoming events.
 * 
 * Uses manual acknowledgment to ensure events are only marked as consumed
 * after successful processing. Reuses existing CustomerSyncHandler for
 * business logic.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class CustomerEventConsumer {
        
    private final  CustomerSyncHandler customerSyncHandler;
    
    private static final String TOPIC = "customer-events";
    private static final String GROUP_ID = "master-sync-group";

    /**
     * Consumes customer events from Kafka topic.
     * Processes each event using the handler and commits offset on success.
     * If processing fails, offset is not committed and Kafka will redeliver.
     * 
     * @param event the customer event from Kafka
     * @param ack manual acknowledgment for offset commit
     */
    @KafkaListener(
        topics = TOPIC,
        groupId = GROUP_ID
    )
    public void consume(CustomerEvent event, Acknowledgment ack){
        log.info("Received From kafka : {}", event.getId());
       
        try {
            customerSyncHandler.handleEntity(event);

            ack.acknowledge();
            
            log.info("Processed customer: {}",event.getId());
            
        } catch (Exception e) {
            log.error("Failed to sync event {}: {}",event.getId(), e.getMessage());
        }
    }
}
