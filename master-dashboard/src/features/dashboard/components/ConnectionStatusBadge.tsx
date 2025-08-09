// Shows the connection status for an agent
// Displays an icon (🟢🟡🔴⚪) with status text and optional timestamp

import React from 'react';
import { getConnectionStatus, type ConnectionStatusResult } from '../utils/connectionStatus';
import { formatLastSeen } from '../utils/dateFormatters';

interface ConnectionStatusBadgeProps {
  lastSeenAt: string | null;
  showLastSeen?: boolean;  // Show "2m ago" below the status
  size?: 'sm' | 'md' | 'lg';
}

export const ConnectionStatusBadge: React.FC<ConnectionStatusBadgeProps> = ({
  lastSeenAt,
  showLastSeen = false,
  size = 'md',
}) => {
  const connection: ConnectionStatusResult = getConnectionStatus(lastSeenAt);

  // Different sizes for the badge
  const sizeClasses = {
    sm: {
      icon: 'text-lg',
      text: 'text-xs',
      container: 'space-x-1',
    },
    md: {
      icon: 'text-2xl',
      text: 'text-sm',
      container: 'space-x-2',
    },
    lg: {
      icon: 'text-3xl',
      text: 'text-base',
      container: 'space-x-3',
    },
  };

  const styling = sizeClasses[size];

  return (
    <div className="flex items-center">
      <div className={`flex items-center ${styling.container}`}>
        {/* Status icon */}
        <span className={styling.icon} role="img" aria-label={connection.status}>
          {connection.icon}
        </span>

        {/* Status text and optional timestamp */}
        <div className="flex flex-col">
          <span className={`font-medium ${connection.color} ${styling.text}`}>
            {connection.status}
          </span>

          {showLastSeen && (
            <span className="text-xs text-gray-500">
              {formatLastSeen(lastSeenAt)}
            </span>
          )}
        </div>
      </div>
    </div>
  );
};

// Simpler version - just shows the icon
export const ConnectionStatusIcon: React.FC<{
  lastSeenAt: string | null;
  size?: 'sm' | 'md' | 'lg';
}> = ({ lastSeenAt, size = 'md' }) => {
  const connection = getConnectionStatus(lastSeenAt);

  const sizeClasses = {
    sm: 'text-lg',
    md: 'text-2xl',
    lg: 'text-3xl',
  };

  return (
    <span
      className={sizeClasses[size]}
      role="img"
      aria-label={connection.status}
      title={`${connection.status} - ${formatLastSeen(lastSeenAt)}`}
    >
      {connection.icon}
    </span>
  );
};
