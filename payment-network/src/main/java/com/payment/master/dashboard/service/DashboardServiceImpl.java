package com.payment.master.dashboard.service;

import com.payment.master.dashboard.dtos.DashboardMetricsResponse;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.AgentRepository;
import com.payment.shared.domain.repositories.TransactionRepository;
import com.payment.shared.enums.AgentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Dashboard service implementation
 * Aggregates data from agents and transactions for the dashboard
 */
@Slf4j
@Service
@Profile("master")  // Only active in master mode
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AgentRepository agentRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<Agent> getAllAgents() {
        log.debug("Fetching all agents for dashboard");
        return agentRepository.findAll();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        log.debug("Fetching all transactions for dashboard");
        return transactionRepository.findAll();
    }

    @Override
    public DashboardMetricsResponse getMetrics() {
        log.debug("Calculating dashboard metrics");

        // Get all agents and transactions
        List<Agent> agents = agentRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAll();

        // Count active agents
        long activeAgents = agents.stream()
                .filter(agent -> agent.getStatus() == AgentStatus.ACTIVE)
                .count();

        // Calculate total volume
        double totalVolume = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        return DashboardMetricsResponse.builder()
                .totalAgents(agents.size())
                .activeAgents((int) activeAgents)
                .totalTransactions(transactions.size())
                .totalVolume(totalVolume)
                .build();
    }
}
