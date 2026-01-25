import { Skeleton, StoreCardSkeleton } from '@/components/ui/skeleton';

export default function StoresLoading() {
  return (
    <main className="min-h-screen bg-background">
      <section className="relative overflow-hidden section-gradient border-b border-border/50">
        <div className="container mx-auto px-4 py-12 md:py-16 relative">
          <div className="max-w-3xl">
            <div className="flex items-center gap-2 mb-6">
              <Skeleton className="w-4 h-4 rounded-full" />
              <Skeleton className="h-4 w-32" />
            </div>

            <Skeleton className="h-12 w-3/4 mb-4" />
            <Skeleton className="h-6 w-full max-w-2xl mb-8" />

            <div className="flex flex-wrap items-center gap-4">
              {[1, 2, 3].map((i) => (
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
        </div>
      </section>

      <div className="sticky top-14 sm:top-16 z-30 bg-background/80 backdrop-blur-xl border-b border-border/40">
        <div className="container mx-auto px-4 py-3">
          <Skeleton className="h-11 w-full max-w-md rounded-xl" />
        </div>
      </div>

      <section className="container mx-auto px-4 py-8 md:py-12">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5 md:gap-6">
          {Array.from({ length: 12 }).map((_, i) => (
            <StoreCardSkeleton key={i} />
          ))}
        </div>
        <div className="mt-12 flex justify-center">
          <Skeleton className="h-10 w-64 rounded-lg" />
        </div>
      </section>
    </main>
  );
}
