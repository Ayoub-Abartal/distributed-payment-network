package com.payment.agent.sync.strategy.http;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.payment.master.sync.dtos.SyncResponse;
import com.payment.master.sync.dtos.TransactionSyncRequest;
import com.payment.shared.sync.events.TransactionEvent;
import com.payment.shared.sync.strategy.common.SyncResult;
import com.payment.shared.sync.strategy.common.SyncStrategyType;
import com.payment.shared.sync.strategy.entity.TransactionSyncStrategy;

@Service
@ConditionalOnProperty(name="app.role", havingValue = "agent")
public class HttpTransactionSyncStrategy implements TransactionSyncStrategy {


    private final String TRANSACTION_SYNC_END_POINT  = "/api/master/sync/transactions";

    @Qualifier("syncRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${app.master.url}")
    private String masterUrl;

    @Value("${app.agent.id}")
    private String agentId;

    public HttpTransactionSyncStrategy(
        @Qualifier("syncRestTemplate")RestTemplate restTemplate){
            this.restTemplate = restTemplate;
        }

    
    @Override
    public SyncResult syncBatch(List<TransactionEvent> events) {
        return sendToMaster(events);
    }

    @Override
    public SyncResult syncEntity(TransactionEvent event) {
        return sendToMaster(List.of(event)) ;
    }
    
    private SyncResult sendToMaster(List<TransactionEvent> events){
      
        try{
            TransactionSyncRequest request = TransactionSyncRequest.builder()
                                    .agentId(agentId)
                                    .transactions(events)
                                    .build();

            ResponseEntity<SyncResponse>  responseEntity = restTemplate.postForEntity( 
                masterUrl + TRANSACTION_SYNC_END_POINT,
                request,
                SyncResponse.class);
                
                SyncResponse syncResponse = responseEntity.getBody();

                // Handle null response
                if(syncResponse == null){
                    return SyncResult.builder()
                    .success(false)
                    .message("No response From Master")
                    .usedStrategy(SyncStrategyType.HTTP)
                    .build();
                }

                // convert SyncResponse to syncResult 
                return SyncResult.builder()
                        .success("SUCCESS".equals(syncResponse.getStatus()))
                        .syncedCount(syncResponse.getSynced())
                        .failedCount(syncResponse.getConflicts())
                        .message(syncResponse.getMessage())
                        .syncedIds(syncResponse.getSyncedIds())
                        .failedIds(syncResponse.getFailedIds())
                        .usedStrategy(SyncStrategyType.HTTP)
                        .build();

            }catch(Exception e){
                return SyncResult.builder()
                        .success(false)
                        .failedCount(events.size())
                        .message("HTTP sync failed: " + e.getMessage())
                        .errors(List.of(e.getMessage()))
                        .usedStrategy(SyncStrategyType.HTTP)
                        .build();
            }
    }
}
