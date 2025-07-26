package com.payment.shared.domain.repositories;

import com.payment.shared.domain.entity.Agent;
import com.payment.shared.enums.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {

    // Find agent by API key (for authentication)
    Optional<Agent> findByApiKey(String apiKey);

    // Count active agents
    long countByStatus(AgentStatus status);
}