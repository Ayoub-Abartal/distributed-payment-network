package com.payment.shared.sync.events;

import java.time.LocalDateTime;
import com.payment.shared.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent implements SyncEvent{

    private String id;
    private String agentId;
    private String customerPhone;
    private TransactionType type;
    private Double amount;
    private LocalDateTime timestamp;
    private String eventType;

    @Override
    public String getEntityId() {
        return id;
    }

    @Override
    public String getEventType(){
        return eventType;
    }

    @Override
    public LocalDateTime getTimestamp(){
        return timestamp;
    }

}
