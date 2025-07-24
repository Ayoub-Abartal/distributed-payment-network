# Architecture Overview

## Design Philosophy

This project uses **feature-based architecture** (also called vertical slices) where code is organized by business capability rather than technical layer. This approach:

- ✅ Makes features easier to locate and understand
- ✅ Reduces coupling between features
- ✅ Allows independent feature development
- ✅ Better reflects business domains

## Structure

- **payment-network**: Single Spring Boot application (backend)
- **master-dashboard**: React + TypeScript (master UI)
- **agent-interface**: React + TypeScript (agent UI)

## Single Application, Multiple Instances
```
┌─────────────────────────────────────────────────────────┐
│           PAYMENT-NETWORK APPLICATION                   │
│                  (Single Codebase)                      │
└─────────────────────────────────────────────────────────┘
                          │
                          │ Deployed as different instances
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
┌───────────────┐  ┌──────────────┐  ┌──────────────┐
│   MASTER      │  │   AGENT 1    │  │   AGENT 2    │
│   Instance    │  │   Instance   │  │   Instance   │
│               │  │              │  │              │
│ Port: 8080    │  │ Port: 8081   │  │ Port: 8082   │
│ Profile:      │  │ Profile:     │  │ Profile:     │
│   master      │  │   agent      │  │   agent      │
│               │  │ ID: AGENT1   │  │ ID: AGENT2   │
│ DB: master.db │  │ DB: agent1.db│  │ DB: agent2.db│
└───────────────┘  └──────────────┘  └──────────────┘
```

## Project Structure (Feature-Based)
```
payment-network/
├── src/main/java/com/payment/
│   │
│   ├── agent/                           # Agent Instance Features
│   │   ├── registration/
│   │   │   ├── AgentRegistrationService.java
│   │   │   └── RegistrationRequest.java
│   │   │
│   │   ├── transaction/
│   │   │   ├── AgentTransactionController.java
│   │   │   ├── TransactionService.java
│   │   │   ├── TransactionRequest.java
│   │   │   └── TransactionResponse.java
│   │   │
│   │   └── sync/
│   │       ├── AgentSyncScheduler.java
│   │       ├── SyncService.java
│   │       └── SyncClient.java
│   │
│   ├── master/                          # Master Instance Features
│   │   ├── agent/
│   │   │   ├── MasterAgentController.java
│   │   │   ├── AgentManagementService.java
│   │   │   └── AgentStatusDTO.java
│   │   │
│   │   ├── sync/
│   │   │   ├── MasterSyncController.java
│   │   │   ├── SyncProcessor.java
│   │   │   ├── ConflictResolver.java
│   │   │   └── SyncRequest.java
│   │   │
│   │   └── dashboard/
│   │       ├── DashboardController.java
│   │       ├── DashboardService.java
│   │       └── DashboardMetrics.java
│   │
│   ├── shared/                          # Shared Across Both
│   │   ├── domain/
│   │   │   ├── Agent.java
│   │   │   ├── Transaction.java
│   │   │   ├── AgentRepository.java
│   │   │   └── TransactionRepository.java
│   │   │
│   │   ├── exception/
│   │   │   ├── ValidationException.java
│   │   │   ├── ConflictException.java
│   │   │   └── UnauthorizedException.java
│   │   │
│   │   └── config/
│   │       ├── ProfileConfig.java
│   │       ├── DatabaseConfig.java
│   │       └── RestTemplateConfig.java
│   │
│   └── PaymentNetworkApplication.java
│
├── src/main/resources/
│   ├── application.yml                  # Common config
│   ├── application-master.yml           # Master profile
│   └── application-agent.yml            # Agent profile
│
├── master-dashboard/                    # React app for master
└── agent-interface/                     # React app for agents
```

## Why Feature-Based Over Layered?

### Layered Architecture (Traditional):
```
controller/
  ├── MasterAgentController.java
  ├── MasterSyncController.java
  ├── AgentTransactionController.java
  └── AgentSyncController.java
service/
  ├── MasterAgentService.java
  ├── MasterSyncService.java
  └── AgentTransactionService.java
repository/
  ├── AgentRepository.java
  └── TransactionRepository.java
```
❌ Related code scattered across folders
❌ Hard to find all code for one feature
❌ Encourages coupling between features

### Feature-Based (Modern):
```
agent/
  └── transaction/
      ├── Controller.java
      ├── Service.java
      ├── Request.java
      └── Response.java
```
✅ All transaction code in one place
✅ Easy to understand feature boundaries
✅ Can develop/test features independently
✅ Natural encapsulation

## Technology Stack

**Backend:**
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database
- Profile-based deployment

**Frontend:**
- React 18 + TypeScript
- Axios for API calls
- Tailwind CSS
- Recharts for visualizations

**DevOps:**
- Docker Compose
- Multi-instance deployment

## Communication Flow

### Agent → Master (Push)
```
Agent Transaction Created
    ↓
Agent Sync Scheduler (30s)
    ↓
POST /api/master/sync/receive
    ↓
Master Stores & Validates
```

### Master → Agent (Pull)
```
Agent Sync Scheduler (30s)
    ↓
GET /api/master/sync/transactions
    ↓
Agent Merges Data
    ↓
Local DB Updated
```

## Profile Detection

**Master Instance:**
```bash
java -jar payment-network.jar \
  --spring.profiles.active=master \
  --server.port=8080
```

**Agent Instance:**
```bash
java -jar payment-network.jar \
  --spring.profiles.active=agent \
  --server.port=8081 \
  --app.agent.id=AGENT1 \
  --app.agent.name="Casa Shop" \
  --app.master.url=http://localhost:8080
```

## Key Features

### Priority 1 (Must Have):
- [x] Feature-based architecture
- [ ] Agent registration system
- [ ] Transaction creation (deposit)
- [ ] Bidirectional sync (30s interval)
- [ ] Basic conflict resolution
- [ ] Master dashboard metrics
- [ ] Agent interface

### Priority 2 (Should Have):
- [ ] Withdrawal operation
- [ ] Balance inquiry
- [ ] Transaction history
- [ ] Agent health monitoring

### Priority 3 (Nice to Have):
- [ ] Daily settlement report
- [ ] Commission tracking
- [ ] Fraud detection alerts

## Why Single App with Profiles?

✅ **Production Pattern:** Used by Kafka, Elasticsearch, Redis clusters
✅ **Code Reuse:** Shared domain logic, entities, utilities
✅ **Deployment Flexibility:** Same JAR, different configs
✅ **Faster Development:** 2-3 days vs 4-5 days for microservices
✅ **Operational Simplicity:** Single build pipeline

The hub-spoke architecture is achieved through **deployment topology**, not code separation.