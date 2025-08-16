package com.payment.master.dashboard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard metrics response
 * Contains high-level stats for the dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {
    private Integer totalAgents;
    private Integer activeAgents;
    private Integer totalTransactions;
    private Double totalVolume;

    // Today's statistics
    private Integer todayTransactions;
    private Double todayDeposits;
    private Double todayWithdrawals;
    private Double todayVolume;

    // Customer statistics
    private Integer totalCustomers;
}
