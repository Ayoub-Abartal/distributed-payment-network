package com.payment.agent.sync.strategy.kafka;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.payment.shared.sync.events.TransactionEvent;
import com.payment.shared.sync.strategy.common.SyncResult;
import com.payment.shared.sync.strategy.common.SyncStrategyType;
import com.payment.shared.sync.strategy.entity.TransactionSyncStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka-based sync strategy for transactions.
 * Sends transaction events to Kafka topic for async processing by master.
 * 
 * Uses KafkaTemplate to publish events to the transaction-events topic.
 * Partitions messages by agentId to maintain ordering per agent.
 * Waits for Kafka acknowledgment before marking as synced.
 */
@Service("kafkaTransactionSyncStrategy")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name="app.role",havingValue = "agent")
public class KafkaTransactionSyncStrategy implements TransactionSyncStrategy {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private static final String TOPIC = "transaction-events";

    @Override
    public SyncResult syncBatch(List<TransactionEvent> events) {
        return registerEventInKafka(events);
    }

    @Override
    public SyncResult syncEntity(TransactionEvent event) {
        return registerEventInKafka(List.of(event));
    }
    
    /**
     * Sends transaction events to Kafka topic.
     * Uses agentId as partition key to maintain message ordering per agent.
     * Waits for Kafka acknowledgment using .get() for reliability.
     * 
     * @param events list of transaction events to send
     * @return sync result with success/failure tracking
     */
    private SyncResult registerEventInKafka(List<TransactionEvent> events){

        List<String> syncedIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();

        for(TransactionEvent event: events){
            try{
                kafkaTemplate.send(TOPIC, event.getAgentId(), event).get();
                syncedIds.add(event.getId());
            }catch(Exception e ){
                failedIds.add(event.getId());
                log.debug("Kafka send Failed for {} : {}", event.getId(), e.getMessage());
            }
        }     
        
        return SyncResult.builder()
                    .success(failedIds.isEmpty())
                    .failedCount(failedIds.size())
                    .failedIds(failedIds)
                    .syncedCount(syncedIds.size())
                    .syncedIds(syncedIds)
                    .usedStrategy(SyncStrategyType.KAFKA)
                    .build();
        
        
    }
}

