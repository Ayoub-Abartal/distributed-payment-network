package com.payment.master.sync.dtos;

import java.util.List;

import com.payment.shared.sync.events.CustomerEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSyncRequest {
    
    private String agentId;
    private List<CustomerEvent> customers;
    
}
