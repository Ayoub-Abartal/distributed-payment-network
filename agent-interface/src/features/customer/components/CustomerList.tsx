import React, { useState } from 'react';
import { Card } from '../../../shared/components';
import { Customer } from '../types/customer.types';
import { formatCurrency } from '../../transaction/utils/currencyFormatter';
import { formatRelativeTime } from '../../transaction/utils/dateFormatter';

interface CustomerListProps {
  customers: Customer[];
  onSelectCustomer?: (customer: Customer) => void;
}

export const CustomerList: React.FC<CustomerListProps> = ({ customers, onSelectCustomer }) => {
  const [searchTerm, setSearchTerm] = useState('');

  // Filter customers by search term
  const filteredCustomers = customers.filter(
    (customer) =>
      customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      customer.phoneNumber.includes(searchTerm)
  );

  // Sort by last transaction date (most recent first)
  const sortedCustomers = [...filteredCustomers].sort((a, b) => {
    if (!a.lastTransactionAt && !b.lastTransactionAt) return 0;
    if (!a.lastTransactionAt) return 1;
    if (!b.lastTransactionAt) return -1;
    return new Date(b.lastTransactionAt).getTime() - new Date(a.lastTransactionAt).getTime();
  });

  return (
    <Card>
      <div className="mb-4">
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Customers</h2>
        <p className="text-sm text-gray-500 mb-3">
          {customers.length} customer{customers.length !== 1 ? 's' : ''} registered
        </p>

        {/* Search bar */}
        <input
          type="text"
          placeholder="Search by name or phone..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* Customer list */}
      <div className="max-h-[500px] overflow-y-auto space-y-3">
        {sortedCustomers.length === 0 ? (
          <EmptyCustomerList searchTerm={searchTerm} />
        ) : (
          sortedCustomers.map((customer) => (
            <CustomerCard
              key={customer.id}
              customer={customer}
              onClick={onSelectCustomer ? () => onSelectCustomer(customer) : undefined}
            />
          ))
        )}
      </div>
    </Card>
  );
};

// Individual customer card
const CustomerCard: React.FC<{ customer: Customer; onClick?: () => void }> = ({
  customer,
  onClick,
}) => {
  return (
    <div
      className={`p-4 border border-gray-200 rounded-lg hover:border-blue-400 transition-colors ${
        onClick ? 'cursor-pointer hover:bg-blue-50' : ''
      }`}
      onClick={onClick}
    >
      <div className="flex justify-between items-start mb-2">
        <div>
          <p className="font-bold text-gray-900">{customer.name}</p>
          <p className="text-sm text-gray-500">{customer.phoneNumber}</p>
        </div>
        <div className="text-right">
          <p className="text-lg font-bold text-blue-600">{formatCurrency(customer.balance)}</p>
          <p className="text-xs text-gray-500">Balance</p>
        </div>
      </div>

      {customer.lastTransactionAt && (
        <p className="text-xs text-gray-400">
          Last transaction: {formatRelativeTime(customer.lastTransactionAt)}
        </p>
      )}
    </div>
  );
};

// Empty state
const EmptyCustomerList: React.FC<{ searchTerm: string }> = ({ searchTerm }) => {
  if (searchTerm) {
    return (
      <div className="text-center py-8 text-gray-500">
        <p className="text-4xl mb-2">🔍</p>
        <p className="text-sm">No customers found matching "{searchTerm}"</p>
      </div>
    );
  }

  return (
    <div className="text-center py-8 text-gray-500">
      <p className="text-6xl mb-4">👥</p>
      <h3 className="text-lg font-medium text-gray-900 mb-2">No Customers Yet</h3>
      <p className="text-sm">Create a new customer to get started</p>
    </div>
  );
};
