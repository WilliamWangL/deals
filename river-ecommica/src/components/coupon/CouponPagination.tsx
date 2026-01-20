'use client';

import { useCallback, useTransition } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PAGINATION } from '@/constants/pagination';

interface CouponPaginationProps {
  total: number;
  pageSize?: number;
  currentPage: number;
}

export function CouponPagination({ total, pageSize = PAGINATION.PAGE_SIZE.COUPON, currentPage }: CouponPaginationProps) {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [isPending, startTransition] = useTransition();

  const totalPages = Math.ceil(total / pageSize);

  const handlePageChange = useCallback((page: number) => {
    if (page < 1 || page > totalPages) return;

    startTransition(() => {
      const params = new URLSearchParams(searchParams.toString());
      params.set('page', String(page));
      router.push(`?${params.toString()}`, { scroll: false });
    });
  }, [searchParams, router, totalPages]);

  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-2 py-8">
      <Button
        variant="outline"
        size="sm"
        onClick={() => handlePageChange(currentPage - 1)}
        disabled={currentPage === 1 || isPending}
        className="h-10 w-10 p-0 rounded-lg border-slate-200"
      >
        <ChevronLeft className="w-4 h-4" />
      </Button>

      <div className="flex items-center gap-1">
        {Array.from({ length: Math.min(PAGINATION.PAGE_RANGE, totalPages) }, (_, i) => {
          let pageNum: number;
          if (totalPages <= PAGINATION.PAGE_RANGE) {
            pageNum = i + 1;
          } else if (currentPage <= Math.floor(PAGINATION.PAGE_RANGE / 2) + 1) {
            pageNum = i + 1;
          } else if (currentPage >= totalPages - Math.floor(PAGINATION.PAGE_RANGE / 2)) {
            pageNum = totalPages - PAGINATION.PAGE_RANGE + 1 + i;
          } else {
            pageNum = currentPage - Math.floor(PAGINATION.PAGE_RANGE / 2) + i;
          }

          return (
            <Button
              key={pageNum}
              variant={currentPage === pageNum ? 'default' : 'ghost'}
              size="sm"
              onClick={() => handlePageChange(pageNum)}
              disabled={isPending}
              className={`h-10 w-10 p-0 rounded-lg ${
                currentPage === pageNum
                  ? 'bg-primary text-white'
                  : 'text-slate-600 hover:text-primary hover:bg-primary/5'
              }`}
            >
              {pageNum}
            </Button>
          );
        })}
      </div>

      <Button
        variant="outline"
        size="sm"
        onClick={() => handlePageChange(currentPage + 1)}
        disabled={currentPage === totalPages || isPending}
        className="h-10 w-10 p-0 rounded-lg border-slate-200"
      >
        <ChevronRight className="w-4 h-4" />
      </Button>
    </div>
  );
}

export default CouponPagination;
