package com.payment.shared.sync.events;

import java.time.LocalDateTime;

public interface SyncEvent {
    /** 
     * Get the type of event (e.g. "Transaction_Created", "Customer_Updated") 
     * @return String 
     */
    String getEventType();
    
    /**
     * Get the unique identifier of the entity(e.g., transaction ID, ....)
     * @return String
     */
    String getEntityId();

    /**
     * Get the Timestamp when the event occured 
     * @return LocalDateTime
     */
    LocalDateTime getTimestamp();
}
