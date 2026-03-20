package com.payment.agent.sync.orchestrator;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.payment.shared.sync.events.SyncEvent;
import com.payment.shared.sync.strategy.common.SyncResult;
import com.payment.shared.sync.strategy.entity.EntitySyncStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentSyncEntityConfig<T extends SyncEvent>{
    
    private String entityName;
    private int order;
    private EntitySyncStrategy<T> strategy;
    private Supplier<List<T>> eventSupplier;
    private Consumer<SyncResult> resultHandler;
}
