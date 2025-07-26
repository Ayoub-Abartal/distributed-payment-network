# Architecture Overview

## Design Philosophy

This project uses **feature-based architecture** (vertical slices) combined with **profile-based deployment** where code is organized by business capability rather than technical layer.

Key principles:
- ✅ Features grouped by business domain (agent/, master/, shared/)
- ✅ Single application deployed with different profiles (master, agent)
- ✅ Modular structure within features (dtos/, service/, controller/, listener/)
- ✅ Shared components in dedicated package (entity/, repository/, enums/, config/)

## Project Structure

- **payment-network**: Single Spring Boot application (backend)
- **master-dashboard**: React + TypeScript (master UI) - TODO
- **agent-interface**: React + TypeScript (agent UI) - TODO

## Single Application, Multiple Instances
```
┌─────────────────────────────────────────────────────────┐
│           PAYMENT-NETWORK APPLICATION                   │
│                  (Single Codebase)                      │
└─────────────────────────────────────────────────────────┘
                          │
                          │ Profile-based deployment
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

## Actual Project Structure (Feature-Based)
```
payment-network/
├── src/main/java/com/payment/
│   │
│   ├── agent/                           # Agent Instance Features
│   │   └── registration/
│   │       ├── dtos/
│   │       │   └── AgentRegistrationRequest.java
│   │       ├── service/
│   │       │   ├── AgentRegistrationService.java
│   │       │   └── AgentRegistrationServiceImpl.java
│   │       └── listener/
│   │           └── AgentStartupRunner.java
│   │
│   ├── master/                          # Master Instance Features
│   │   └── agent/
│   │       ├── dtos/
│   │       │   └── AgentRegistrationResponse.java
│   │       ├── service/
│   │       │   ├── MasterAgentService.java
│   │       │   └── MasterAgentServiceImpl.java
│   │       └── controller/
│   │           └── MasterAgentController.java
│   │
│   ├── shared/                          # Shared Across Both
│   │   ├── domain/
│   │   │   ├── entity/
│   │   │   │   ├── Agent.java
│   │   │   │   └── Transaction.java
│   │   │   └── repository/
│   │   │       ├── AgentRepository.java
│   │   │       └── TransactionRepository.java
│   │   ├── enums/
│   │   │   ├── AgentStatus.java
│   │   │   ├── TransactionType.java
│   │   │   └── SyncStatus.java
│   │   └── config/
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
├── master-dashboard/                    # React app (TODO)
└── agent-interface/                     # React app (TODO)
```

## Profile Detection Mechanism

### Main Application Class
```java
@SpringBootApplication
@EnableScheduling
public class PaymentNetworkApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PaymentNetworkApplication.class);
        app.setAdditionalProfiles(getActiveProfile(args));
        app.run(args);
    }
    
    private static String getActiveProfile(String[] args) {
        // 1. Check command line args
        // 2. Check system property
        // 3. Default to "master"
    }
}
```

### Profile-Specific Beans
Components use `@ConditionalOnProperty` to load only for specific profiles:
```java
@Service
@ConditionalOnProperty(name = "app.role", havingValue = "agent")
public class AgentRegistrationServiceImpl { ... }

@Service
@ConditionalOnProperty(name = "app.role", havingValue = "master")
public class MasterAgentServiceImpl { ... }
```

## Running the Application

### Start Master Instance
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=master"
```

### Start Agent Instance
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=agent --app.agent.id=AGENT1 --app.agent.name='Casa Shop'"
```

### Access H2 Consoles
- **Master:** http://localhost:8080/h2-console (URL: `jdbc:h2:file:./data/master`)
- **Agent:** http://localhost:8081/h2-console (URL: `jdbc:h2:file:./data/agent-AGENT1`)

## Technology Stack

**Backend:**
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (file-based)
- Spring Boot Actuator
- Bucket4j (rate limiting)
- Lombok
- Jakarta Validation

**Frontend (TODO):**
- React 18 + TypeScript
- Axios for API calls
- Tailwind CSS
- Recharts for visualizations

**DevOps (TODO):**
- Docker Compose
- Multi-instance deployment

## Implemented Features

### ✅ Priority 1: Agent Registration
- [x] Agent entity with API key
- [x] Agent registration DTOs
- [x] Master receives and validates registration
- [x] API key generation (UUID)
- [x] Agent auto-registers on startup using `@EventListener`
- [x] Profile-based database configuration
- [x] Separate databases per instance

### 🔄 Priority 2: Transaction & Sync (In Progress)
- [ ] Transaction creation (deposit)
- [ ] Bidirectional sync (30s interval)
- [ ] Conflict resolution
- [ ] Agent transaction interface
- [ ] Master dashboard

### 📋 Priority 3: Additional Features (TODO)
- [ ] Withdrawal operation
- [ ] Balance inquiry
- [ ] Transaction history
- [ ] Agent health monitoring
- [ ] Daily settlement report

## Why This Architecture?

### Single App with Profiles vs. Microservices

✅ **Production Pattern:** Used by Kafka, Elasticsearch, Redis clusters
✅ **Code Reuse:** Shared domain logic, entities, utilities
✅ **Deployment Flexibility:** Same JAR, different configs
✅ **Faster Development:** 2-3 days vs 4-5 days for separate services
✅ **Operational Simplicity:** Single build pipeline

### Feature-Based vs. Layered

**Traditional Layered (Avoided):**
```
controller/
service/
repository/
dto/
```
❌ Related code scattered across folders
❌ Hard to find all code for one feature

**Feature-Based (Implemented):**
```
agent/
  └── registration/
      ├── dtos/
      ├── service/
      ├── controller/
      └── listener/
```
✅ All registration code in one place
✅ Clear feature boundaries
✅ Easy to develop/test independently

## Key Design Decisions

### 1. Profile Detection
- Custom logic in main class vs Spring's default
- Supports command line, system properties, and defaults
- Explicit profile selection for clarity

### 2. Database Configuration
- Programmatic `DataSource` creation for agents
- Dynamic database path based on agent ID
- Ensures complete isolation between instances

### 3. Startup Registration
- Uses `@EventListener(ApplicationReadyEvent.class)`
- Different from `ApplicationRunner` (company pattern)
- Fires when app is fully ready, not just started

### 4. Modular Feature Structure
- Each feature has its own dtos/, service/, controller/
- Promotes encapsulation and maintainability
- Easy to add new features without impacting existing ones

## Communication Flow

### Agent Registration (Implemented)
```
Agent Startup
    ↓
@EventListener triggers
    ↓
AgentRegistrationService
    ↓
POST /api/master/agents/register
    ↓
MasterAgentService validates & saves
    ↓
Generate API Key (UUID)
    ↓
Return API key to agent
    ↓
Agent stores for future sync
```

### Transaction Sync (TODO)
```
Agent creates transaction locally
    ↓
Scheduled task (every 30s)
    ↓
Push to Master (POST /api/master/sync/receive)
    ↓
Master validates & stores
    ↓
Agent pulls from Master (GET /api/master/sync/transactions)
    ↓
Agent merges data locally
```

## Database Schema

### Agents Table
```sql
CREATE TABLE agents (
    id VARCHAR(255) PRIMARY KEY,
    business_name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) UNIQUE NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    api_key VARCHAR(255) UNIQUE,
    registered_at TIMESTAMP NOT NULL,
    last_seen_at TIMESTAMP
);
```

### Transactions Table (TODO)
```sql
CREATE TABLE transactions (
    id VARCHAR(255) PRIMARY KEY,
    agent_id VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
```

## API Endpoints

### Master APIs (Implemented)
```
POST /api/master/agents/register
  Request: AgentRegistrationRequest
  Response: AgentRegistrationResponse (with API key)
```

### Master APIs (TODO)
```
POST /api/master/sync/receive          # Receive transactions from agents
GET  /api/master/sync/transactions     # Send transactions to agents
GET  /api/master/dashboard/metrics     # Dashboard data
```

### Agent APIs (TODO)
```
POST /api/agent/transactions           # Create transaction
GET  /api/agent/transactions           # List local transactions
GET  /api/agent/sync-status            # Check sync status
```

## Actuator Endpoints

Both master and agent expose:
- `/actuator/health` - Health checks
- `/actuator/info` - Application info
- `/actuator/metrics` - Performance metrics

## Future Enhancements

1. **Security:**
   - API key validation on all sync endpoints
   - Rate limiting per agent (Bucket4j ready)
   - HTTPS in production

2. **Monitoring:**
   - Agent health tracking (last_seen_at)
   - Sync failure alerts
   - Transaction volume metrics

3. **Resilience:**
   - Retry logic for failed syncs
   - Offline transaction queue
   - Circuit breaker pattern

4. **Frontend:**
   - Master dashboard (React)
   - Agent interface (React)
   - Real-time updates (WebSocket)