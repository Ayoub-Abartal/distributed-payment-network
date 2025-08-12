import { useState, useEffect, useCallback } from 'react';
import { Transaction, TransactionRequest } from '../types/transaction.types';
import { createTransaction, getLocalTransactions } from '../services/transactionService';

interface UseTransactionsReturn {
  transactions: Transaction[];
  loading: boolean;
  error: string | null;
  submitTransaction: (request: TransactionRequest) => Promise<void>;
  refreshTransactions: () => void;
}

// Hook to manage transaction data and operations
export const useTransactions = (autoRefreshMs: number = 5000): UseTransactionsReturn => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Fetch transactions from API
  const fetchTransactions = useCallback(async () => {
    try {
      const data = await getLocalTransactions();
      setTransactions(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch transactions');
      console.error('Error fetching transactions:', err);
    }
  }, []);

  // Submit new transaction
  const submitTransaction = async (request: TransactionRequest) => {
    setLoading(true);
    setError(null);

    try {
      await createTransaction(request);
      // Refresh list after creating
      await fetchTransactions();
    } catch (err: any) {
      const errorMsg = err.response?.data?.message || err.message || 'Failed to create transaction';
      setError(errorMsg);
      throw new Error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  // Load transactions on mount
  useEffect(() => {
    fetchTransactions();
  }, [fetchTransactions]);

  // Auto-refresh transactions
  useEffect(() => {
    if (autoRefreshMs > 0) {
      const interval = setInterval(fetchTransactions, autoRefreshMs);
      return () => clearInterval(interval);
    }
  }, [autoRefreshMs, fetchTransactions]);

  return {
    transactions,
    loading,
    error,
    submitTransaction,
    refreshTransactions: fetchTransactions,
  };
};
