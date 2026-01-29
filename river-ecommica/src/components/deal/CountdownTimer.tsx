'use client';

import { useEffect, useState } from 'react';
import { Clock } from 'lucide-react';
import { cn } from '@/lib/utils';

interface CountdownTimerProps {
  endTime: string;
}

export function CountdownTimer({ endTime }: CountdownTimerProps) {
  const [timeLeft, setTimeLeft] = useState<string>('');
  const [isUrgent, setIsUrgent] = useState(false);

  useEffect(() => {
    const calculateTime = () => {
      const end = new Date(endTime).getTime();
      if (isNaN(end)) return null;
      const now = new Date().getTime();
      const diff = end - now;

      if (diff <= 0) return null;

      const days = Math.floor(diff / (1000 * 60 * 60 * 24));

      let urgent = false;
      let text = '';

      if (days > 1) {
        text = `${days}d left`;
      } else {
        const hours = Math.floor(
          (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        );
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

        urgent = true;
        if (days === 1) text = `1d ${hours}h`;
        else text = `${hours}h ${minutes}m`;
      }
      return { text, urgent };
    };

    const update = () => {
      const result = calculateTime();
      if (result) {
        setTimeLeft(result.text);
        setIsUrgent(result.urgent);
      }
    };

    requestAnimationFrame(update);
    const timer = setInterval(update, 60000);
    return () => clearInterval(timer);
  }, [endTime]);

  if (!timeLeft) return null;

  return (
    <span
      role="timer"
      aria-live="off"
      aria-label="Deal countdown timer"
      className={cn(
        'inline-flex items-center gap-1 text-[11px] font-semibold',
        isUrgent ? 'text-rose-600' : 'text-amber-600'
      )}
    >
      <Clock className={cn('w-3 h-3', isUrgent && 'animate-pulse')} />
      {timeLeft}
    </span>
  );
}

export default CountdownTimer;
