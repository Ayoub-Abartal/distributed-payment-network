// Main agent list component
// Shows all registered agents with their connection status

import React from 'react';
import { Agent } from '../types';
import { AgentCard } from './AgentCard';

interface AgentListProps {
  agents: Agent[];
  onAgentClick?: (agent: Agent) => void;
  showDetails?: boolean;
}

export const AgentList: React.FC<AgentListProps> = ({
  agents,
  onAgentClick,
  showDetails = false,
}) => {
  return (
    <div className="bg-white rounded-lg shadow">
      {/* Header */}
      <div className="px-6 py-4 border-b border-gray-200">
        <h2 className="text-lg font-semibold text-gray-900">
          Agent Network Status
        </h2>
        <p className="text-sm text-gray-500 mt-1">
          {agents.length} {agents.length === 1 ? 'agent' : 'agents'} registered
        </p>
      </div>

      {/* Agent list */}
      <div className="divide-y divide-gray-200">
        {agents.length === 0 ? (
          <EmptyAgentList />
        ) : (
          agents.map((agent) => (
            <AgentCard
              key={agent.id}
              agent={agent}
              onClick={onAgentClick}
              showDetails={showDetails}
            />
          ))
        )}
      </div>
    </div>
  );
};

// Shows when no agents are registered yet
const EmptyAgentList: React.FC = () => {
  return (
    <div className="px-6 py-12 text-center">
      <div className="text-6xl mb-4">📡</div>

      <h3 className="text-lg font-medium text-gray-900 mb-2">
        No Agents Registered Yet
      </h3>
      <p className="text-sm text-gray-500 max-w-sm mx-auto">
        Agents will appear here once they register with the master node.
        Make sure your agent instances are running and configured correctly.
      </p>

      {/* Quick guide */}
      <div className="mt-6 text-left max-w-md mx-auto bg-gray-50 rounded-lg p-4">
        <p className="text-xs font-semibold text-gray-700 mb-2">
          To register an agent:
        </p>
        <ol className="text-xs text-gray-600 space-y-1 list-decimal list-inside">
          <li>Start an agent instance with the agent profile</li>
          <li>Ensure it can reach the master node</li>
          <li>The agent will auto-register on startup</li>
        </ol>
      </div>
    </div>
  );
};
