package com.payment.agent.sync.strategy.kafka;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.payment.shared.sync.events.CustomerEvent;
import com.payment.shared.sync.strategy.common.SyncResult;
import com.payment.shared.sync.strategy.common.SyncStrategyType;
import com.payment.shared.sync.strategy.entity.CustomerSyncStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka-based sync strategy for customers.
 * Sends customer events to Kafka topic for async processing by master.
 * 
 * Uses KafkaTemplate to publish events to the customer-events topic.
 * Partitions messages by agentId to maintain ordering per agent.
 * Waits for Kafka acknowledgment before marking as synced.
 */
@Service("kafkaCustomerSyncStrategy")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name="app.role",havingValue = "agent")
public class KafkaCustomerSyncStrategy implements CustomerSyncStrategy {

    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    private static final String TOPIC = "customer-events";

    @Override
    public SyncResult syncBatch(List<CustomerEvent> events) {
        return registerEventInKafka(events);
    }

    @Override
    public SyncResult syncEntity(CustomerEvent event) {
        return registerEventInKafka(List.of(event));
    }
    
    /**
     * Sends customer events to Kafka topic.
     * Uses agentId as partition key to maintain message ordering per agent.
     * Waits for Kafka acknowledgment using .get() for reliability.
     * 
     * @param events list of customer events to send
     * @return sync result with success/failure tracking
     */
    private SyncResult registerEventInKafka(List<CustomerEvent> events){

        List<String> syncedIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();

        for(CustomerEvent event: events){
            try{
                kafkaTemplate.send(TOPIC, event.getAgentId(),event).get();
                syncedIds.add(event.getId());
            }catch(Exception e){
                failedIds.add(event.getId());
                log.debug("Kafka failed for {} : {}", event.getId(), e.getMessage());
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
