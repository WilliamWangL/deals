import { Skeleton, CouponCardSkeleton } from '@/components/ui/skeleton';

export default function CouponsLoading() {
  return (
    <main className="min-h-screen bg-background">
      <section className="relative overflow-hidden section-gradient border-b border-border/50">
        <div className="absolute inset-0 bg-dots-pattern opacity-50" />

        <div className="container mx-auto px-4 py-10 md:py-14 relative">
          <div className="relative max-w-5xl mx-auto">
            <div className="bg-card rounded-3xl shadow-xl shadow-primary/5 border border-border overflow-hidden">
              <div className="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/2 w-8 h-8 bg-background rounded-full border-r border-border" />
              <div className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 w-8 h-8 bg-background rounded-full border-l border-border" />

              <div className="flex flex-col md:flex-row">
                <div className="flex-1 p-8 md:p-10">
                  <div className="flex items-center gap-2 mb-4">
                    <Skeleton className="h-8 w-8 rounded-lg" />
                    <Skeleton className="h-4 w-24" />
                  </div>

                  <Skeleton className="h-12 w-3/4 mb-4" />
                  <Skeleton className="h-12 w-1/2 mb-4" />

                  <Skeleton className="h-6 w-full max-w-lg mb-6" />

                  <Skeleton className="h-9 w-48 rounded-full" />
                </div>

                <div className="relative md:border-l border-dashed border-border p-8 md:p-10 bg-muted/10 flex flex-col justify-center min-w-[280px]">
                  <div className="space-y-6">
                    {[1, 2, 3].map((i) => (
                      <div key={i} className="flex items-center justify-between md:justify-start md:gap-4">
                        <div className="flex items-center gap-2">
                          <Skeleton className="w-4 h-4 rounded-full" />
                          <Skeleton className="h-4 w-16" />
                        </div>
                        <Skeleton className="h-8 w-12" />
                      </div>
                    ))}
                  </div>
                </div>
              </div>
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
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5 md:gap-6">
          {Array.from({ length: 9 }).map((_, i) => (
            <CouponCardSkeleton key={i} />
          ))}
        </div>
        <div className="mt-12 flex justify-center">
          <Skeleton className="h-10 w-64 rounded-lg" />
        </div>
      </section>
    </main>
  );
}
