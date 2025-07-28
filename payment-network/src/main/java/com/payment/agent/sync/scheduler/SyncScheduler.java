package com.payment.agent.sync.scheduler;

import com.payment.agent.sync.service.AgentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.sync.enabled", havingValue = "true")
public class SyncScheduler {

    private final AgentSyncService agentSyncService;

    @Scheduled(fixedRateString = "${app.sync.interval}")
    public void syncWithMaster() {
        log.debug("Scheduled sync triggered");
        agentSyncService.pushToMaster();
    }
}