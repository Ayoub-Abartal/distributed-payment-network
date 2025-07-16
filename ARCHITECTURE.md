# Architecture Overview

## Structure

- **master-service**: Central coordination Spring Boot service
- **agent-service**: Autonomous agent Spring Boot service (can run multiple instances)
- **common-lib**: Shared DTOs and exceptions
- **master-dashboard**: React + TypeScript dashboard for master
- **agent-interface**: React + TypeScript interface for agents

## Technology Stack

**Backend:**
- Spring Boot 3.x
- H2 Database
- REST API
- Scheduled synchronization

**Frontend:**
- React 18
- TypeScript
- Axios
- Zod
- React-hook-form
- Tailwind CSS

## Communication

- Agent → Master: REST API push (scheduled every 30s)
- Master → Agent: REST API pull (on-demand)
- Conflict resolution: Timestamp-based (last-write-wins)
