import React from 'react';
import { Customer } from '../types/customer.types';
import { formatCurrency } from '../utils/currencyFormatter';
import { Card } from '../../../shared/components';

interface CustomerBalanceProps {
  customer: Customer | null;
  lookingUp: boolean;
  customerPhone: string;
}

// Shows customer balance info after looking up by phone
export const CustomerBalance: React.FC<CustomerBalanceProps> = ({
  customer,
  lookingUp,
  customerPhone,
}) => {
  // Still looking up
  if (lookingUp) {
    return (
      <div className="mb-4 p-3 bg-gray-100 rounded-lg text-sm text-gray-600">
        Looking up customer...
      </div>
    );
  }

  // Customer found
  if (customer) {
    return (
      <Card className="mb-4">
        <div className="flex justify-between items-center mb-3">
          <div>
            <p className="text-xs text-gray-500">Customer</p>
            <p className="text-lg font-bold text-gray-900">{customer.name}</p>
          </div>
          <div className="text-right">
            <p className="text-xs text-gray-500">Balance</p>
            <p className="text-2xl font-bold text-blue-600">{formatCurrency(customer.balance)}</p>
          </div>
        </div>
        <div className="text-xs text-gray-500">
          <p>Phone: {customer.phoneNumber}</p>
          {customer.lastTransactionAt && (
            <p>Last transaction: {new Date(customer.lastTransactionAt).toLocaleString()}</p>
          )}
        </div>
      </Card>
    );
  }

  // New customer (phone is valid but not found)
  if (customerPhone.length === 10 && /^0[67]\d{8}$/.test(customerPhone)) {
    return (
      <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-700">
        📝 New customer - Account will be created
      </div>
    );
  }

  return null;
};
