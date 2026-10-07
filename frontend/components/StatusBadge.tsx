import React from 'react';
import { ResearchStatus } from '@/types';
import { CheckCircle2, Clock, AlertCircle, Loader2 } from 'lucide-react';
import { cn } from '@/lib/utils';

interface StatusBadgeProps {
  status: ResearchStatus;
  className?: string;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, className, size = 'md' }) => {
  const isSmall = size === 'sm';

  switch (status) {
    case 'COMPLETED':
      return (
        <span
          className={cn(
            'inline-flex items-center gap-1.5 font-medium rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20',
            isSmall ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-xs sm:text-sm',
            className
          )}
        >
          <CheckCircle2 className={isSmall ? 'w-3 h-3' : 'w-4 h-4 text-emerald-400'} />
          Completed
        </span>
      );
    case 'IN_PROGRESS':
      return (
        <span
          className={cn(
            'inline-flex items-center gap-1.5 font-medium rounded-full bg-blue-500/10 text-blue-400 border border-blue-500/20 animate-pulse',
            isSmall ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-xs sm:text-sm',
            className
          )}
        >
          <Loader2 className={cn('animate-spin', isSmall ? 'w-3 h-3' : 'w-4 h-4 text-blue-400')} />
          In Progress
        </span>
      );
    case 'FAILED':
      return (
        <span
          className={cn(
            'inline-flex items-center gap-1.5 font-medium rounded-full bg-rose-500/10 text-rose-400 border border-rose-500/20',
            isSmall ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-xs sm:text-sm',
            className
          )}
        >
          <AlertCircle className={isSmall ? 'w-3 h-3' : 'w-4 h-4 text-rose-400'} />
          Failed
        </span>
      );
    case 'PENDING':
    default:
      return (
        <span
          className={cn(
            'inline-flex items-center gap-1.5 font-medium rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20',
            isSmall ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-xs sm:text-sm',
            className
          )}
        >
          <Clock className={isSmall ? 'w-3 h-3' : 'w-4 h-4 text-amber-400'} />
          Pending
        </span>
      );
  }
};
