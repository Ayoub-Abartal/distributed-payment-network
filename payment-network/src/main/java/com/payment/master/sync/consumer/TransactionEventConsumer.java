package com.payment.master.sync.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.payment.master.sync.handler.TransactionSyncHandler;
import com.payment.shared.sync.events.TransactionEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer for transaction events.
 * Listens to transaction-events topic and processes incoming events.
 * 
 * Uses manual acknowledgment to ensure events are only marked as consumed
 * after successful processing. Reuses existing TransactionSyncHandler for
 * business logic.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class TransactionEventConsumer {
    
    private final  TransactionSyncHandler transactionSyncHandler;
    
    private static final String TOPIC = "transaction-events";
    private static final String GROUP_ID = "master-sync-group";

    /**
     * Consumes transaction events from Kafka topic.
     * Processes each event using the handler and commits offset on success.
     * If processing fails, offset is not committed and Kafka will redeliver.
     * 
     * @param event the transaction event from Kafka
     * @param ack manual acknowledgment for offset commit
     */
    @KafkaListener(
        topics = TOPIC,
        groupId = GROUP_ID
    )
    public void consume(TransactionEvent event, Acknowledgment ack){    
        log.info("Received from Kafka: {} ", event.getId());
       
        try{
        
            transactionSyncHandler.handleEntity(event);
            
            ack.acknowledge();
            log.info("Processed transaction: {}",event.getId());

        }catch(Exception e){
            log.error("Failed to process transaction {}: {}", event.getId(), e.getMessage());
        }
    }
}
