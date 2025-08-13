# Agent Interface

React + TypeScript interface for payment agents to process deposits and withdrawals.

## Features

- Create deposit transactions
- Create withdrawal transactions
- View local transaction history
- Auto-refresh every 5 seconds
- Phone number validation
- Amount validation
- Real-time sync status

## Tech Stack

- React 19
- TypeScript
- Tailwind CSS 3
- Axios

## Getting Started

### Prerequisites

- Node.js 16+
- Payment network backend running

### Installation

```bash
npm install
```

### Running Agent 1

```bash
npm run start:agent1
```

This starts the interface on port 3001 connected to AGENT1 (port 8081)

### Running Agent 2

```bash
npm run start:agent2
```

This starts the interface on port 3002 connected to AGENT2 (port 8082)

### Custom Configuration

Create a `.env` file:

```
REACT_APP_AGENT_API_URL=http://localhost:8081
REACT_APP_AGENT_ID=AGENT1
```

## Project Structure

```
src/
├── features/
│   └── transaction/
│       ├── components/       # Transaction UI components
│       ├── hooks/           # React hooks
│       ├── services/        # API calls
│       ├── types/           # TypeScript types
│       └── utils/           # Helper functions
├── shared/
│   └── components/          # Reusable components
└── pages/                   # Main pages
```

## Usage

1. Select transaction type (Deposit or Withdrawal)
2. Enter customer phone number (must be Moroccan format: 06XXXXXXXX or 07XXXXXXXX)
3. Enter amount in MAD
4. Submit transaction
5. View transaction in the list with sync status
