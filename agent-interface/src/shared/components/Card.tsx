import React from 'react';

interface CardProps {
  children: React.ReactNode;
  className?: string;
}

// Simple white card with shadow
export const Card: React.FC<CardProps> = ({ children, className = '' }) => {
  return (
    <div className={`bg-white rounded-lg shadow p-6 ${className}`}>
      {children}
    </div>
  );
};
