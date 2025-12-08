import React, { useState } from 'react';
import { Customer } from '../types';
import { formatTimestamp } from '../utils/dateFormatters';

interface CustomerListProps {
  customers: Customer[];
}

// Display list of customers with their balances
export const CustomerList: React.FC<CustomerListProps> = ({ customers }) => {
  const [searchTerm, setSearchTerm] = useState('');

  // Filter customers by phone or name
  const filteredCustomers = customers.filter(
    (customer) =>
      customer.phoneNumber.includes(searchTerm) ||
      customer.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Sort by balance (highest first)
  const sortedCustomers = [...filteredCustomers].sort((a, b) => b.balance - a.balance);

  if (customers.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-bold mb-4 text-gray-800">👥 Customers</h2>
        <div className="text-center py-8 text-gray-500">
          <p className="text-4xl mb-2">👥</p>
          <p>No customers yet</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-gray-800">👥 Customers</h2>
        <span className="text-sm text-gray-500">{customers.length} total</span>
      </div>

      {/* Search bar */}
      <div className="mb-4">
        <input
          type="text"
          placeholder="Search by phone or name..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* Customer list */}
      <div className="space-y-3 max-h-96 overflow-y-auto">
        {sortedCustomers.map((customer) => (
          <div
            key={customer.id}
            className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors"
          >
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <p className="font-medium text-gray-900">{customer.name}</p>
                  <span className="text-xs text-gray-500">{customer.phoneNumber}</span>
                </div>
                <p className="text-xs text-gray-500 mt-1">
                  {customer.lastTransactionAt
                    ? `Last transaction: ${formatTimestamp(customer.lastTransactionAt)}`
                    : 'No transactions yet'}
                </p>
              </div>
              <div className="text-right">
                <p className="text-lg font-bold text-blue-600">
                  {customer.balance.toFixed(2)} MAD
                </p>
                <p className="text-xs text-gray-500">Balance</p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {filteredCustomers.length === 0 && searchTerm && (
        <div className="text-center py-4 text-gray-500">
          <p>No customers found matching "{searchTerm}"</p>
        </div>
      )}
    </div>
  );
};
