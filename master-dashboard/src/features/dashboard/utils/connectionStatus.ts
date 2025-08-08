// Connection status utilities
// Figures out if an agent is online, warning, offline, or never connected

export type ConnectionStatus = 'ONLINE' | 'WARNING' | 'OFFLINE' | 'NEVER';

export interface ConnectionStatusResult {
  status: ConnectionStatus;
  color: string;  // Tailwind color class
  icon: string;   // Emoji to show
}

// Thresholds for connection status
// Online = last seen within 60 seconds
// Warning = last seen within 10 minutes
const CONNECTION_THRESHOLDS = {
  ONLINE: 60,
  WARNING: 600,
} as const;

// Figure out the agent's connection status
// based on when we last heard from them
export const getConnectionStatus = (lastSeenAt: string | null): ConnectionStatusResult => {
  // Never connected
  if (!lastSeenAt) {
    return {
      status: 'NEVER',
      color: 'text-gray-500',
      icon: '⚪',
    };
  }

  // Calculate how long ago they were seen
  const now = new Date();
  const lastSeen = new Date(lastSeenAt);
  const secondsAgo = Math.floor((now.getTime() - lastSeen.getTime()) / 1000);

  // Online - within last minute
  if (secondsAgo < CONNECTION_THRESHOLDS.ONLINE) {
    return {
      status: 'ONLINE',
      color: 'text-green-600',
      icon: '🟢',
    };
  }

  // Warning - within last 10 minutes
  if (secondsAgo < CONNECTION_THRESHOLDS.WARNING) {
    return {
      status: 'WARNING',
      color: 'text-yellow-600',
      icon: '🟡',
    };
  }

  // Offline - more than 10 minutes
  return {
    status: 'OFFLINE',
    color: 'text-red-600',
    icon: '🔴',
  };
};

// Quick check if agent is currently online
export const isAgentOnline = (lastSeenAt: string | null): boolean => {
  const status = getConnectionStatus(lastSeenAt);
  return status.status === 'ONLINE';
};

// Get a friendly description of the status
export const getStatusDescription = (status: ConnectionStatus): string => {
  switch (status) {
    case 'ONLINE':
      return 'Connected and syncing';
    case 'WARNING':
      return 'Connected but delayed';
    case 'OFFLINE':
      return 'Disconnected - may need attention';
    case 'NEVER':
      return 'Never connected';
    default:
      return 'Unknown status';
  }
};
