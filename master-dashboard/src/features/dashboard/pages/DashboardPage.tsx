// Main dashboard page
// Combines all dashboard components into one view

import React from 'react';
import { useDashboardData } from '../hooks/useDashboardData';
import { MetricsCard } from '../components/MetricsCard';
import { AgentList } from '../components/AgentList';
import { TransactionList } from '../components/TransactionList';
import { LoadingSpinner } from '../components/shared/LoadingSpinner';
import { ErrorMessage } from '../components/shared/ErrorMessage';
import { formatCompactAmount } from '../utils/currencyFormatters';

export const DashboardPage: React.FC = () => {
  // Fetch dashboard data (polls every 5 seconds)
  const { agents, transactions, metrics, loading, error, refetch } = useDashboardData(5000);

  // Show loading state on first load
  if (loading && !metrics) {
    return (
      <div className="min-h-screen bg-gray-100">
        <LoadingSpinner size="lg" message="Loading dashboard..." />
      </div>
    );
  }

  // Show error state
  if (error) {
    return (
      <div className="min-h-screen bg-gray-100">
        <ErrorMessage
          title="Failed to load dashboard"
          message={error}
          onRetry={refetch}
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                Payment Network Dashboard
              </h1>
              <p className="text-sm text-gray-500 mt-1">
                Master node monitoring and management
              </p>
            </div>

            {/* Auto-refresh indicator */}
            <div className="text-xs text-gray-500">
              {loading ? (
                <span className="flex items-center">
                  <span className="animate-spin mr-2">🔄</span>
                  Refreshing...
                </span>
              ) : (
                <span>Auto-refresh: 5s</span>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Overall Metrics grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <MetricsCard
            title="Total Agents"
            value={metrics?.totalAgents || 0}
            icon="🏪"
            color="blue"
          />
          <MetricsCard
            title="Active Agents"
            value={metrics?.activeAgents || 0}
            icon="✅"
            color="green"
          />
          <MetricsCard
            title="Total Customers"
            value={metrics?.totalCustomers || 0}
            icon="👥"
            color="indigo"
          />
          <MetricsCard
            title="Total Volume"
            value={formatCompactAmount(metrics?.totalVolume || 0)}
            icon="💰"
            color="orange"
          />
        </div>

        {/* Today's Statistics */}
        <div className="mb-8">
          <h2 className="text-xl font-bold text-gray-800 mb-4">📊 Today's Activity</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <MetricsCard
              title="Transactions"
              value={metrics?.todayTransactions || 0}
              icon="📝"
              color="blue"
            />
            <MetricsCard
              title="Deposits"
              value={formatCompactAmount(metrics?.todayDeposits || 0)}
              icon="⬆️"
              color="green"
            />
            <MetricsCard
              title="Withdrawals"
              value={formatCompactAmount(metrics?.todayWithdrawals || 0)}
              icon="⬇️"
              color="red"
            />
            <MetricsCard
              title="Total Volume"
              value={formatCompactAmount(metrics?.todayVolume || 0)}
              icon="💵"
              color="purple"
            />
          </div>
        </div>

        {/* Two column layout for agents and transactions */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Agent list */}
          <AgentList agents={agents} />

          {/* Transaction list */}
          <TransactionList transactions={transactions} limit={10} />
        </div>

        {/* Full width transaction list */}
        <div className="mb-8">
          <TransactionList transactions={transactions} limit={20} />
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white shadow mt-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <p className="text-center text-sm text-gray-500">
            Distributed Payment Network Demo - Master Dashboard
          </p>
        </div>
      </footer>
    </div>
  );
};
