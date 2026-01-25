'use client';

import { useRouter, useSearchParams, usePathname } from 'next/navigation';
import { useTransition, useState } from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Search,
  BadgeCheck,
  X
} from 'lucide-react';
import { cn } from '@/lib/utils';

export default function CouponsToolbar() {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const [isPending, startTransition] = useTransition();

  const [searchValue, setSearchValue] = useState(searchParams.get('q') || '');
  const showVerified = searchParams.get('verified') === 'true';

  const handleSearch = (value: string) => {
    setSearchValue(value);
    const params = new URLSearchParams(searchParams);
    if (value) {
      params.set('q', value);
    } else {
      params.delete('q');
    }
    startTransition(() => {
      router.replace(`${pathname}?${params.toString()}`);
    });
  };

  const toggleVerified = () => {
    const params = new URLSearchParams(searchParams);
    if (showVerified) {
      params.delete('verified');
    } else {
      params.set('verified', 'true');
    }
    startTransition(() => {
      router.replace(`${pathname}?${params.toString()}`);
    });
  };

  return (
    <div className="sticky top-14 sm:top-16 z-30 bg-background/80 backdrop-blur-xl border-b border-border/40">
      <div className="container mx-auto px-4 py-3">
        <div className="flex items-center gap-3">
          {/* Search Input */}
          <div className="relative flex-1 max-w-md group">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground group-hover:text-primary transition-colors" />
            <Input
              placeholder="Search codes or stores..."
              value={searchValue}
              onChange={(e) => handleSearch(e.target.value)}
              className={cn(
                "pl-12 pr-12 h-12 bg-white border-slate-200 rounded-full shadow-sm hover:shadow-md",
                "focus:bg-white focus:border-primary/20 focus:ring-4 focus:ring-primary/10",
                "placeholder:text-muted-foreground/60",
                "transition-all duration-200"
              )}
            />
            {searchValue && (
              <button
                onClick={() => handleSearch('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 w-6 h-6 rounded-full bg-slate-100 hover:bg-slate-200 flex items-center justify-center transition-colors"
              >
                <X className="w-3 h-3 text-muted-foreground" />
              </button>
            )}
          </div>

          {/* Verified Toggle */}
          <Button
            variant="ghost"
            size="sm"
            onClick={toggleVerified}
            className={cn(
              "h-12 px-6 rounded-full gap-2 font-medium transition-all duration-200 shadow-sm border",
              showVerified
                ? "bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border-emerald-200 shadow-emerald-100"
                : "bg-white text-slate-600 hover:text-slate-900 hover:bg-slate-50 border-slate-200"
            )}
          >
            <BadgeCheck className={cn(
              "w-4 h-4",
              showVerified && "text-emerald-600"
            )} />
            <span className="hidden sm:inline">Verified</span>
          </Button>
        </div>
      </div>
    </div>
  );
}
