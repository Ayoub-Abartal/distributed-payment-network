package com.payment.master.sync.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.TransactionRepository;
import com.payment.shared.mapper.TransactionEventMapper;
import com.payment.shared.sync.events.TransactionEvent;
import com.payment.shared.sync.strategy.common.SyncResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Processes transaction events received from agents.
 * Converts TransactionEvent to Transaction entity and saves to master database.
 */
@Component
@Slf4j
@ConditionalOnProperty(name="app.role",havingValue = "master")
public class TransactionSyncHandler implements EntitySyncHandler<TransactionEvent>{

    private final TransactionRepository transactionRepository;

    public TransactionSyncHandler(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    @Override
    public SyncResult handleBatch(List<TransactionEvent> events) {
        List<String> syncedIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();

        for(TransactionEvent event: events){
            try{
                String id  = handleEntity(event);
                syncedIds.add(id);

            }catch(Exception e){
                String id = extractId(event);
                failedIds.add(id);
                log.error("Failed to handle event {}: {}", id, e.getMessage());

            }
        }
        
        return SyncResult.builder()
                .success(syncedIds.size() > 0)
                .syncedCount(syncedIds.size())
                .failedCount(failedIds.size())
                .syncedIds(syncedIds)
                .failedIds(failedIds)
                .message("Synced "+syncedIds.size()+" transactions, "+failedIds.size() + " failed")
                .build();
    }

    @Override
    public String handleEntity(TransactionEvent event) {
        Transaction transaction = TransactionEventMapper.toEntity(event);
        
        transactionRepository.save(transaction);   
        log.debug("Saved Transactions: {}", transaction.getId());

        return transaction.getId();
    }   


    @Override
    public String extractId(TransactionEvent event) {
        return event.getId();
    }
    
}
