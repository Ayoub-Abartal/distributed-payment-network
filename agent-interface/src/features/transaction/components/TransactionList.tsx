import React from 'react';
import { Card } from '../../../shared/components';
import { Transaction } from '../types/transaction.types';
import { formatCurrency } from '../utils/currencyFormatter';
import { formatDate, formatRelativeTime } from '../utils/dateFormatter';

interface TransactionListProps {
  transactions: Transaction[];
}

// Show list of recent transactions
export const TransactionList: React.FC<TransactionListProps> = ({ transactions }) => {
  if (transactions.length === 0) {
    return (
      <Card>
        <h2 className="text-2xl font-bold mb-4 text-gray-800">Recent Transactions</h2>
        <div className="text-center py-8 text-gray-500">
          <p className="text-4xl mb-2">📋</p>
          <p>No transactions yet</p>
        </div>
      </Card>
    );
  }

  return (
    <Card>
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold text-gray-800">Recent Transactions</h2>
        <span className="text-sm text-gray-500">{transactions.length} total</span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b">
              <th className="text-left py-3 px-2 text-sm font-medium text-gray-600">Type</th>
              <th className="text-left py-3 px-2 text-sm font-medium text-gray-600">Customer</th>
              <th className="text-right py-3 px-2 text-sm font-medium text-gray-600">Amount</th>
              <th className="text-left py-3 px-2 text-sm font-medium text-gray-600">Status</th>
              <th className="text-left py-3 px-2 text-sm font-medium text-gray-600">Time</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((tx) => (
              <tr key={tx.id} className="border-b hover:bg-gray-50">
                {/* Type */}
                <td className="py-3 px-2">
                  <span
                    className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                      tx.type === 'DEPOSIT'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-blue-100 text-blue-800'
                    }`}
                  >
                    {tx.type === 'DEPOSIT' ? '💰 Deposit' : '💳 Withdrawal'}
                  </span>
                </td>

                {/* Customer */}
                <td className="py-3 px-2 text-sm text-gray-900">{tx.customerPhone}</td>

                {/* Amount */}
                <td className="py-3 px-2 text-sm font-medium text-right">
                  <span className={tx.type === 'DEPOSIT' ? 'text-green-600' : 'text-blue-600'}>
                    {tx.type === 'DEPOSIT' ? '+' : '-'}
                    {formatCurrency(tx.amount)}
                  </span>
                </td>

                {/* Status */}
                <td className="py-3 px-2">
                  <span
                    className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                      tx.status === 'SYNCED'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-yellow-100 text-yellow-800'
                    }`}
                  >
                    {tx.status === 'SYNCED' ? '✅ Synced' : '🔄 Pending'}
                  </span>
                </td>

                {/* Time */}
                <td className="py-3 px-2 text-sm text-gray-600" title={formatDate(tx.createdAt)}>
                  {formatRelativeTime(tx.createdAt)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  );
};
