// Transaction helper utilities
// Functions for working with transaction data

import { Transaction } from '../types';

// Get color classes for transaction type badges
// DEPOSIT = green, WITHDRAWAL = red
export const getTransactionTypeColor = (type: Transaction['type']): string => {
  return type === 'DEPOSIT'
    ? 'text-green-600 bg-green-50'
    : 'text-red-600 bg-red-50';
};

// Get color classes for transaction status badges
export const getTransactionStatusColor = (status: Transaction['status']): string => {
  switch (status) {
    case 'SYNCED':
      return 'text-green-700 bg-green-100';
    case 'PENDING_SYNC':
      return 'text-yellow-700 bg-yellow-100';
    case 'FAILED':
      return 'text-red-700 bg-red-100';
    default:
      return 'text-gray-700 bg-gray-100';
  }
};

// Sort transactions by timestamp (newest first)
export const sortTransactionsByDate = (transactions: Transaction[]): Transaction[] => {
  return [...transactions].sort((a, b) =>
    new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
  );
};

// Filter transactions by type
export const filterByType = (
  transactions: Transaction[],
  type: Transaction['type']
): Transaction[] => {
  return transactions.filter(t => t.type === type);
};

// Filter transactions by status
export const filterByStatus = (
  transactions: Transaction[],
  status: Transaction['status']
): Transaction[] => {
  return transactions.filter(t => t.status === status);
};

// Filter transactions by agent
export const filterByAgent = (
  transactions: Transaction[],
  agentId: string
): Transaction[] => {
  return transactions.filter(t => t.agentId === agentId);
};

// Calculate total amount for transactions
export const calculateTotalAmount = (transactions: Transaction[]): number => {
  return transactions.reduce((sum, t) => sum + t.amount, 0);
};

// Get transactions for a specific date range
export const filterByDateRange = (
  transactions: Transaction[],
  startDate: Date,
  endDate: Date
): Transaction[] => {
  return transactions.filter(t => {
    const txDate = new Date(t.timestamp);
    return txDate >= startDate && txDate <= endDate;
  });
};

// Group transactions by agent ID
export const groupByAgent = (transactions: Transaction[]): Record<string, Transaction[]> => {
  return transactions.reduce((groups, transaction) => {
    const agentId = transaction.agentId;
    if (!groups[agentId]) {
      groups[agentId] = [];
    }
    groups[agentId].push(transaction);
    return groups;
  }, {} as Record<string, Transaction[]>);
};
