# Distributed Payment Network

A full-stack fintech application demonstrating a distributed agent-master payment system with real-time synchronization and conflict resolution.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)

## Overview

This project simulates a distributed payment network where multiple agents can process transactions independently, with automatic synchronization to a central master node. It demonstrates real-world fintech challenges including eventual consistency, conflict resolution, and real-time data synchronization.

## Features

### Master Dashboard
- **Real-time monitoring** of all agents, transactions, and system metrics
- **Agent management** with online/offline status detection
- **Customer overview** across all agents with balance tracking
- **Transaction history** with advanced filtering and search
- **Today's activity metrics** including deposits, withdrawals, and total volume
- **Auto-refresh** every 5 seconds for live updates

### Agent Interface
- **Transaction processing** for deposits and withdrawals
- **Customer management** with creation and balance tracking
- **Quick customer selection** via dropdown or dedicated management tab
- **Balance validation** to prevent insufficient fund withdrawals
- **Transaction history** for the local agent
- **Automatic synchronization** with master node every 30 seconds

### System Features
- **Distributed architecture** with agent-master synchronization
- **Conflict resolution** using last-write-wins strategy
- **Bidirectional sync** for transactions and customers
- **Customer auto-creation** during first transaction
- **Phone number validation** for Moroccan format (06/07)
- **Real-time balance updates** and transaction tracking

## 📸 Screenshots

### Master Dashboard - Overview
![Master Dashboard](screenshoots/master-dashboard.png)
*Comprehensive view of all agents, customers, and real-time metrics including today's activity*

### Master Dashboard - Transaction History
![Transaction History](screenshoots/transactions-history.png)
*Complete transaction history with filtering by phone, agent, and transaction type*

### Agent Interface - Transaction Processing
![Agent Transactions](screenshoots/agent-transaction.png)
*Process deposits and withdrawals with customer selection dropdown and balance display*

### Agent Interface - Customer Management
![Agent Customers](screenshoots/agent-customers.png)
*Create and manage customers with search functionality and balance overview*

## Architecture

### System Design
```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   Agent Node 1  │         │   Master Node   │         │   Agent Node 2  │
│                 │         │   (Hub)         │         │                 │
│  - Transactions │◄───────►│  - Aggregation  │◄───────►│  - Transactions │
│  - Customers    │  Sync   │  - Monitoring   │  Sync   │  - Customers    │
│  - PostgreSQL   │  Event  │  - PostgreSQL   │  Event  │  - PostgreSQL   │
│                 │  Driven │  - Kafka        │  Driven │                 │
└─────────────────┘         └─────────────────┘         └─────────────────┘
         │                           │                           │
         └───────────────────────────┴───────────────────────────┘
                                     │
                              ┌──────▼──────┐
                              │    Kafka    │
                              │  (Event Bus)│
                              └─────────────┘
```

### Key Components

**Spring Profiles:**
- `master` - Runs the master node (Hub) with aggregation and monitoring
- `agent` - Runs agent nodes (Spokes) for transaction processing

**Database Architecture:**
- Each node has its own PostgreSQL database (true distributed architecture)
- UUID-based entity IDs preserved across all nodes for referential integrity
- No JPA relationships - entities linked by business keys (phoneNumber)
- Agent ID embedded in all entities for ownership tracking

**Authentication:**
- API key-based authentication using RestTemplate interceptor pattern
- Automatic header injection for all sync requests
- Designed for easy migration to JWT in the future

**HTTP Synchronization (Implemented):**
- Orchestrator-Receiver pattern for clean separation of concerns
- Agent side: `AgentSyncOrchestrator` coordinates multi-entity sync
- Master side: `MasterSyncReceiver` routes events to entity-specific handlers
- Event DTOs (TransactionEvent, CustomerEvent) separate from JPA entities
- Auto-discovery of handlers via Spring for easy scalability
- Strategy Pattern ready for future Kafka/Hybrid implementations

**Current Sync Flow:**
1. Agent processes transaction locally with generated UUID
2. Transaction marked as PENDING_SYNC
3. Background scheduler triggers orchestrator every 30 seconds
4. Orchestrator syncs Customer first, then Transaction (dependency order)
5. HTTP strategy sends events to master with API key authentication
6. Master receiver routes to handlers, converts events to entities
7. Entities saved with SYNCED status
8. Agent updates local status based on response
9. Master aggregates data for dashboard

## Tech Stack

### Backend
- **Java 17** - Modern Java features and performance
- **Spring Boot 3.x** - Enterprise-grade framework
- **Spring Data JPA** - Database abstraction with projections
- **Spring Profiles** - Environment-based configuration (master/agent)
- **PostgreSQL 15** - Production-grade relational database (separate instance per node)
- **Apache Kafka** - Event streaming platform for distributed messaging
- **Lombok** - Reduces boilerplate code
- **Maven** - Dependency management and build tool

### Frontend
- **React 18** - Modern UI library with hooks
- **TypeScript** - Type-safe JavaScript
- **Axios** - HTTP client for API integration
- **Custom Hooks** - Reusable logic for data fetching and state management
- **Feature-based Architecture** - Modular and scalable code organization
- **Tailwind CSS** - Utility-first CSS framework

### Key Patterns & Practices
- **Repository Pattern** - Clean data access layer
- **Service Layer** - Business logic separation
- **Orchestrator Pattern** - Coordinates multi-entity sync on agent side
- **Receiver Pattern** - Routes events to handlers on master side
- **Strategy Pattern** - Pluggable sync mechanisms (HTTP implemented, Kafka/Hybrid ready)
- **Handler Pattern** - Entity-specific processing with auto-discovery
- **DTO Mapping** - API data transfer objects and event DTOs
- **Interceptor Pattern** - Automatic API key injection for authentication
- **ID Preservation** - UUID generation at source for distributed referential integrity
- **Custom Hooks** - React state and side effects management
- **Polling Strategy** - Auto-refresh for real-time updates
- **Debouncing** - Optimized user input handling

## Getting Started

### Prerequisites
- Docker and Docker Compose
- (Optional) Java 17, Node.js 16+, Maven 3.6+ for local development

### Option 1: Run with Docker Compose (Recommended)

**Start the entire system (Hub + 2 Spokes + Infrastructure):**

```bash
docker-compose up
```

This will start:
- **Zookeeper:** localhost:2181 (Kafka coordination)
- **Kafka Broker:** localhost:9092 (Event streaming)
- **Kafka UI:** http://localhost:8090 (Kafka management interface)
- **PostgreSQL Hub:** localhost:5432 (Master database)
- **PostgreSQL Spoke 1:** localhost:5433 (Agent 1 database)
- **PostgreSQL Spoke 2:** localhost:5434 (Agent 2 database)
- **pgAdmin:** http://localhost:5050 (Database management - admin@admin.com/admin)
- **Hub (Master):** http://localhost:8080
- **Spoke 1:** http://localhost:8081
- **Spoke 2:** http://localhost:8082
- **Master Dashboard:** http://localhost:3000
- **Agent Interface:** http://localhost:3001

**Stop the system:**
```bash
docker-compose down
```

**Stop and remove all data (fresh start):**
```bash
docker-compose down -v
```

---

### Option 2: Run Manually (Without Docker)

#### Backend - Master Node

```bash
cd payment-network
mvn spring-boot:run -Dspring-boot.run.arguments=--app.role=master
```

The master node will start on `http://localhost:8080`

#### Backend - Agent Node

```bash
cd payment-network
mvn spring-boot:run -Dspring-boot.run.arguments=--app.role=agent,--server.port=8081,--agent.id=AGENT-001,--agent.name="Agent Casa",--agent.location="Casablanca"
```

The agent node will start on `http://localhost:8081`

#### Frontend - Master Dashboard

```bash
cd master-dashboard
npm install
npm start
```

Dashboard will open at `http://localhost:3000`

#### Frontend - Agent Interface

```bash
cd agent-interface
npm install
REACT_APP_AGENT_API_URL=http://localhost:8081 npm start
```

Agent interface will open at `http://localhost:3001`

## Project Structure

```
distributed-payment-network/
├── payment-network/              # Spring Boot backend
│   └── src/main/java/com/payment/
│       ├── agent/                # Agent-specific controllers & services
│       │   ├── customer/         # Customer management
│       │   ├── sync/             # Sync orchestrator & strategies
│       │   │   ├── orchestrator/ # AgentSyncOrchestrator
│       │   │   ├── strategy/     # HTTP/Kafka/Hybrid strategies
│       │   │   └── service/      # Sync scheduler
│       │   └── transaction/      # Transaction processing
│       ├── master/               # Master-specific controllers & services
│       │   ├── agent/            # Agent registration
│       │   ├── customer/         # Customer aggregation
│       │   ├── dashboard/        # Dashboard metrics
│       │   └── sync/             # Sync receiver & handlers
│       │       ├── receiver/     # MasterSyncReceiver
│       │       ├── handler/      # Entity-specific handlers
│       │       ├── config/       # Auto-discovery config
│       │       └── controller/   # REST endpoints
│       └── shared/               # Shared entities & repositories
│           ├── domain/
│           │   ├── entity/       # JPA entities
│           │   └── repositories/ # Spring Data repositories
│           ├── sync/             # Sync contracts & DTOs
│           │   ├── events/       # Event DTOs
│           │   ├── dtos/         # Request/Response DTOs
│           │   ├── strategy/     # Strategy interfaces
│           │   └── mapper/       # Entity-Event mappers
│           └── enums/            # Shared enumerations
│
├── master-dashboard/             # React master dashboard
│   └── src/
│       ├── features/
│       │   └── dashboard/
│       │       ├── components/   # Dashboard UI components
│       │       ├── hooks/        # Custom React hooks
│       │       ├── services/     # API services
│       │       ├── types/        # TypeScript types
│       │       └── utils/        # Helper functions
│       └── pages/                # Page components
│
└── agent-interface/              # React agent interface
    └── src/
        ├── features/
        │   ├── customer/         # Customer management feature
        │   │   ├── components/
        │   │   ├── hooks/
        │   │   ├── services/
        │   │   └── types/
        │   └── transaction/      # Transaction feature
        │       ├── components/
        │       ├── hooks/
        │       ├── services/
        │       └── types/
        └── pages/                # Page components
```



**Feature-based Module Structure:**
```
features/
├── customer/           # Customer domain
│   ├── components/     # UI components
│   ├── hooks/          # Data fetching logic
│   ├── services/       # API calls
│   └── types/          # TypeScript interfaces
└── transaction/        # Transaction domain
    └── ...
```

## Security Considerations

- API key authentication for agent-master communication
- Agent ID validation on sync requests
- Phone number format validation
- Balance validation before withdrawals
- Transaction isolation for data consistency

## Future Enhancements

- Kafka event-driven sync (infrastructure ready, strategy pattern in place)
- Hybrid sync mode (Kafka with HTTP fallback)
- Parallel sync for independent entities
- JWT authentication for web interfaces
- WebSocket for real-time updates
- Transaction rollback mechanism
- Multi-currency support
- Advanced reporting and analytics
- Agent performance metrics
- Audit logging
- Retry logic with exponential backoff
- Circuit breaker pattern
- Property-based testing for distributed consistency

## 📝 About This Project

This is a personal project built to demonstrate distributed systems concepts used in production environments. The Hub-Spoke architecture is inspired by a real system maintained for 176 sites, implementing eventual consistency, conflict resolution, and real-time synchronization.

Currently implements HTTP-based synchronization using orchestrator-receiver pattern. The architecture is designed with Strategy Pattern to support future Kafka and Hybrid sync modes.

## 📝 License

This project is for demonstration purposes.