package com.payment.master.dashboard.controller;

import com.payment.master.dashboard.dtos.DashboardMetricsResponse;
import com.payment.master.dashboard.service.DashboardService;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.domain.entity.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Dashboard Controller
 * Provides endpoints for the React dashboard
 */
@Slf4j
@RestController
@Profile("master")  // Only active in master mode
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get all agents
     * GET /api/master/agents
     */
    @GetMapping("/agents")
    public ResponseEntity<List<Agent>> getAllAgents() {
        log.info("Dashboard request: Get all agents");
        List<Agent> agents = dashboardService.getAllAgents();
        log.info("Returning {} agents", agents.size());
        return ResponseEntity.ok(agents);
    }

    /**
     * Get all transactions
     * GET /api/master/transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        log.info("Dashboard request: Get all transactions");
        List<Transaction> transactions = dashboardService.getAllTransactions();
        log.info("Returning {} transactions", transactions.size());
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get dashboard metrics
     * GET /api/master/dashboard/metrics
     */
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<DashboardMetricsResponse> getMetrics() {
        log.info("Dashboard request: Get metrics");
        DashboardMetricsResponse metrics = dashboardService.getMetrics();
        log.info("Metrics: {} agents, {} active, {} transactions, {} volume",
                metrics.getTotalAgents(),
                metrics.getActiveAgents(),
                metrics.getTotalTransactions(),
                metrics.getTotalVolume());
        return ResponseEntity.ok(metrics);
    }
}
