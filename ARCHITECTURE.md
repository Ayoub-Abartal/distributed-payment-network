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
- **master-dashboard**: React + TypeScript + Tailwind CSS (master UI) ✅
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
        │                 │                 │
        │◄────────────────┴─────────────────┘
        │         Sync every 30s
        │
        ▼
┌───────────────┐
│   DASHBOARD   │
│               │
│ Port: 3000    │
│ React + TS    │
└───────────────┘
```

## Complete Project Structure (Feature-Based)
```
distributed-payment-network/
├── payment-network/                      # Backend (Spring Boot)
│   ├── src/main/java/com/payment/
│   │   │
│   │   ├── agent/                        # Agent Instance Features
│   │   │   ├── registration/
│   │   │   │   ├── dtos/
│   │   │   │   │   └── AgentRegistrationRequest.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── AgentRegistrationService.java
│   │   │   │   │   └── AgentRegistrationServiceImpl.java
│   │   │   │   └── listener/
│   │   │   │       └── AgentStartupRunner.java
│   │   │   ├── transaction/
│   │   │   │   ├── dtos/
│   │   │   │   │   ├── TransactionRequest.java
│   │   │   │   │   └── TransactionResponse.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── TransactionService.java
│   │   │   │   │   └── TransactionServiceImpl.java
│   │   │   │   └── controller/
│   │   │   │       └── AgentTransactionController.java
│   │   │   └── sync/
│   │   │       ├── service/
│   │   │       │   ├── AgentSyncService.java
│   │   │       │   └── AgentSyncServiceImpl.java
│   │   │       └── scheduler/
│   │   │           └── SyncScheduler.java
│   │   │
│   │   ├── master/                       # Master Instance Features
│   │   │   ├── agent/
│   │   │   │   ├── dtos/
│   │   │   │   │   └── AgentRegistrationResponse.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── MasterAgentService.java
│   │   │   │   │   └── MasterAgentServiceImpl.java
│   │   │   │   └── controller/
│   │   │   │       └── MasterAgentController.java
│   │   │   ├── sync/
│   │   │   │   ├── dtos/
│   │   │   │   │   ├── SyncRequest.java
│   │   │   │   │   └── SyncResponse.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── MasterSyncService.java
│   │   │   │   │   └── MasterSyncServiceImpl.java
│   │   │   │   └── controller/
│   │   │   │       └── MasterSyncController.java
│   │   │   └── dashboard/
│   │   │       ├── dtos/
│   │   │       │   └── DashboardMetricsResponse.java
│   │   │       ├── service/
│   │   │       │   ├── DashboardService.java
│   │   │       │   └── DashboardServiceImpl.java
│   │   │       └── controller/
│   │   │           └── DashboardController.java
│   │   │
│   │   ├── shared/                       # Shared Across Both
│   │   │   ├── domain/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Agent.java
│   │   │   │   │   └── Transaction.java
│   │   │   │   └── repository/
│   │   │   │       ├── AgentRepository.java
│   │   │   │       └── TransactionRepository.java
│   │   │   ├── enums/
│   │   │   │   ├── AgentStatus.java
│   │   │   │   ├── TransactionType.java
│   │   │   │   └── SyncStatus.java
│   │   │   └── config/
│   │   │       ├── DatabaseConfig.java
│   │   │       ├── RestTemplateConfig.java
│   │   │       └── CorsConfig.java
│   │   │
│   │   └── PaymentNetworkApplication.java
│   │
│   └── src/main/resources/
│       ├── application.yml               # Common config
│       ├── application-master.yml        # Master profile
│       └── application-agent.yml         # Agent profile
│
└── master-dashboard/                     # Frontend (React)
    ├── src/
    │   ├── features/
    │   │   └── dashboard/
    │   │       ├── components/
    │   │       │   ├── shared/
    │   │       │   │   ├── Badge.tsx
    │   │       │   │   ├── Card.tsx
    │   │       │   │   ├── EmptyState.tsx
    │   │       │   │   ├── LoadingSpinner.tsx
    │   │       │   │   └── ErrorMessage.tsx
    │   │       │   ├── AgentCard.tsx
    │   │       │   ├── AgentList.tsx
    │   │       │   ├── ConnectionStatusBadge.tsx
    │   │       │   ├── MetricsCard.tsx
    │   │       │   ├── TransactionRow.tsx
    │   │       │   ├── TransactionTableHeader.tsx
    │   │       │   └── TransactionList.tsx
    │   │       ├── hooks/
    │   │       │   └── useDashboardData.ts
    │   │       ├── services/
    │   │       │   └── dashboardApi.ts
    │   │       ├── utils/
    │   │       │   ├── connectionStatus.ts
    │   │       │   ├── currencyFormatters.ts
    │   │       │   ├── dateFormatters.ts
    │   │       │   └── transactionHelpers.ts
    │   │       ├── constants/
    │   │       │   └── colors.ts
    │   │       ├── types/
    │   │       │   └── index.ts
    │   │       └── pages/
    │   │           └── DashboardPage.tsx
    │   ├── App.tsx
    │   └── index.tsx
    ├── tailwind.config.js
    └── package.json
```

## Technology Stack

**Backend:**
- Spring Boot 3.2.0
- Java 17
- Spring Data JPA
- H2 Database (file-based)
- Spring Boot Actuator
- Bucket4j (rate limiting)
- Lombok
- Jakarta Validation

**Frontend:**
- React 18
- TypeScript 4.x
- Tailwind CSS 3.x
- Axios for HTTP requests
- Feature-based architecture

**Database:**
- H2 (file-based, separate per instance)
- JPA with Hibernate
- Automatic schema generation

## Implemented Features

### ✅ Phase 1: Agent Registration
- [x] Agent entity with API key
- [x] Agent auto-registers on startup using `@EventListener`
- [x] Master generates UUID API keys
- [x] Unique phone number generation per agent
- [x] Profile-based database configuration
- [x] Separate H2 databases per instance

### ✅ Phase 2: Transaction Management
- [x] Transaction entity (DEPOSIT/WITHDRAWAL)
- [x] Transaction creation at agent
- [x] Transaction status tracking (PENDING_SYNC, SYNCED, FAILED)
- [x] Agent transaction endpoints

### ✅ Phase 3: Bidirectional Sync
- [x] Scheduled sync (every 30 seconds)
- [x] Agent → Master push (transactions + heartbeat)
- [x] API key validation
- [x] Last seen timestamp tracking
- [x] Empty sync for heartbeat (connection monitoring)

### ✅ Phase 4: Master Dashboard
- [x] Dashboard REST API endpoints
  - GET /api/master/agents
  - GET /api/master/transactions
  - GET /api/master/dashboard/metrics
- [x] React dashboard with TypeScript
- [x] Real-time data polling (5s interval)
- [x] Connection status monitoring (🟢🟡🔴⚪)
- [x] Metrics cards (agents, transactions, volume)
- [x] Agent list with connection status
- [x] Transaction list with filtering
- [x] Loading and error states
- [x] Tailwind CSS styling
- [x] CORS configuration

### 📋 Future Enhancements (Optional)
- [ ] Agent interface UI
- [ ] Customer entity and balance tracking
- [ ] Pull sync (agents pulling from each other)
- [ ] Transaction history with date range
- [ ] Daily settlement reports
- [ ] Advanced filtering and search
- [ ] Charts and visualizations
- [ ] API key persistence to database

## Running the Application

### Start Master Instance
```bash
cd payment-network
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=master"
```
Access: http://localhost:8080

### Start Agent Instances
```bash
# Agent 1
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=agent --app.agent.id=AGENT1 --app.agent.name='Casa Shop' --server.port=8081"

# Agent 2
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=agent --app.agent.id=AGENT2 --app.agent.name='Rabat Store' --server.port=8082"

# Agent 3
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=agent --app.agent.id=AGENT3 --app.agent.name='Marrakech Shop' --server.port=8083"
```

### Start Dashboard
```bash
cd master-dashboard
npm install
npm start
```
Access: http://localhost:3000

### Access H2 Consoles
- **Master:** http://localhost:8080/h2-console (URL: `jdbc:h2:file:./data/master`)
- **Agent 1:** http://localhost:8081/h2-console (URL: `jdbc:h2:file:./data/agent-AGENT1`)
- **Agent 2:** http://localhost:8082/h2-console (URL: `jdbc:h2:file:./data/agent-AGENT2`)

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
Components use `@ConditionalOnProperty` or `@Profile` to load only for specific profiles:
```java
@Service
@Profile("agent")
public class AgentRegistrationServiceImpl { ... }

@Service
@Profile("master")
public class MasterAgentServiceImpl { ... }
```

## Communication Flow

### 1. Agent Registration (On Startup)
```
Agent Startup
    ↓
@EventListener(ApplicationReadyEvent) triggers
    ↓
Generate unique phone number (060000000X)
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
Agent stores key in memory for sync
```

### 2. Transaction Creation
```
User creates transaction at agent
    ↓
POST /api/agent/transactions
    ↓
TransactionService creates locally
    ↓
Status: PENDING_SYNC
    ↓
Saved to agent's database
```

### 3. Scheduled Sync (Every 30s)
```
@Scheduled task triggers
    ↓
Agent gets all PENDING_SYNC transactions
    ↓
Build SyncRequest with agent ID + transactions
    ↓
POST /api/master/sync/receive (with X-API-Key header)
    ↓
Master validates API key
    ↓
Master saves transactions with status SYNCED
    ↓
Master updates agent.last_seen_at timestamp
    ↓
Agent marks transactions as SYNCED
```

**Note:** Sync happens even with 0 transactions (heartbeat) to keep `last_seen_at` updated.

### 4. Dashboard Monitoring
```
Dashboard loads (React)
    ↓
useDashboardData hook initializes
    ↓
Fetch data every 5 seconds:
    - GET /api/master/agents
    - GET /api/master/transactions
    - GET /api/master/dashboard/metrics
    ↓
Calculate connection status in frontend:
    - 🟢 ONLINE: last_seen < 60s
    - 🟡 WARNING: last_seen < 10min
    - 🔴 OFFLINE: last_seen > 10min
    - ⚪ NEVER: last_seen = null
    ↓
Display real-time updates
```

## Database Schema

### Agents Table
```sql
CREATE TABLE agents (
    id VARCHAR(255) PRIMARY KEY,           -- AGENT1, AGENT2, etc.
    business_name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) UNIQUE NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,           -- PENDING, ACTIVE, SUSPENDED, REJECTED
    api_key VARCHAR(255) UNIQUE,
    registered_at TIMESTAMP NOT NULL,
    last_seen_at TIMESTAMP                 -- Updated on every sync
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id VARCHAR(255) PRIMARY KEY,           -- UUID
    agent_id VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,             -- DEPOSIT, WITHDRAWAL
    amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,           -- PENDING_SYNC, SYNCED, FAILED
    timestamp TIMESTAMP NOT NULL
);
```

## API Endpoints

### Master Endpoints

#### Agent Management
```
POST /api/master/agents/register
  Request: { agentId, businessName, ownerName, phoneNumber, location }
  Response: { apiKey, status, message }
```

#### Sync Management
```
POST /api/master/sync/receive
  Headers: X-API-Key
  Request: { agentId, transactions: [] }
  Response: { synced, conflicts, status, message }
```

#### Dashboard APIs
```
GET /api/master/agents
  Response: Agent[]

GET /api/master/transactions
  Response: Transaction[]

GET /api/master/dashboard/metrics
  Response: { totalAgents, activeAgents, totalTransactions, totalVolume }
```

### Agent Endpoints

#### Transaction Management
```
POST /api/agent/transactions
  Request: { customerPhone, type, amount }
  Response: { id, agentId, status, timestamp }

GET /api/agent/transactions
  Response: Transaction[]
```

## Dashboard Architecture

### Frontend Structure
```
Dashboard (React)
├── Data Layer
│   ├── Types (TypeScript interfaces)
│   ├── Services (API calls with Axios)
│   └── Hooks (useDashboardData with polling)
├── Utils
│   ├── connectionStatus (ONLINE/WARNING/OFFLINE logic)
│   ├── dateFormatters (relative time, timestamps)
│   ├── currencyFormatters (amount with DH)
│   └── transactionHelpers (sorting, filtering)
├── Components
│   ├── Shared (Badge, Card, EmptyState, LoadingSpinner, ErrorMessage)
│   ├── MetricsCard (stat cards with icons)
│   ├── ConnectionStatusBadge (🟢🟡🔴⚪)
│   ├── AgentCard + AgentList
│   └── TransactionRow + TransactionList
└── Pages
    └── DashboardPage (main layout)
```

### Real-Time Updates
- Dashboard polls every 5 seconds
- Connection status calculated client-side
- Auto-refresh indicator shows sync state
- Loading/error states handled gracefully

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
- Dynamic database path based on agent ID: `data/agent-{AGENT_ID}.db`
- Ensures complete isolation between instances

### 3. Startup Registration
- Uses `@EventListener(ApplicationReadyEvent.class)`
- Different from `ApplicationRunner` (company pattern)
- Fires when app is fully ready, not just started

### 4. Unique Phone Numbers
- Generated based on agent ID: AGENT1 → 0600000001
- Prevents unique constraint violations
- Supports unlimited agents

### 5. Heartbeat Sync
- Agents sync every 30s even with no transactions
- Keeps `last_seen_at` updated for connection monitoring
- Frontend calculates status based on timestamp

### 6. Frontend Architecture
- Feature-based structure (features/dashboard/)
- Utility-first with Tailwind CSS
- Real-time updates with polling
- Comprehensive TypeScript types

## Security Considerations

### Implemented
- ✅ API key generation (UUID)
- ✅ API key validation on sync endpoints
- ✅ CORS configuration for dashboard
- ✅ Separate databases per agent

### Production TODO
- [ ] HTTPS/TLS
- [ ] API key encryption in database
- [ ] Rate limiting per agent (Bucket4j ready)
- [ ] Request signing
- [ ] Authentication for dashboard

## Monitoring & Observability

### Actuator Endpoints
Both master and agent expose:
- `/actuator/health` - Health checks
- `/actuator/info` - Application info
- `/actuator/metrics` - Performance metrics

### Dashboard Features
- Real-time agent connection status
- Transaction volume tracking
- Active vs total agents count
- Last seen timestamps

## Performance Optimizations

1. **Database:** File-based H2 with automatic indexes
2. **Sync:** 30-second interval prevents overwhelming master
3. **Dashboard:** 5-second polling with efficient queries
4. **Frontend:** React memo, debouncing, lazy loading

## Testing Strategy

### Backend Testing
- Unit tests for services
- Integration tests for controllers
- Profile-specific bean loading tests

### Frontend Testing
- Component tests (React Testing Library)
- Utility function tests
- API integration tests

## Differences from Company Implementation

**Intentionally Different (to avoid IP concerns):**
- ✅ Used `@EventListener` vs `ApplicationRunner`
- ✅ UUID API keys vs custom scheme
- ✅ master/agent terminology vs central/site
- ✅ H2 database vs MySQL
- ✅ Programmatic DB config vs YAML-based

## Future Enhancements

### Priority 1
- [ ] API key persistence to database
- [ ] Agent interface UI (React)
- [ ] Customer entity and balance tracking

### Priority 2
- [ ] Pull sync (agents pulling from each other)
- [ ] Advanced dashboard filters
- [ ] Charts and visualizations (Recharts)
- [ ] Transaction search

### Priority 3
- [ ] Docker Compose setup
- [ ] CI/CD pipeline
- [ ] Production deployment guide
- [ ] Performance benchmarks

## Conclusion

This architecture demonstrates:
- ✅ Distributed systems design
- ✅ Profile-based deployment patterns
- ✅ Feature-based code organization
- ✅ Real-time monitoring
- ✅ Full-stack development (Spring Boot + React + TypeScript)
- ✅ Clean code with comprehensive comments
- ✅ Professional git history

Built as a demo for Skypay freelance application, showcasing practical distributed payment network implementation with modern technologies.
