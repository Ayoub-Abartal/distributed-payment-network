import React, { useState, useEffect } from 'react';
import { TransactionForm } from '../features/transaction/components/TransactionForm';
import { TransactionList } from '../features/transaction/components/TransactionList';
import { useTransactions } from '../features/transaction/hooks/useTransactions';
import { CustomersPage } from './CustomersPage';
import { Customer } from '../features/customer/types/customer.types';

type TabType = 'transactions' | 'customers';

export const AgentPage: React.FC = () => {
  const { transactions, loading, error, submitTransaction, refreshTransactions } = useTransactions(5000);
  const [agentName, setAgentName] = useState('Agent');
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<TabType>('transactions');
  const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);

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

  // Handle customer selection from customer list
  const handleSelectCustomer = (customer: Customer) => {
    setSelectedCustomer(customer);
    setActiveTab('transactions');
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

          {/* Navigation Tabs */}
          <div className="mt-6 border-b border-gray-200">
            <nav className="flex space-x-8">
              <button
                onClick={() => setActiveTab('transactions')}
                className={`py-3 px-1 border-b-2 font-medium text-sm transition-colors ${
                  activeTab === 'transactions'
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                💸 Transactions
              </button>
              <button
                onClick={() => setActiveTab('customers')}
                className={`py-3 px-1 border-b-2 font-medium text-sm transition-colors ${
                  activeTab === 'customers'
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                👥 Customers
              </button>
            </nav>
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

        {/* Tab Content */}
        {activeTab === 'transactions' ? (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Transaction Form */}
            <div>
              <TransactionForm
                onSubmit={handleSubmit}
                loading={loading}
                selectedCustomer={selectedCustomer}
                onClearCustomer={() => setSelectedCustomer(null)}
              />
            </div>

            {/* Transaction List */}
            <div>
              <TransactionList transactions={transactions} />
            </div>
          </div>
        ) : (
          <CustomersPage onSelectCustomer={handleSelectCustomer} />
        )}
      </main>
    </div>
  );
};
