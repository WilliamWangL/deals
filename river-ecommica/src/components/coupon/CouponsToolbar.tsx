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
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search codes or stores..."
              value={searchValue}
              onChange={(e) => handleSearch(e.target.value)}
              className={cn(
                "pl-10 pr-10 h-11 bg-muted/30 border-border/50 rounded-xl",
                "focus:bg-background focus:border-primary/30 focus:ring-2 focus:ring-primary/10",
                "placeholder:text-muted-foreground/60",
                "transition-all duration-200"
              )}
            />
            {searchValue && (
              <button
                onClick={() => handleSearch('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 rounded-full bg-muted hover:bg-muted-foreground/20 flex items-center justify-center transition-colors"
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
              "h-11 px-4 rounded-xl gap-2 font-medium transition-all duration-200",
              showVerified
                ? "bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border border-emerald-200"
                : "text-muted-foreground hover:text-foreground hover:bg-muted/50"
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
