import React, { useState } from 'react';
import { Card, Button, Input } from '../../../shared/components';
import { CreateCustomerRequest } from '../types/customer.types';

interface CreateCustomerFormProps {
  onSubmit: (request: CreateCustomerRequest) => Promise<void>;
  loading: boolean;
}

export const CreateCustomerForm: React.FC<CreateCustomerFormProps> = ({ onSubmit, loading }) => {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [name, setName] = useState('');
  const [initialBalance, setInitialBalance] = useState('');
  const [errors, setErrors] = useState<{ phone?: string; name?: string; balance?: string }>({});

  const validate = (): boolean => {
    const newErrors: { phone?: string; name?: string; balance?: string } = {};

    if (!phoneNumber.trim()) {
      newErrors.phone = 'Phone number is required';
    } else if (!/^0[67]\d{8}$/.test(phoneNumber)) {
      newErrors.phone = 'Invalid Moroccan phone (must be 06XXXXXXXX or 07XXXXXXXX)';
    }

    if (!name.trim()) {
      newErrors.name = 'Name is required';
    } else if (name.trim().length < 2) {
      newErrors.name = 'Name must be at least 2 characters';
    }

    if (initialBalance) {
      const balanceNum = parseFloat(initialBalance);
      if (isNaN(balanceNum)) {
        newErrors.balance = 'Balance must be a number';
      } else if (balanceNum < 0) {
        newErrors.balance = 'Balance cannot be negative';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    const request: CreateCustomerRequest = {
      phoneNumber,
      name: name.trim(),
      initialBalance: initialBalance ? parseFloat(initialBalance) : 0,
    };

    try {
      await onSubmit(request);
      // Reset form on success
      setPhoneNumber('');
      setName('');
      setInitialBalance('');
      setErrors({});
    } catch (error) {
      // Error handled by parent
    }
  };

  return (
    <Card>
      <h2 className="text-2xl font-bold mb-6 text-gray-800">Create New Customer</h2>

      <form onSubmit={handleSubmit}>
        <Input
          label="Phone Number"
          type="tel"
          value={phoneNumber}
          onChange={setPhoneNumber}
          placeholder="0612345678"
          required
          error={errors.phone}
          disabled={loading}
        />

        <Input
          label="Customer Name"
          type="text"
          value={name}
          onChange={setName}
          placeholder="John Doe"
          required
          error={errors.name}
          disabled={loading}
        />

        <Input
          label="Initial Balance (MAD)"
          type="number"
          value={initialBalance}
          onChange={setInitialBalance}
          placeholder="0.00"
          error={errors.balance}
          disabled={loading}
        />

        <Button type="submit" variant="primary" disabled={loading} fullWidth>
          {loading ? 'Creating...' : 'Create Customer'}
        </Button>
      </form>
    </Card>
  );
};
