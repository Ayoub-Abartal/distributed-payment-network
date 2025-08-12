// Format ISO date string to readable format
export const formatDate = (dateString: string): string => {
  if (!dateString) return 'N/A';

  const date = new Date(dateString);

  // Check if date is valid
  if (isNaN(date.getTime())) return 'Invalid date';

  return new Intl.DateTimeFormat('fr-MA', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
};

// How long ago was this transaction?
export const formatRelativeTime = (dateString: string): string => {
  if (!dateString) return 'N/A';

  const now = new Date();
  const date = new Date(dateString);

  // Check if date is valid
  if (isNaN(date.getTime())) return 'Invalid date';

  const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (seconds < 60) return 'Just now';
  if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
  if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
  return `${Math.floor(seconds / 86400)}d ago`;
};
