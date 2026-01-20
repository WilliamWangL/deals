'use client';

import { useRouter, useSearchParams, usePathname } from 'next/navigation';
import { useTransition, useState } from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { 
  Search, 
  SlidersHorizontal, 
  CheckCircle2, 
  ArrowUpDown,
  LayoutGrid,
  List,
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

  const clearFilters = () => {
    setSearchValue('');
    startTransition(() => {
      router.replace(pathname);
    });
  };

  const hasActiveFilters = searchValue || showVerified;

  return (
    <section className="sticky top-0 z-30 bg-background/95 backdrop-blur-xl border-b border-border/50 shadow-sm">
      <div className="container mx-auto px-4 py-4">
        <div className="flex flex-col md:flex-row gap-4 justify-between items-center">

          <div className="relative w-full md:w-96 group">
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground group-hover:text-primary transition-colors">
              <Search className="w-5 h-5" />
            </div>
            <Input
              placeholder="Search coupons..."
              value={searchValue}
              onChange={(e) => handleSearch(e.target.value)}
              className="pl-10 h-12 bg-muted/50 border-border/80 focus:bg-card transition-all rounded-xl shadow-sm focus:shadow-md focus:border-primary/50"
            />
            {searchValue && (
              <button
                onClick={() => handleSearch('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-2 md:pb-0 no-scrollbar">

            <Button
              variant={showVerified ? "default" : "outline"}
              size="sm"
              onClick={toggleVerified}
              className={cn(
                "h-10 rounded-xl gap-2 shrink-0 transition-all",
                showVerified
                  ? "bg-emerald-600 hover:bg-emerald-700 text-white border-transparent shadow-md"
                  : "border-border/80 text-muted-foreground hover:text-emerald-600 hover:border-emerald-200 hover:bg-emerald-50"
              )}
            >
              <CheckCircle2 className="w-4 h-4" />
              Verified Only
            </Button>

            <div className="w-px h-6 bg-border mx-1 shrink-0" />

            <Button
              variant="ghost"
              size="sm"
              className="h-10 rounded-xl text-muted-foreground hover:text-primary hover:bg-primary/5 gap-2 shrink-0"
            >
              <ArrowUpDown className="w-4 h-4" />
              Sort
            </Button>

            <Button
              variant="ghost"
              size="sm"
              className="h-10 rounded-xl text-muted-foreground hover:text-primary hover:bg-primary/5 gap-2 shrink-0"
            >
              <SlidersHorizontal className="w-4 h-4" />
              Filters
            </Button>

            <div className="ml-auto md:ml-2 flex items-center gap-1 p-1 rounded-xl border border-border/80 bg-muted/30 shrink-0">
               <Button variant="ghost" size="icon" className="h-8 w-8 rounded-lg bg-card text-foreground shadow-sm hover:bg-card">
                 <LayoutGrid className="w-4 h-4" />
               </Button>
               <Button variant="ghost" size="icon" className="h-8 w-8 rounded-lg text-muted-foreground hover:text-foreground hover:bg-muted/50">
                 <List className="w-4 h-4" />
               </Button>
            </div>
          </div>

        </div>
      </div>
    </section>
  );
}
