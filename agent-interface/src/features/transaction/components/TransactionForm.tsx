import React, { useState, useEffect } from 'react';
import { Card, Button, Input } from '../../../shared/components';
import { TransactionRequest, TransactionType } from '../types/transaction.types';
import { Customer } from '../types/customer.types';
import { getCustomerByPhone } from '../services/transactionService';
import { formatCurrency } from '../utils/currencyFormatter';

interface TransactionFormProps {
  onSubmit: (request: TransactionRequest) => Promise<void>;
  loading: boolean;
}

// Form to create deposit or withdrawal transactions
export const TransactionForm: React.FC<TransactionFormProps> = ({ onSubmit, loading }) => {
  const [customerPhone, setCustomerPhone] = useState('');
  const [amount, setAmount] = useState('');
  const [type, setType] = useState<TransactionType>('DEPOSIT');
  const [errors, setErrors] = useState<{ phone?: string; amount?: string }>({});
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [lookingUp, setLookingUp] = useState(false);

  // Lookup customer when phone number is valid
  useEffect(() => {
    const lookupCustomer = async () => {
      if (!/^0[67]\d{8}$/.test(customerPhone)) {
        setCustomer(null);
        return;
      }

      setLookingUp(true);
      const result = await getCustomerByPhone(customerPhone);
      setCustomer(result);
      setLookingUp(false);
    };

    const timer = setTimeout(lookupCustomer, 500); // Debounce
    return () => clearTimeout(timer);
  }, [customerPhone]);

  // Basic validation before submitting
  const validate = (): boolean => {
    const newErrors: { phone?: string; amount?: string } = {};

    if (!customerPhone.trim()) {
      newErrors.phone = 'Phone number is required';
    } else if (!/^0[67]\d{8}$/.test(customerPhone)) {
      newErrors.phone = 'Invalid Moroccan phone (must be 06XXXXXXXX or 07XXXXXXXX)';
    }

    const amountNum = parseFloat(amount);
    if (!amount || isNaN(amountNum)) {
      newErrors.amount = 'Amount is required';
    } else if (amountNum <= 0) {
      newErrors.amount = 'Amount must be positive';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    const request: TransactionRequest = {
      customerPhone,
      type,
      amount: parseFloat(amount),
    };

    try {
      await onSubmit(request);
      // Reset form on success
      setCustomerPhone('');
      setAmount('');
      setErrors({});
      setCustomer(null);
    } catch (error) {
      // Error handled by parent
    }
  };

  return (
    <Card>
      <h2 className="text-2xl font-bold mb-6 text-gray-800">New Transaction</h2>

      <form onSubmit={handleSubmit}>
        {/* Transaction Type Selection */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Transaction Type <span className="text-red-500">*</span>
          </label>
          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => setType('DEPOSIT')}
              className={`flex-1 py-3 rounded-lg font-medium transition-colors ${
                type === 'DEPOSIT'
                  ? 'bg-green-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              💰 Deposit
            </button>
            <button
              type="button"
              onClick={() => setType('WITHDRAWAL')}
              className={`flex-1 py-3 rounded-lg font-medium transition-colors ${
                type === 'WITHDRAWAL'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              💳 Withdrawal
            </button>
          </div>
        </div>

        {/* Customer Phone */}
        <Input
          label="Customer Phone"
          type="tel"
          value={customerPhone}
          onChange={setCustomerPhone}
          placeholder="0612345678"
          required
          error={errors.phone}
          disabled={loading}
        />

        {/* Customer Balance Display */}
        {lookingUp && (
          <div className="mb-4 p-3 bg-gray-100 rounded-lg text-sm text-gray-600">
            Looking up customer...
          </div>
        )}

        {customer && !lookingUp && (
          <div className="mb-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <div className="flex justify-between items-center">
              <div>
                <p className="text-sm font-medium text-gray-700">Customer Balance</p>
                <p className="text-2xl font-bold text-blue-600">{formatCurrency(customer.balance)}</p>
              </div>
              <div className="text-right">
                <p className="text-xs text-gray-500">Customer Name</p>
                <p className="text-sm font-medium text-gray-700">{customer.name}</p>
              </div>
            </div>
            {type === 'WITHDRAWAL' && customer.balance < parseFloat(amount || '0') && (
              <p className="mt-2 text-sm text-red-600 font-medium">
                ⚠️ Insufficient balance for this withdrawal
              </p>
            )}
          </div>
        )}

        {!customer && !lookingUp && customerPhone.length === 10 && /^0[67]\d{8}$/.test(customerPhone) && (
          <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-700">
            📝 New customer - Account will be created
          </div>
        )}

        {/* Amount */}
        <Input
          label="Amount (MAD)"
          type="number"
          value={amount}
          onChange={setAmount}
          placeholder="100.00"
          required
          error={errors.amount}
          disabled={loading}
        />

        {/* Submit Button */}
        <Button
          type="submit"
          variant={type === 'DEPOSIT' ? 'success' : 'primary'}
          disabled={loading}
          fullWidth
        >
          {loading ? 'Processing...' : `Submit ${type === 'DEPOSIT' ? 'Deposit' : 'Withdrawal'}`}
        </Button>
      </form>
    </Card>
  );
};
