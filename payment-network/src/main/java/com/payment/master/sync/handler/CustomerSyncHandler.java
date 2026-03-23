package com.payment.master.sync.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.payment.shared.domain.entity.Customer;
import com.payment.shared.domain.repositories.CustomerRepository;
import com.payment.shared.mapper.CustomerEventMapper;
import com.payment.shared.sync.events.CustomerEvent;
import com.payment.shared.sync.strategy.common.SyncResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Processes customer events received from agents.
 * Converts CustomerEvent to Customer entity and saves to master database.
 */
@Component
@Slf4j
@ConditionalOnProperty(name="app.role", havingValue = "master")
public class CustomerSyncHandler implements EntitySyncHandler<CustomerEvent>{

    private final CustomerRepository customerRepository;

    public CustomerSyncHandler(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    @Override
    public SyncResult handleBatch(List<CustomerEvent> events) {

        List<String> syncedIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();

        for(CustomerEvent event: events){

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
                .message("Synced "+syncedIds.size()+" customers, "+failedIds.size() + " failed")
                .build();
    }

    @Override
    public String handleEntity(CustomerEvent event) {
        Customer customer = CustomerEventMapper.toEntity(event);
        
        customerRepository.save(customer); 
        log.debug("Saved Customer: {}", customer.getId());
        
        return customer.getId();
    }

    @Override
    public String extractId(CustomerEvent event) {
        return event.getId();
    }
    
}
