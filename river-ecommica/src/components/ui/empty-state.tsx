'use client';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { BookOpen, RefreshCw, Search, ShoppingBag, Tag, Ticket } from 'lucide-react';

interface EmptyStateProps {
  icon?: 'search' | 'bag' | 'tag' | 'ticket' | 'book';
  title: string;
  description?: string;
  action?: {
    label: string;
    onClick: () => void;
  };
  className?: string;
}

const icons = {
  search: Search,
  bag: ShoppingBag,
  tag: Tag,
  ticket: Ticket,
  book: BookOpen,
};

export function EmptyState({
  icon = 'search',
  title,
  description,
  action,
  className,
}: EmptyStateProps) {
  const Icon = icons[icon] || Search;

  return (
    <div className={cn("flex flex-col items-center justify-center py-24 text-center", className)}>
      <div className="w-24 h-24 bg-muted rounded-full flex items-center justify-center mb-6 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-tr from-primary/10 to-transparent" />
        <Icon className="w-12 h-12 text-muted-foreground/50" />
      </div>
      <h3 className="text-xl font-bold text-foreground mb-2">{title}</h3>
      {description && (
        <p className="text-muted-foreground max-w-md mx-auto mb-6">{description}</p>
      )}
      {action && (
        <Button onClick={action.onClick} variant="outline" className="gap-2">
          <RefreshCw className="w-4 h-4" />
          {action.label}
        </Button>
      )}
    </div>
  );
}

export default EmptyState;
