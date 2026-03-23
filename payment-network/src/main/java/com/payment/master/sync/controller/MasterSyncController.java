package com.payment.master.sync.controller;

import com.payment.master.sync.receiver.MasterSyncReceiver;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.domain.repositories.AgentRepository;
import com.payment.shared.sync.dtos.CustomerSyncRequest;
import com.payment.shared.sync.dtos.SyncResponse;
import com.payment.shared.sync.dtos.TransactionSyncRequest;
import com.payment.shared.sync.strategy.common.EntityType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/master/sync")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterSyncController {

    private final MasterSyncReceiver masterSyncReceiver;
    private final AgentRepository agentRepository;  

    @PostMapping("/transactions")
    public ResponseEntity<SyncResponse> syncTransactions(
                @RequestHeader("X-API-Key") String apiKey,
                @RequestBody TransactionSyncRequest request) {
        
        // Validate API key
        ResponseEntity<SyncResponse> validationError = validateAgent(apiKey, request.getAgentId());
        if (validationError != null) {
            return validationError;  // Return 401 error
        }

        log.info("Syncing {} transactions from agent {} ",
                 request.getTransactions().size(),
                 request.getAgentId()
                );

        SyncResponse response = masterSyncReceiver.receive(EntityType.TRANSACTION, 
                                                            request.getTransactions()
                                                        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/customers")
    public ResponseEntity<SyncResponse> syncCustomers(
                @RequestHeader("X-API-Key") String apiKey,
                @RequestBody CustomerSyncRequest request) {

        // Validate API key
        ResponseEntity<SyncResponse> validationError = validateAgent(apiKey, request.getAgentId());
        if (validationError != null) {
            return validationError;  // Return 401 error
        }
    
        log.info("Syncing {} customers from agent {} ",
                 request.getCustomers().size(),
                 request.getAgentId()
                );
        SyncResponse response = masterSyncReceiver.receive(EntityType.CUSTOMER, request.getCustomers());

        return ResponseEntity.ok(response);
    }
    
    /**
     * Validates API key and agent ID.
     * 
     * @param apiKey the API key from request header
     * @param agentId the agent ID from request body
     * @return error response if invalid, null if valid
     */
    private ResponseEntity<SyncResponse> validateAgent(String apiKey, String agentId) {
        // Check if API key exists
        Optional<Agent> agentOpt = agentRepository.findByApiKey(apiKey);
        
        if (agentOpt.isEmpty()) {
            log.warn("Invalid API key provided");
            return ResponseEntity.status(401)
                .body(SyncResponse.builder()
                    .status("FAILED")
                    .message("Invalid API key")
                    .synced(0)
                    .conflicts(0)
                    .build());
        }
        
        Agent agent = agentOpt.get();
        
        // Verify agent ID matches
        if (!agent.getId().equals(agentId)) {
            log.warn("Agent ID mismatch. Expected: {}, Got: {}", agent.getId(), agentId);
            return ResponseEntity.status(401)
                .body(SyncResponse.builder()
                    .status("FAILED")
                    .message("Agent ID mismatch")
                    .synced(0)
                    .conflicts(0)
                    .build());
        }
        
        // Valid - return null (no error)
        return null;
    }


}