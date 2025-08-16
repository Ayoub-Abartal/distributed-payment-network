// Color constants
// All the color mappings used across the dashboard

// Metrics card colors
export const METRICS_CARD_COLORS = {
  blue: 'bg-blue-50 text-blue-600',
  green: 'bg-green-50 text-green-600',
  purple: 'bg-purple-50 text-purple-600',
  orange: 'bg-orange-50 text-orange-600',
  red: 'bg-red-50 text-red-600',
  yellow: 'bg-yellow-50 text-yellow-600',
  gray: 'bg-gray-50 text-gray-600',
  indigo: 'bg-indigo-50 text-indigo-600',
} as const;

// Agent status colors (for registration status badges)
export const AGENT_STATUS_COLORS = {
  ACTIVE: 'bg-green-100 text-green-800',
  PENDING: 'bg-yellow-100 text-yellow-800',
  SUSPENDED: 'bg-red-100 text-red-800',
  REJECTED: 'bg-gray-100 text-gray-800',
} as const;

// Connection status colors
export const CONNECTION_STATUS_COLORS = {
  ONLINE: {
    text: 'text-green-600',
    icon: '🟢',
  },
  WARNING: {
    text: 'text-yellow-600',
    icon: '🟡',
  },
  OFFLINE: {
    text: 'text-red-600',
    icon: '🔴',
  },
  NEVER: {
    text: 'text-gray-500',
    icon: '⚪',
  },
} as const;

// Transaction type colors
export const TRANSACTION_TYPE_COLORS = {
  DEPOSIT: 'text-green-600 bg-green-50',
  WITHDRAWAL: 'text-red-600 bg-red-50',
} as const;

// Transaction status colors
export const TRANSACTION_STATUS_COLORS = {
  SYNCED: 'text-green-700 bg-green-100',
  PENDING_SYNC: 'text-yellow-700 bg-yellow-100',
  FAILED: 'text-red-700 bg-red-100',
} as const;
