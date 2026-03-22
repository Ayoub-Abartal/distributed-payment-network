package com.payment.agent.sync.strategy.http;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.payment.shared.sync.dtos.CustomerSyncRequest;
import com.payment.shared.sync.dtos.SyncResponse;
import com.payment.shared.sync.events.CustomerEvent;
import com.payment.shared.sync.strategy.common.SyncResult;
import com.payment.shared.sync.strategy.common.SyncStrategyType;
import com.payment.shared.sync.strategy.entity.CustomerSyncStrategy;

@Service
@ConditionalOnProperty(name="app.role", havingValue = "agent")
public class HttpCustomerSyncStrategy implements CustomerSyncStrategy{


    private final String CUSTOMER_SYNC_ENDPOINT = "/api/master/sync/customers";

    @Value("${app.agent.id}")
    private String agentId;

    @Qualifier("syncRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${app.master.url}")
    private String masterUrl;

     public HttpCustomerSyncStrategy( @Qualifier("syncRestTemplate") RestTemplate restTemplate)
    {
            this.restTemplate = restTemplate;
    }

    @Override
    public SyncResult syncBatch(List<CustomerEvent> events) {
        return sendToMaster(events);
    }

    @Override
    public SyncResult syncEntity(CustomerEvent event) {
        return sendToMaster(List.of(event));
    }

    public SyncResult sendToMaster(List<CustomerEvent> events){
        try{
            // build customer request 
            CustomerSyncRequest request = CustomerSyncRequest.builder()
                                                            .agentId(agentId)
                                                            .customers(events)
                                                            .build();
            // post entity from request 
            ResponseEntity<SyncResponse> responseEntity = restTemplate.postForEntity(
                masterUrl + CUSTOMER_SYNC_ENDPOINT,
                request,
                SyncResponse.class);

            SyncResponse syncResponse  = responseEntity.getBody();

            // if null return sync failed 
            if(syncResponse == null ){
                return SyncResult.builder()
                                .success(false)
                                .message("No Response From Master")
                                .usedStrategy(SyncStrategyType.HTTP)
                                .build();
            }

            // if not return reponse with failed and synced counts .... constructing a SyncResult oject 
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
            // return sync failed with exception message 
            return SyncResult.builder()
                            .success(false)
                            .failedCount(events.size())
                            .message("HTTP sync Failed: "+e.getMessage())
                            .errors(List.of(e.getMessage()))
                            .usedStrategy(SyncStrategyType.HTTP)
                            .build();
        }

    }
    
}
