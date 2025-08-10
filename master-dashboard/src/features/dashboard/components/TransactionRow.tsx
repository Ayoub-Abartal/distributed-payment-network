// Single transaction row component
// Shows one transaction in the table

import React from 'react';
import { Transaction } from '../types';
import { formatAmount } from '../utils/currencyFormatters';
import { formatTimestamp } from '../utils/dateFormatters';
import {
  getTransactionTypeColor,
  getTransactionStatusColor,
} from '../utils/transactionHelpers';

interface TransactionRowProps {
  transaction: Transaction;
  onClick?: (transaction: Transaction) => void;
}

export const TransactionRow: React.FC<TransactionRowProps> = ({
  transaction,
  onClick,
}) => {
  const handleClick = () => {
    if (onClick) {
      onClick(transaction);
    }
  };

  return (
    <tr
      className={`hover:bg-gray-50 ${onClick ? 'cursor-pointer' : ''}`}
      onClick={handleClick}
    >
      {/* Transaction Type */}
      <td className="px-6 py-4 whitespace-nowrap">
        <span
          className={`px-2 py-1 text-xs font-semibold rounded ${getTransactionTypeColor(
            transaction.type
          )}`}
        >
          {transaction.type}
        </span>
      </td>

      {/* Amount */}
      <td className="px-6 py-4 whitespace-nowrap">
        <span className="text-sm font-medium text-gray-900">
          {formatAmount(transaction.amount)}
        </span>
      </td>

      {/* Customer Phone */}
      <td className="px-6 py-4 whitespace-nowrap">
        <span className="text-sm text-gray-500">
          {transaction.customerPhone}
        </span>
      </td>

      {/* Agent ID */}
      <td className="px-6 py-4 whitespace-nowrap">
        <span className="text-sm text-gray-900">{transaction.agentId}</span>
      </td>

      {/* Status */}
      <td className="px-6 py-4 whitespace-nowrap">
        <span
          className={`px-2 py-1 text-xs font-semibold rounded ${getTransactionStatusColor(
            transaction.status
          )}`}
        >
          {transaction.status}
        </span>
      </td>

      {/* Timestamp */}
      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
        {formatTimestamp(transaction.timestamp)}
      </td>
    </tr>
  );
};
