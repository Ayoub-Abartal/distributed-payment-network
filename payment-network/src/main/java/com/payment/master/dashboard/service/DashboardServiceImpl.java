package com.payment.master.dashboard.service;

import com.payment.master.dashboard.dtos.DashboardMetricsResponse;
import com.payment.shared.domain.entity.Agent;
import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.domain.repositories.AgentRepository;
import com.payment.shared.domain.repositories.CustomerRepository;
import com.payment.shared.domain.repositories.TransactionRepository;
import com.payment.shared.enums.AgentStatus;
import com.payment.shared.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final CustomerRepository customerRepository;

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

        // Today's transactions (from start of today onwards)
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        List<Transaction> todayTxs = transactions.stream()
                .filter(tx -> !tx.getTimestamp().isBefore(startOfToday))
                .toList();

        // Today's deposits
        double todayDeposits = todayTxs.stream()
                .filter(tx -> tx.getType() == TransactionType.DEPOSIT)
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Today's withdrawals
        double todayWithdrawals = todayTxs.stream()
                .filter(tx -> tx.getType() == TransactionType.WITHDRAWAL)
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Total customers
        long totalCustomers = customerRepository.count();

        return DashboardMetricsResponse.builder()
                .totalAgents(agents.size())
                .activeAgents((int) activeAgents)
                .totalTransactions(transactions.size())
                .totalVolume(totalVolume)
                .todayTransactions(todayTxs.size())
                .todayDeposits(todayDeposits)
                .todayWithdrawals(todayWithdrawals)
                .todayVolume(todayDeposits + todayWithdrawals)
                .totalCustomers((int) totalCustomers)
                .build();
    }
}
