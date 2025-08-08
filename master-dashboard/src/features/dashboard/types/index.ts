export interface Agent {
  id: string;
  businessName: string;
  ownerName: string;
  phoneNumber: string;
  location: string;
  status: 'PENDING' | 'ACTIVE' | 'SUSPENDED' | 'REJECTED';
  registeredAt: string;
  lastSeenAt: string | null;
}

export interface Transaction {
  id: string;
  agentId: string;
  customerPhone: string;
  type: 'DEPOSIT' | 'WITHDRAWAL';
  amount: number;
  status: 'PENDING_SYNC' | 'SYNCED' | 'FAILED';
  timestamp: string;
}

export interface DashboardMetrics {
  totalAgents: number;
  activeAgents: number;
  totalTransactions: number;
  totalVolume: number;
}

export interface AgentWithStats extends Agent {
  transactionCount: number;
  lastSeenFormatted: string;
  connectionStatus: 'ONLINE' | 'WARNING' | 'OFFLINE';
}