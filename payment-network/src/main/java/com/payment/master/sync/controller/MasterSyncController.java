package com.payment.master.sync.controller;

import com.payment.master.sync.dtos.SyncRequest;
import com.payment.master.sync.dtos.SyncResponse;
import com.payment.master.sync.service.MasterSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master/sync")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterSyncController {

    private final MasterSyncService masterSyncService;

    @PostMapping("/receive")
    public ResponseEntity<SyncResponse> receiveTransactions(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody SyncRequest request) {

        log.info("Sync endpoint called by agent: {}", request.getAgentId());

        SyncResponse response = masterSyncService.receiveTransactions(request, apiKey);

        if ("FAILED".equals(response.getStatus())) {
            return ResponseEntity.status(401).body(response);
        }

        return ResponseEntity.ok(response);
    }
}