// Reusable empty state component
// Shows when there's no data to display

import React from 'react';

interface EmptyStateProps {
  icon?: string;  // Emoji icon
  title: string;
  description?: string;
  action?: React.ReactNode;  // Optional action button or link
  children?: React.ReactNode;  // Optional custom content
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  icon = '📭',
  title,
  description,
  action,
  children,
}) => {
  return (
    <div className="px-6 py-12 text-center">
      {/* Icon */}
      <div className="text-6xl mb-4">{icon}</div>

      {/* Title */}
      <h3 className="text-lg font-medium text-gray-900 mb-2">{title}</h3>

      {/* Description */}
      {description && (
        <p className="text-sm text-gray-500 max-w-sm mx-auto">{description}</p>
      )}

      {/* Optional action */}
      {action && <div className="mt-6">{action}</div>}

      {/* Custom content */}
      {children && <div className="mt-6">{children}</div>}
    </div>
  );
};
