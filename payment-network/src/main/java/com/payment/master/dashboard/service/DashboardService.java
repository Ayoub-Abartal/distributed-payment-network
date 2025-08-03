package com.payment.master.dashboard.service;

import com.payment.master.dashboard.dtos.DashboardMetricsResponse;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.domain.entity.Transaction;

import java.util.List;

/**
 * Dashboard service interface
 * Provides data for the master dashboard
 */
public interface DashboardService {

    /**
     * Get all agents
     */
    List<Agent> getAllAgents();

    /**
     * Get all transactions
     */
    List<Transaction> getAllTransactions();

    /**
     * Get dashboard metrics
     */
    DashboardMetricsResponse getMetrics();
}
