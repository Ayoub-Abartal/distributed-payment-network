import axios from 'axios';
import { Agent, Transaction, DashboardMetrics, Customer } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const dashboardApi = {
  // Get all agents
  getAgents: async (): Promise<Agent[]> => {
    const response = await axios.get(`${API_BASE_URL}/api/master/agents`);
    return response.data;
  },

  // Get all transactions
  getTransactions: async (): Promise<Transaction[]> => {
    const response = await axios.get(`${API_BASE_URL}/api/master/transactions`);
    return response.data;
  },

  // Get dashboard metrics
  getMetrics: async (): Promise<DashboardMetrics> => {
    const response = await axios.get(`${API_BASE_URL}/api/master/dashboard/metrics`);
    return response.data;
  },

  // Get all customers
  getCustomers: async (): Promise<Customer[]> => {
    const response = await axios.get(`${API_BASE_URL}/api/master/customers`);
    return response.data;
  },
};