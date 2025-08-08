// Date and time formatting utilities
// Handles all the date/time display logic in one place

// How long ago was this timestamp?
// Returns things like "30s ago", "5m ago", "2h ago"
export const formatLastSeen = (lastSeenAt: string | null): string => {
  if (!lastSeenAt) {
    return 'Never';
  }

  const now = new Date();
  const lastSeen = new Date(lastSeenAt);
  const seconds = Math.floor((now.getTime() - lastSeen.getTime()) / 1000);

  // Just in case we get a future timestamp (shouldn't happen)
  if (seconds < 0) {
    return 'Just now';
  }

  // Less than a minute
  if (seconds < 60) {
    return `${seconds}s ago`;
  }

  // Less than an hour
  if (seconds < 3600) {
    const minutes = Math.floor(seconds / 60);
    return `${minutes}m ago`;
  }

  // Less than a day
  if (seconds < 86400) {
    const hours = Math.floor(seconds / 3600);
    return `${hours}h ago`;
  }

  // A day or more
  const days = Math.floor(seconds / 86400);
  return `${days}d ago`;
};

// Format timestamp for transaction list
// Shows like "Dec 8, 2:30 PM"
export const formatTimestamp = (timestamp: string, locale: string = 'en-US'): string => {
  const date = new Date(timestamp);

  return date.toLocaleString(locale, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

// Full date with year
// Shows like "December 8, 2025"
export const formatFullDate = (timestamp: string, locale: string = 'en-US'): string => {
  const date = new Date(timestamp);

  return date.toLocaleDateString(locale, {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

// Just the time part
// Shows like "2:30 PM"
export const formatTimeOnly = (timestamp: string, locale: string = 'en-US'): string => {
  const date = new Date(timestamp);

  return date.toLocaleTimeString(locale, {
    hour: '2-digit',
    minute: '2-digit',
  });
};

// How many seconds ago was this?
// Returns 0 if timestamp is null
export const getSecondsAgo = (timestamp: string | null): number => {
  if (!timestamp) {
    return 0;
  }

  const now = new Date();
  const then = new Date(timestamp);
  return Math.floor((now.getTime() - then.getTime()) / 1000);
};

// Check if something happened recently
// Useful for "is this within the last minute?" type checks
export const isWithinLast = (timestamp: string | null, seconds: number): boolean => {
  if (!timestamp) {
    return false;
  }

  const secondsAgo = getSecondsAgo(timestamp);
  return secondsAgo >= 0 && secondsAgo < seconds;
};
