'use client';

import { useState, useEffect, useCallback } from 'react';
import { Search, X } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { useTranslations } from 'next-intl';
import { cn } from '@/lib/utils';

interface SearchBarProps {
  placeholder?: string;
  onSearch?: (query: string) => void;
  className?: string;
  defaultValue?: string;
  autoFocus?: boolean;
}

export function SearchBar({
  placeholder,
  onSearch,
  className,
  defaultValue = '',
  autoFocus = false,
}: SearchBarProps) {
  const t = useTranslations('Common');
  const [query, setQuery] = useState(defaultValue);

  const handleSearch = useCallback((q: string) => {
    if (onSearch) {
      onSearch(q);
    }
  }, [onSearch]);

  useEffect(() => {
    const timer = setTimeout(() => {
      if (query !== defaultValue) {
        handleSearch(query);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query, handleSearch, defaultValue]);

  const handleClear = () => {
    setQuery('');
    handleSearch('');
  };

  return (
    <div className={cn("relative w-full group", className)}>
      <div className="absolute left-4 top-1/2 -translate-y-1/2 text-muted-foreground group-hover:text-primary transition-colors z-10">
        <Search className="h-5 w-5" />
      </div>
      <Input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder={placeholder || t('searchPlaceholder')}
        className="pl-12 h-12 bg-white border-slate-200 focus:border-primary/20 focus:ring-4 focus:ring-primary/10 transition-all rounded-full pr-12 shadow-sm hover:shadow-md"
        autoFocus={autoFocus}
      />
      {query && (
        <Button
          variant="ghost"
          size="icon"
          onClick={handleClear}
          className="absolute right-2 top-1/2 -translate-y-1/2 h-8 w-8 hover:bg-slate-100 rounded-full"
        >
          <X className="h-4 w-4" />
        </Button>
      )}
    </div>
  );
}

export default SearchBar;
