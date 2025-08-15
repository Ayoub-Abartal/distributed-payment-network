// Customer balance information
export interface Customer {
  id: string;
  phoneNumber: string;
  name: string;
  balance: number;
  createdAt: string;
  lastTransactionAt?: string;
}
