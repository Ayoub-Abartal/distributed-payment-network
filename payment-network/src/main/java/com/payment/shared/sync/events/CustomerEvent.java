package com.payment.shared.sync.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEvent implements SyncEvent {
    private String id;
    private String phoneNumber;
    private String name;
    private Double balance;
    private LocalDateTime createdAt;
    private LocalDateTime lastTransactionAt;
    private LocalDateTime timestamp;
    private String agentId;
    private String eventType;

    @Override
    public String getEventType(){
        return eventType;
    }

    @Override
    public String getEntityId(){
        return id;
    }

    @Override
    public LocalDateTime getTimestamp(){
        return timestamp;
    }

}
