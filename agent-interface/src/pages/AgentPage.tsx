import React, { useState, useEffect } from 'react';
import { TransactionForm } from '../features/transaction/components/TransactionForm';
import { TransactionList } from '../features/transaction/components/TransactionList';
import { useTransactions } from '../features/transaction/hooks/useTransactions';

export const AgentPage: React.FC = () => {
  const { transactions, loading, error, submitTransaction, refreshTransactions } = useTransactions(5000);
  const [agentName, setAgentName] = useState('Agent');
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Get agent name from env or URL
  useEffect(() => {
    const envAgentId = process.env.REACT_APP_AGENT_ID;
    if (envAgentId) {
      setAgentName(envAgentId);
    }
  }, []);

  // Handle form submission
  const handleSubmit = async (request: any) => {
    try {
      await submitTransaction(request);
      setSuccessMessage(`${request.type} of ${request.amount} MAD created successfully!`);
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err: any) {
      // Error already handled by hook
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">🏪 {agentName}</h1>
              <p className="text-gray-600 mt-1">Payment Agent Interface</p>
            </div>
            <button
              onClick={refreshTransactions}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              🔄 Refresh
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Success Message */}
        {successMessage && (
          <div className="mb-6 bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg">
            ✅ {successMessage}
          </div>
        )}

        {/* Error Message */}
        {error && (
          <div className="mb-6 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg">
            ❌ {error}
          </div>
        )}

        {/* Two Column Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Transaction Form */}
          <div>
            <TransactionForm onSubmit={handleSubmit} loading={loading} />
          </div>

          {/* Transaction List */}
          <div>
            <TransactionList transactions={transactions} />
          </div>
        </div>
      </main>
    </div>
  );
};
