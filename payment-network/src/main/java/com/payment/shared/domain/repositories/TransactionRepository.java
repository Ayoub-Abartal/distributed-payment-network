package com.payment.shared.domain.repositories;

import com.payment.shared.domain.entity.Transaction;
import com.payment.shared.enums.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Find transactions that need to be synced
    List<Transaction> findByStatus(SyncStatus status);

    // Find transactions by agent
    List<Transaction> findByAgentId(String agentId);

    // Count transactions by agent
    long countByAgentId(String agentId);

    // Find transactions created after a certain time (for sync)
    List<Transaction> findByTimestampAfter(LocalDateTime timestamp);

    // Find transactions from OTHER agents (for pulling from master)
    List<Transaction> findByAgentIdNotAndTimestampAfter(String agentId, LocalDateTime timestamp);

    // Get recent transactions (for dashboard)
    List<Transaction> findTop50ByOrderByTimestampDesc();

    // Sum all transaction amounts
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t")
    Double sumAllAmounts();

    // Sum amounts after a certain time
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.timestamp > :since")
    Double sumAmountsByTimestampAfter(LocalDateTime since);

    // Count transactions after a certain time
    long countByTimestampAfter(LocalDateTime timestamp);
}