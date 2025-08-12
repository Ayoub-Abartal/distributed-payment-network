// Transaction types from backend
export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL';

export type SyncStatus = 'PENDING_SYNC' | 'SYNCED';

// What the backend expects when creating a transaction
export interface TransactionRequest {
  customerPhone: string;
  type: TransactionType;
  amount: number;
}

// What the backend returns
export interface Transaction {
  id: string;
  agentId: string;
  customerPhone: string;
  type: TransactionType;
  amount: number;
  status: SyncStatus;
  createdAt: string;
  syncedAt?: string;
}

export interface TransactionResponse {
  id: string;
  agentId: string;
  customerPhone: string;
  type: TransactionType;
  amount: number;
  status: SyncStatus;
  createdAt: string;
  syncedAt?: string;
  message?: string;
}
