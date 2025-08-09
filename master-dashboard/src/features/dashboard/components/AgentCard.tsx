// Single agent card component
// Shows agent info with connection status and registration status

import React from 'react';
import { Agent } from '../types';
import { ConnectionStatusBadge } from './ConnectionStatusBadge';

interface AgentCardProps {
  agent: Agent;
  onClick?: (agent: Agent) => void;  // Optional click handler
  showDetails?: boolean;  // Show owner name and phone
}

export const AgentCard: React.FC<AgentCardProps> = ({
  agent,
  onClick,
  showDetails = false,
}) => {
  const handleClick = () => {
    if (onClick) {
      onClick(agent);
    }
  };

  return (
    <div
      className={`px-6 py-4 hover:bg-gray-50 transition-colors ${
        onClick ? 'cursor-pointer' : ''
      }`}
      onClick={handleClick}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
      onKeyDown={(e) => {
        if (onClick && (e.key === 'Enter' || e.key === ' ')) {
          e.preventDefault();
          handleClick();
        }
      }}
    >
      <div className="flex items-center justify-between">
        {/* Left side: Connection status + agent info */}
        <div className="flex items-center space-x-3">
          <ConnectionStatusBadge
            lastSeenAt={agent.lastSeenAt}
            showLastSeen={true}
          />

          <div>
            {/* Business name */}
            <h3 className="text-sm font-medium text-gray-900">
              {agent.businessName}
            </h3>

            {/* Location */}
            <p className="text-sm text-gray-500">{agent.location}</p>

            {/* Optional extra details */}
            {showDetails && (
              <div className="mt-1 text-xs text-gray-400">
                <p>Owner: {agent.ownerName}</p>
                <p>Phone: {agent.phoneNumber}</p>
              </div>
            )}
          </div>
        </div>

        {/* Right side: Registration status badge */}
        <div className="text-right">
          <AgentStatusBadge status={agent.status} />
        </div>
      </div>
    </div>
  );
};

// Shows the agent's registration status (ACTIVE, PENDING, etc.)
const AgentStatusBadge: React.FC<{ status: Agent['status'] }> = ({ status }) => {
  // Pick colors based on status
  const getStatusColor = (): string => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'SUSPENDED':
        return 'bg-red-100 text-red-800';
      case 'REJECTED':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <span className={`px-2 py-1 text-xs font-semibold rounded ${getStatusColor()}`}>
      {status}
    </span>
  );
};
