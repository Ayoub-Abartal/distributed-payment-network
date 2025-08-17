import axios from 'axios';
import { Customer, CreateCustomerRequest } from '../types/customer.types';

// Base URL for agent API
const API_BASE_URL = process.env.REACT_APP_AGENT_API_URL || 'http://localhost:8081';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Get all customers from this agent
export const getAllCustomers = async (): Promise<Customer[]> => {
  const response = await api.get<Customer[]>('/api/agent/customers');
  return response.data;
};

// Get customer by phone number
export const getCustomerByPhone = async (phoneNumber: string): Promise<Customer | null> => {
  try {
    const response = await api.get<Customer>(`/api/agent/customers/${phoneNumber}`);
    return response.data;
  } catch (error) {
    return null;
  }
};

// Create a new customer
export const createCustomer = async (request: CreateCustomerRequest): Promise<Customer> => {
  const response = await api.post<Customer>('/api/agent/customers', request);
  return response.data;
};
