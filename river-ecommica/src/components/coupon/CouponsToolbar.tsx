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
    <div className="sticky top-0 z-30 bg-white/80 backdrop-blur-md border-b border-slate-200 shadow-sm transition-all duration-300">
      <div className="container mx-auto px-4 py-4">
        <div className="flex flex-col md:flex-row gap-4 justify-between items-center">
          
          <div className="relative w-full md:w-96 group">
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-hover:text-primary transition-colors">
              <Search className="w-5 h-5" />
            </div>
            <Input 
              placeholder="Search coupons..." 
              value={searchValue}
              onChange={(e) => handleSearch(e.target.value)}
              className="pl-10 h-11 bg-slate-50 border-slate-200 focus:bg-white transition-all rounded-xl shadow-sm focus:shadow-md"
            />
            {searchValue && (
              <button 
                onClick={() => handleSearch('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-2 md:pb-0 scrollbar-hide">
            
            <Button 
              variant={showVerified ? "default" : "outline"} 
              size="sm" 
              onClick={toggleVerified}
              className={cn(
                "h-10 rounded-lg gap-2 shrink-0 transition-all",
                showVerified 
                  ? "bg-green-600 hover:bg-green-700 text-white border-transparent" 
                  : "border-slate-200 text-slate-600 hover:text-green-600 hover:border-green-200 hover:bg-green-50"
              )}
            >
              <CheckCircle2 className="w-4 h-4" />
              Verified Only
            </Button>

            <div className="w-px h-6 bg-slate-200 mx-1 shrink-0" />

            <Button 
              variant="ghost" 
              size="sm" 
              className="h-10 rounded-lg text-slate-600 hover:text-primary hover:bg-primary/5 gap-2 shrink-0"
            >
              <ArrowUpDown className="w-4 h-4" />
              Sort
            </Button>
            
            <Button 
              variant="ghost" 
              size="sm" 
              className="h-10 rounded-lg text-slate-600 hover:text-primary hover:bg-primary/5 gap-2 shrink-0"
            >
              <SlidersHorizontal className="w-4 h-4" />
              Filters
            </Button>

            <div className="ml-auto md:ml-2 flex items-center gap-1 border rounded-lg p-1 border-slate-200 shrink-0 bg-slate-50">
               <Button variant="ghost" size="icon" className="h-8 w-8 rounded bg-white text-slate-900 shadow-sm hover:bg-white">
                 <LayoutGrid className="w-4 h-4" />
               </Button>
               <Button variant="ghost" size="icon" className="h-8 w-8 rounded text-slate-400 hover:text-slate-600 hover:bg-slate-200/50">
                 <List className="w-4 h-4" />
               </Button>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}
