# Sync Strategy Architecture

## Overview
Hybrid sync system supporting HTTP, Kafka, and combined strategies for distributed payment network synchronization.

## Architecture Layers
1. Foundation (common types)
2. Events (DTOs)
3. Entity Strategies (per-entity sync)
4. Batch Strategies (atomic multi-entity sync)
5. Factory (strategy creation)

## Components

### Foundation (`strategy/common/`)
- EntityType
- SyncStrategyType
- SyncResult
- SyncEvent

### Events (`events/`)
- TransactionEvent
- CustomerEvent

### Strategies
- Entity-level (fine-grained)
- Batch-level (atomic)

## How to Add New Entity Type

## How to Add New Transport Strategy

## Design Decisions

## Future Enhancements
