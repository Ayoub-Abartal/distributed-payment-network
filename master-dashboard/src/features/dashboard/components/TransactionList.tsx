// Main transaction list component
// Shows recent transactions from all agents in a table

import React from 'react';
import { Transaction } from '../types';
import { sortTransactionsByDate } from '../utils/transactionHelpers';
import { TransactionTableHeader } from './TransactionTableHeader';
import { TransactionRow } from './TransactionRow';

interface TransactionListProps {
  transactions: Transaction[];
  limit?: number; // How many to show
  onTransactionClick?: (transaction: Transaction) => void;
}

export const TransactionList: React.FC<TransactionListProps> = ({
  transactions,
  limit = 20,
  onTransactionClick,
}) => {
  // Sort and limit the transactions
  const displayedTransactions = sortTransactionsByDate(transactions).slice(0, limit);

  return (
    <div className="bg-white rounded-lg shadow">
      {/* Header */}
      <div className="px-6 py-4 border-b border-gray-200">
        <h2 className="text-lg font-semibold text-gray-900">
          Recent Transactions
        </h2>
        <p className="text-sm text-gray-500 mt-1">
          Showing {displayedTransactions.length} of {transactions.length} transactions
        </p>
      </div>

      {/* Transaction table */}
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <TransactionTableHeader />
          <tbody className="bg-white divide-y divide-gray-200">
            {displayedTransactions.length === 0 ? (
              <EmptyTransactionList />
            ) : (
              displayedTransactions.map((transaction) => (
                <TransactionRow
                  key={transaction.id}
                  transaction={transaction}
                  onClick={onTransactionClick}
                />
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

// Shows when there are no transactions
const EmptyTransactionList: React.FC = () => {
  return (
    <tr>
      <td colSpan={6} className="px-6 py-12 text-center">
        <div className="text-6xl mb-4">💸</div>
        <h3 className="text-lg font-medium text-gray-900 mb-2">
          No Transactions Yet
        </h3>
        <p className="text-sm text-gray-500">
          Transactions will appear here once agents start processing deposits and withdrawals.
        </p>
      </td>
    </tr>
  );
};
