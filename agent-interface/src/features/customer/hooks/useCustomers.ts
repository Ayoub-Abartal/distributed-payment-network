import { useState, useEffect } from 'react';
import { Customer, CreateCustomerRequest } from '../types/customer.types';
import { getAllCustomers, createCustomer } from '../services/customerService';

export const useCustomers = (refreshInterval: number = 5000) => {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const data = await getAllCustomers();
      setCustomers(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch customers');
    } finally {
      setLoading(false);
    }
  };

  const createNewCustomer = async (request: CreateCustomerRequest): Promise<Customer> => {
    try {
      const newCustomer = await createCustomer(request);
      setCustomers((prev) => [newCustomer, ...prev]);
      setError(null);
      return newCustomer;
    } catch (err: any) {
      const errorMsg = err.response?.data?.message || 'Failed to create customer';
      setError(errorMsg);
      throw new Error(errorMsg);
    }
  };

  useEffect(() => {
    fetchCustomers();

    // Set up polling
    const interval = setInterval(fetchCustomers, refreshInterval);
    return () => clearInterval(interval);
  }, [refreshInterval]);

  return {
    customers,
    loading,
    error,
    createNewCustomer,
    refreshCustomers: fetchCustomers,
  };
};
