// Error message component
// Shows when something goes wrong

import React from 'react';

interface ErrorMessageProps {
  title?: string;
  message: string;
  onRetry?: () => void;  // Optional retry button
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({
  title = 'Something went wrong',
  message,
  onRetry,
}) => {
  return (
    <div className="px-6 py-12 text-center">
      {/* Error icon */}
      <div className="text-6xl mb-4">⚠️</div>

      {/* Title */}
      <h3 className="text-lg font-medium text-gray-900 mb-2">{title}</h3>

      {/* Error message */}
      <p className="text-sm text-gray-500 max-w-sm mx-auto">{message}</p>

      {/* Retry button */}
      {onRetry && (
        <button
          onClick={onRetry}
          className="mt-6 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Try Again
        </button>
      )}
    </div>
  );
};
