import axios from 'axios';
import { Transaction, TransactionRequest, TransactionResponse } from '../types/transaction.types';

// Base URL for agent API - can be configured via env variable
const API_BASE_URL = process.env.REACT_APP_AGENT_API_URL || 'http://localhost:8081';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create a new transaction (deposit or withdrawal)
export const createTransaction = async (request: TransactionRequest): Promise<TransactionResponse> => {
  const response = await api.post<TransactionResponse>('/api/agent/transactions', request);
  return response.data;
};

// Get all local transactions from this agent
export const getLocalTransactions = async (): Promise<Transaction[]> => {
  const response = await api.get<Transaction[]>('/api/agent/transactions');
  return response.data;
};
