package com.payment.agent.sync.service;

import com.payment.shared.domain.entity.Customer;
import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.CustomerRepository;
import com.payment.shared.domain.repositories.TransactionRepository;
import com.payment.shared.enums.SyncStatus;
import com.payment.shared.sync.events.CustomerEvent;
import com.payment.shared.sync.events.TransactionEvent;
import com.payment.shared.sync.strategy.common.EntityType;
import com.payment.shared.sync.strategy.common.SyncStrategyType;
import com.payment.agent.sync.config.ApiKeyHolder;
import com.payment.agent.sync.mapper.CustomerEventMapper;
import com.payment.agent.sync.mapper.TransactionEventMapper;
import com.payment.agent.sync.orchestrator.AgentSyncEntityConfig;
import com.payment.agent.sync.orchestrator.AgentSyncOrchestrator;
import com.payment.agent.sync.strategy.factory.AgentSyncStrategyFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class AgentSyncServiceImpl implements AgentSyncService {

    private final AgentSyncStrategyFactory syncStrategyFactory;
    private final AgentSyncOrchestrator syncOrchestrator;

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    private final ApiKeyHolder apiKeyHolder;

    public AgentSyncServiceImpl(
        TransactionRepository transactionRepository,
        CustomerRepository customerRepository,
        ApiKeyHolder apiKeyHolder,
        AgentSyncStrategyFactory syncStrategyFactory
    ){
        this.apiKeyHolder = apiKeyHolder;

        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        
        this.syncStrategyFactory = syncStrategyFactory;

        List<AgentSyncEntityConfig<?>> configs = new ArrayList<>();

        // build transaction Config 
        AgentSyncEntityConfig<TransactionEvent> transactionSyncConfig = this.setupTransactionConfig();

        // build Customer config 
        AgentSyncEntityConfig<CustomerEvent> customerSyncConfig = this.setupCustomerConfig();
    
        // Add configs

        configs.add(transactionSyncConfig);
        configs.add(customerSyncConfig);

        // create orchestrator 
        this.syncOrchestrator = new AgentSyncOrchestrator(configs);

    }

    @Override
    public void pushToMaster() {
        if (apiKeyHolder.getApiKey() == null) {
            log.warn("No API key available. Cannot sync. Agent may not be registered.");
            return;
        }
        syncOrchestrator.syncAll();
    }

    // Returns Customer Sync Config  
    private AgentSyncEntityConfig<CustomerEvent> setupCustomerConfig(){
              
        return AgentSyncEntityConfig.<CustomerEvent>builder()
                .entityName("Customer")
                .order(1)
                .strategy(syncStrategyFactory.getStrategy(EntityType.CUSTOMER, SyncStrategyType.HTTP))
                .eventSupplier(
                () -> {
                    List<Customer> customers = customerRepository.findAll();
                    
                    List<CustomerEvent> customerEvents = customers.stream()
                        .map(CustomerEventMapper::toEvent)
                        .toList();

                        return customerEvents;
                }
                )
                .resultHandler(
                    (syncResponse) -> {
                        if(syncResponse.isSuccess()){
                            log.info(" Successfully synced {} Customers. Response: {}",
                                            syncResponse.getSyncedCount(), 
                                            syncResponse.getMessage()
                                        );
                        }else{
                            log.error("Failed Syncing  {} Customers. Response: {} ",
                                    syncResponse.getFailedCount(),
                                    syncResponse.getMessage()
                            );
                        }
                    }
                )
                .build();
    }

    
    // Returns Transaction Sync Config 
    private AgentSyncEntityConfig<TransactionEvent> setupTransactionConfig(){
        
        return AgentSyncEntityConfig.<TransactionEvent>builder()
                .entityName("Transaction")
                .order(2)
                .strategy(syncStrategyFactory.getStrategy(EntityType.TRANSACTION, SyncStrategyType.HTTP))
                .eventSupplier(
                () ->{
                    
                    // Get Pending Transactions
                    List<Transaction> pendingTransactions = transactionRepository.findByStatus(SyncStatus.PENDING_SYNC);

                    // Convert to events using mapper 
                    List<TransactionEvent> events = pendingTransactions
                                .stream()
                                .map(TransactionEventMapper::toEvent)
                                .toList();

                    return events;
                } 
                )
                .resultHandler(
                    (syncResponse) -> {
                        if(syncResponse.isSuccess()){

                            // Get Synced Ids
                            List<String> syncedIds = syncResponse.getSyncedIds();
                            
                            // find those transactions 
                            List<Transaction> syncedTransactions = transactionRepository.findAllById(syncedIds);

                            // Update Status with sync
                            syncedTransactions.forEach(tx -> tx.setStatus(SyncStatus.SYNCED));
                                            transactionRepository.saveAll(syncedTransactions);
                            
                            log.info(" Successfully synced {} transactions. Response: {}",
                                        syncResponse.getSyncedCount(), 
                                        syncResponse.getMessage()
                                    );

                        }else{
                            log.error("Failed Syncing {} transactions . Response: {}",
                                    syncResponse.getFailedCount(),
                                    syncResponse.getMessage()
                            );
                        }
                    }
                )
                .build();
    }

}