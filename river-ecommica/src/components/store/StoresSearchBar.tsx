'use client';

import { useState, useEffect, useTransition } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Search, X, Loader2 } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

interface StoresSearchBarProps {
  placeholder?: string;
  className?: string;
}

export function StoresSearchBar({ placeholder = 'Search stores...', className }: StoresSearchBarProps) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [isPending, startTransition] = useTransition();
  const initialQuery = searchParams.get('q') || '';
  const [query, setQuery] = useState(initialQuery);

  // Debounced search
  useEffect(() => {
    const currentUrlQuery = searchParams.get('q') || '';
    if (query.trim() === currentUrlQuery) return;

    const timer = setTimeout(() => {
      startTransition(() => {
        const params = new URLSearchParams(searchParams.toString());
        if (query.trim()) {
          params.set('q', query.trim());
        } else {
          params.delete('q');
        }
        params.delete('page'); // Reset to page 1 when searching
        router.push(`?${params.toString()}`, { scroll: false });
      });
    }, 300);

    return () => clearTimeout(timer);
  }, [query, searchParams, router]);

  const handleClear = () => {
    setQuery('');
    startTransition(() => {
      const params = new URLSearchParams(searchParams.toString());
      params.delete('q');
      params.delete('page');
      router.push(`?${params.toString()}`, { scroll: false });
    });
  };

  return (
    <div className={`relative w-full group ${className || ''}`}>
      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 group-hover:text-primary transition-colors z-10">
        {isPending ? (
          <Loader2 className="h-5 w-5 animate-spin text-primary" />
        ) : (
          <Search className="h-5 w-5" />
        )}
      </div>
      <Input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder={placeholder}
        className="pl-12 h-12 bg-white border-slate-200 focus:border-primary/20 focus:ring-4 focus:ring-primary/10 transition-all rounded-full pr-12 shadow-sm hover:shadow-md"
      />
      {query && (
        <Button
          type="button"
          variant="ghost"
          size="icon"
          onClick={handleClear}
          className="absolute right-2 top-1/2 -translate-y-1/2 h-8 w-8 hover:bg-slate-100 rounded-full"
          aria-label="Clear search"
        >
          <X className="h-4 w-4" />
        </Button>
      )}
    </div>
  );
}

export default StoresSearchBar;
