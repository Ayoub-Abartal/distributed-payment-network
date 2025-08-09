// Metrics card component
// Shows a single metric with title, value, optional subtitle and icon

import React from 'react';
import { METRICS_CARD_COLORS } from '../constants/colors';

interface MetricsCardProps {
  title: string;
  value: string | number;
  subtitle?: string;  // Optional subtitle like "↑ 12% from last week"
  icon?: string;      // Optional emoji icon
  color?: keyof typeof METRICS_CARD_COLORS;
}

export const MetricsCard: React.FC<MetricsCardProps> = ({
  title,
  value,
  subtitle,
  icon,
  color = 'blue',
}) => {
  return (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex items-center justify-between">
        {/* Left side: Title and value */}
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-bold text-gray-900 mt-2">{value}</p>
          {subtitle && (
            <p className="text-sm text-gray-500 mt-1">{subtitle}</p>
          )}
        </div>

        {/* Right side: Optional icon */}
        {icon && (
          <div
            className={`w-12 h-12 rounded-full ${METRICS_CARD_COLORS[color]} flex items-center justify-center text-xl`}
          >
            {icon}
          </div>
        )}
      </div>
    </div>
  );
};
