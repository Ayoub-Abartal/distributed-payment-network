// Reusable card container component
// Basic white card with shadow and optional padding

import React from 'react';

interface CardProps {
  children: React.ReactNode;
  className?: string;  // Allow custom classes
  padding?: 'none' | 'sm' | 'md' | 'lg';
}

export const Card: React.FC<CardProps> = ({
  children,
  className = '',
  padding = 'md',
}) => {
  // Padding options
  const paddingClasses = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
  };

  return (
    <div
      className={`
        bg-white rounded-lg shadow
        ${paddingClasses[padding]}
        ${className}
      `}
    >
      {children}
    </div>
  );
};

// Card header component
export const CardHeader: React.FC<{
  children: React.ReactNode;
  className?: string;
}> = ({ children, className = '' }) => {
  return (
    <div className={`px-6 py-4 border-b border-gray-200 ${className}`}>
      {children}
    </div>
  );
};

// Card title component
export const CardTitle: React.FC<{
  children: React.ReactNode;
  subtitle?: string;
}> = ({ children, subtitle }) => {
  return (
    <div>
      <h2 className="text-lg font-semibold text-gray-900">{children}</h2>
      {subtitle && (
        <p className="text-sm text-gray-500 mt-1">{subtitle}</p>
      )}
    </div>
  );
};

// Card body component
export const CardBody: React.FC<{
  children: React.ReactNode;
  className?: string;
}> = ({ children, className = '' }) => {
  return <div className={className}>{children}</div>;
};
