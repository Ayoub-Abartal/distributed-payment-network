import { useState, useEffect } from 'react';
import { Agent, Transaction, DashboardMetrics } from '../types';
import { dashboardApi } from '../services/dashboardApi';

export const useDashboardData = (refreshInterval: number = 5000) => {
  const [agents, setAgents] = useState<Agent[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [agentsData, transactionsData, metricsData] = await Promise.all([
        dashboardApi.getAgents(),
        dashboardApi.getTransactions(),
        dashboardApi.getMetrics(),
      ]);

      setAgents(agentsData);
      setTransactions(transactionsData);
      setMetrics(metricsData);
      setError(null);
    } catch (err) {
      setError('Failed to fetch dashboard data');
      console.error('Dashboard data fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // Initial fetch
    fetchData();

    // Set up polling
    const interval = setInterval(fetchData, refreshInterval);

    return () => clearInterval(interval);
  }, [refreshInterval]);

  return { agents, transactions, metrics, loading, error, refetch: fetchData };
};