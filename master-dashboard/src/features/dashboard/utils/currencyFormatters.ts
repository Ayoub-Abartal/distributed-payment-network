// Currency formatting utilities
// Handles money display throughout the app

// Format amount with currency
// Shows like "500.00 DH"
export const formatAmount = (amount: number): string => {
  return `${amount.toFixed(2)} DH`;
};

// Format amount without currency (just the number)
// Shows like "500.00"
export const formatAmountPlain = (amount: number): string => {
  return amount.toFixed(2);
};

// Format with thousands separator
// Shows like "1,500.00 DH"
export const formatAmountWithCommas = (amount: number): string => {
  const formatted = amount.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
  return `${formatted} DH`;
};

// Format large numbers in compact form
// 1000 -> "1K", 1000000 -> "1M"
export const formatCompactAmount = (amount: number): string => {
  if (amount >= 1000000) {
    return `${(amount / 1000000).toFixed(1)}M DH`;
  }
  if (amount >= 1000) {
    return `${(amount / 1000).toFixed(1)}K DH`;
  }
  return formatAmount(amount);
};

// Calculate percentage of total
export const calculatePercentage = (amount: number, total: number): string => {
  if (total === 0) return '0%';
  return `${((amount / total) * 100).toFixed(1)}%`;
};
