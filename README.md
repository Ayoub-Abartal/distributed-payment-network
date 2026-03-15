# Distributed Payment Network

A full-stack fintech application demonstrating a distributed agent-master payment system with real-time synchronization and conflict resolution.

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)

## 🎯 Overview

This project simulates a distributed payment network where multiple agents can process transactions independently, with automatic synchronization to a central master node. It demonstrates real-world fintech challenges including eventual consistency, conflict resolution, and real-time data synchronization.

## ✨ Features

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

## 🏗️ Architecture

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

**Event-Driven Synchronization (In Progress):**
- Strategy Pattern implementation for flexible sync mechanisms
- Event DTOs (TransactionEvent, CustomerEvent) separate from JPA entities
- Entity-specific strategies (HTTP, Kafka, Hybrid) for different sync approaches
- Kafka infrastructure ready for event streaming

**Current Sync Flow:**
1. Agent processes transaction locally with generated UUID
2. Transaction marked as PENDING_SYNC
3. Background scheduler pushes to master via HTTP (transitioning to Kafka)
4. Master validates and stores with SYNCED status
5. Master aggregates data for dashboard
6. Conflict resolution using last-write-wins strategy

## 🛠️ Tech Stack

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
- **Strategy Pattern** - Pluggable sync mechanisms (HTTP, Kafka, Hybrid)
- **DTO Mapping** - API data transfer objects and event DTOs
- **Interceptor Pattern** - Automatic API key injection for authentication
- **ID Preservation** - UUID generation at source for distributed referential integrity
- **Custom Hooks** - React state and side effects management
- **Polling Strategy** - Auto-refresh for real-time updates
- **Debouncing** - Optimized user input handling
- **Conflict Resolution** - Last-write-wins for distributed consistency

## 🚀 Getting Started

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

## 📁 Project Structure

```
distributed-payment-network/
├── payment-network/              # Spring Boot backend
│   └── src/main/java/com/payment/
│       ├── agent/                # Agent-specific controllers & services
│       │   ├── customer/         # Customer management
│       │   ├── sync/             # Sync scheduler
│       │   └── transaction/      # Transaction processing
│       ├── master/               # Master-specific controllers & services
│       │   ├── agent/            # Agent registration
│       │   ├── customer/         # Customer aggregation
│       │   ├── dashboard/        # Dashboard metrics
│       │   └── sync/             # Sync receiver
│       └── shared/               # Shared entities & repositories
│           ├── domain/
│           │   ├── entity/       # JPA entities
│           │   └── repositories/ # Spring Data repositories
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

## 🔑 Key Implementation Details

### Backend Architecture

**Spring Profiles for Multi-Mode:**
```java
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterSyncController { ... }

@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class AgentSyncScheduler { ... }
```

**DTO Projections for Optimized Queries:**
```java
public interface CustomerProjection {
    String getPhoneNumber();
    String getName();
    Double getBalance();
}
```

**Transaction Management:**
```java
@Transactional
public SyncResponse receiveTransactions(SyncRequest request) {
    // Atomic transaction processing with conflict resolution
}
```

### Frontend Architecture

**Custom Hooks for Data Management:**
```typescript
export const useTransactions = (refreshInterval: number) => {
  // Automatic polling with state management
  // Error handling and loading states
  // Transaction submission logic
}
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

## 🔐 Security Considerations

- API key authentication for agent-master communication
- Agent ID validation on sync requests
- Phone number format validation
- Balance validation before withdrawals
- Transaction isolation for data consistency

## 🚧 Future Enhancements

- Complete Kafka event-driven sync implementation
- JWT authentication for web interfaces
- WebSocket for real-time updates
- Transaction rollback mechanism
- Multi-currency support
- Advanced reporting and analytics
- Agent performance metrics
- Audit logging
- Property-based testing for distributed consistency

## 📝 About This Project

This is a personal project built to demonstrate distributed systems concepts used in production environments. The Hub-Spoke architecture is inspired by a real system maintained for 176 sites, implementing eventual consistency, conflict resolution, and real-time synchronization.

Currently implementing event-driven architecture with Kafka using the Strategy Pattern for flexible sync mechanisms (HTTP, Kafka, Hybrid).

## 📝 License

This project is for demonstration purposes.