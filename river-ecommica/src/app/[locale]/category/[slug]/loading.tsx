import { Skeleton, DealCardSkeleton, CouponCardSkeleton } from '@/components/ui/skeleton';

export default function CategoryLoading() {
  return (
    <main className="min-h-screen bg-background">
      <section className="page-header py-12 md:py-16">
        <div className="container mx-auto px-4 relative">
          <div className="flex items-center gap-2 mb-6">
            <Skeleton className="h-4 w-12" />
            <Skeleton className="h-4 w-4" />
            <Skeleton className="h-4 w-24" />
          </div>

          <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-8">
            <div className="max-w-2xl w-full">
              <div className="flex items-center gap-3 mb-5">
                <Skeleton className="w-14 h-14 rounded-2xl" />
                <Skeleton className="h-6 w-32 rounded-full" />
              </div>
              <Skeleton className="h-12 w-3/4 mb-4" />
              <Skeleton className="h-6 w-full max-w-xl" />
            </div>

            <div className="flex gap-4 md:gap-6 flex-wrap lg:flex-nowrap">
              {[1, 2].map((i) => (
                <div key={i} className="stat-card min-w-[120px]">
                  <div className="flex items-center gap-2 mb-1">
                    <Skeleton className="w-4 h-4 rounded-full" />
                    <Skeleton className="h-4 w-16" />
                  </div>
                  <Skeleton className="h-8 w-12" />
                </div>
              ))}
            </div>
          </div>

          <div className="flex flex-wrap gap-2 mt-8">
            {[1, 2, 3, 4, 5].map((i) => (
              <Skeleton key={i} className="h-9 w-24 rounded-full" />
            ))}
          </div>
        </div>
      </section>

      <section className="container mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
            <Skeleton className="w-12 h-12 rounded-2xl" />
            <div>
              <Skeleton className="h-8 w-32 mb-1" />
              <Skeleton className="h-4 w-24" />
            </div>
          </div>
          <Skeleton className="hidden md:block h-10 w-28 rounded-full" />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {Array.from({ length: 4 }).map((_, i) => (
            <DealCardSkeleton key={i} />
          ))}
        </div>
      </section>

      <section className="container mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
            <Skeleton className="w-12 h-12 rounded-2xl" />
            <div>
              <Skeleton className="h-8 w-32 mb-1" />
              <Skeleton className="h-4 w-24" />
            </div>
          </div>
          <Skeleton className="hidden md:block h-10 w-28 rounded-full" />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {Array.from({ length: 3 }).map((_, i) => (
            <CouponCardSkeleton key={i} />
          ))}
        </div>
      </section>
    </main>
  );
}
