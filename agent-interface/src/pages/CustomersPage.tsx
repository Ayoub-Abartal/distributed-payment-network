import React, { useState } from 'react';
import { useCustomers } from '../features/customer/hooks/useCustomers';
import { CreateCustomerForm } from '../features/customer/components/CreateCustomerForm';
import { CustomerList } from '../features/customer/components/CustomerList';
import { CreateCustomerRequest, Customer } from '../features/customer/types/customer.types';

interface CustomersPageProps {
  onSelectCustomer?: (customer: Customer) => void;
}

export const CustomersPage: React.FC<CustomersPageProps> = ({ onSelectCustomer }) => {
  const { customers, loading, error, createNewCustomer, refreshCustomers } = useCustomers(5000);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleCreateCustomer = async (request: CreateCustomerRequest) => {
    try {
      await createNewCustomer(request);
      setSuccessMessage(`Customer ${request.name} created successfully!`);
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err: any) {
      // Error already handled by hook
    }
  };

  return (
    <div>
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
        {/* Customer Creation Form */}
        <div>
          <CreateCustomerForm onSubmit={handleCreateCustomer} loading={loading} />
        </div>

        {/* Customer List */}
        <div>
          <CustomerList customers={customers} onSelectCustomer={onSelectCustomer} />
        </div>
      </div>
    </div>
  );
};
