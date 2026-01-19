'use client';

import { cn } from '@/lib/utils';

interface SkeletonProps {
  className?: string;
}

export function Skeleton({ className }: SkeletonProps) {
  return (
    <div
      className={cn(
        "animate-pulse rounded-md bg-muted",
        className
      )}
    />
  );
}

export function DealCardSkeleton() {
  return (
    <div className="bg-card border rounded-xl overflow-hidden">
      <Skeleton className="h-52 w-full" />
      <div className="p-5 space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <Skeleton className="h-8 w-8 rounded-full" />
            <Skeleton className="h-4 w-24" />
          </div>
          <Skeleton className="h-6 w-20 rounded-full" />
        </div>
        <div className="space-y-2">
          <Skeleton className="h-6 w-full" />
          <Skeleton className="h-6 w-3/4" />
        </div>
        <div className="pt-2 flex items-baseline gap-3">
          <Skeleton className="h-8 w-20" />
          <Skeleton className="h-4 w-12" />
        </div>
      </div>
      <div className="p-5 pt-0">
        <Skeleton className="h-11 w-full rounded-lg" />
      </div>
    </div>
  );
}

export function CouponCardSkeleton() {
  return (
    <div className="bg-card border border-l-0 h-full">
      <div className="flex h-full items-stretch">
        <Skeleton className="w-24 sm:w-28 shrink-0" />
        <div className="flex-1 p-3 sm:p-4 flex flex-col justify-between gap-3">
          <div>
            <div className="flex items-start justify-between gap-2 mb-2">
              <div className="flex items-center gap-2 min-w-0">
                <Skeleton className="h-8 w-8 rounded-full" />
                <Skeleton className="h-4 w-24" />
              </div>
            </div>
            <Skeleton className="h-4 w-full mb-1" />
            <Skeleton className="h-4 w-2/3 mb-2" />
            <Skeleton className="h-3 w-32" />
          </div>
          <div className="space-y-2">
            <Skeleton className="h-8 w-full rounded-lg" />
            <Skeleton className="h-3 w-24" />
          </div>
        </div>
      </div>
    </div>
  );
}

export function StoreCardSkeleton() {
  return (
    <div className="bg-card border rounded-xl p-6 flex flex-col items-center text-center">
      <Skeleton className="h-16 w-16 rounded-full mb-4" />
      <Skeleton className="h-5 w-24 mb-2" />
      <Skeleton className="h-4 w-16" />
    </div>
  );
}

export function CategorySkeleton() {
  return (
    <div className="flex items-center gap-2 rounded-full border border-gray-200 bg-white px-5 py-2.5">
      <Skeleton className="h-4 w-4 rounded-full" />
      <Skeleton className="h-4 w-20" />
    </div>
  );
}
