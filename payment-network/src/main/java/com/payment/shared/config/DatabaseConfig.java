package com.payment.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @ConditionalOnProperty(name = "app.role", havingValue = "agent")
    public DataSource agentDataSource(@Value("${app.agent.id}") String agentId) {
        String dbUrl = "jdbc:h2:file:./data/agent-" + agentId;

        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url(dbUrl)
                .username("sa")
                .password("")
                .build();
    }
}