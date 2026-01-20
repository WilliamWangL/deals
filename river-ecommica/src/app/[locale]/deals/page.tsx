import { Metadata } from 'next';
import { Suspense } from 'react';
import { getTranslations } from 'next-intl/server';
import { fetchDeals } from '@/lib/api';
import { getCurrentRegion } from '@/lib/region';
import { PAGINATION } from '@/constants/pagination';
import DealCard from '@/components/deal/DealCard';
import { DealsSearchBar } from '@/components/deal/DealsSearchBar';
import { DealPagination } from '@/components/deal/DealPagination';
import { EmptyState } from '@/components/ui/empty-state';
import {
  Tag,
  Percent,
  Clock,
  Zap,
  ArrowRight
} from 'lucide-react';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'deals' });

  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function DealsPage({
  params,
  searchParams
}: {
  params: Promise<{ locale: string }>;
  searchParams: Promise<{ q?: string; page?: string; region?: string }>
}) {
  await params;
  const queryParams = await searchParams;
  const searchQuery = queryParams.q?.trim() || '';
  const currentPage = parseInt(queryParams.page || String(PAGINATION.DEFAULT_PAGE), 10);
  const pageSize = PAGINATION.PAGE_SIZE.DEAL;
  const region = await getCurrentRegion(queryParams);

  const dealsResult = await fetchDeals({
    pageNo: currentPage,
    pageSize,
    region
  });
  const allDeals = dealsResult.list || [];
  const total = dealsResult.total || 0;

  const deals = searchQuery
    ? allDeals.filter(deal =>
        deal.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        deal.description?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        deal.merchant?.name?.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : allDeals;

  const now = new Date();
  const totalDeals = total;
  const activeDeals = deals.filter(d => !d.endTime || new Date(d.endTime) > now).length;
  const avgDiscount = totalDeals > 0
    ? Math.round(allDeals.reduce((acc, d) => acc + (d.discountPercent || 0), 0) / Math.max(1, allDeals.length))
    : 0;

  return (
    <main className="min-h-screen bg-background">
      {/* Hero Section - Compact & Urgent Style */}
      <section className="relative bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 overflow-hidden">
        {/* Animated gradient overlay */}
        <div className="absolute inset-0 bg-[linear-gradient(45deg,transparent_25%,rgba(251,146,60,0.1)_50%,transparent_75%)] bg-[length:250%_250%] animate-shimmer" />

        {/* Grid pattern */}
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)] bg-[size:32px_32px]" />

        <div className="container mx-auto px-4 py-8 md:py-12 relative">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
            {/* Left: Title & Description */}
            <div className="flex-1">
              <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-amber-500/20 border border-amber-500/30 text-amber-400 text-xs font-semibold mb-4">
                <Zap className="w-3.5 h-3.5" />
                LIVE DEALS
              </div>

              <h1 className="text-3xl md:text-4xl lg:text-5xl font-bold text-white tracking-tight mb-3">
                Today's Best
                <span className="block text-amber-400">Deals & Discounts</span>
              </h1>

              <p className="text-slate-400 text-base md:text-lg max-w-xl">
                Hand-picked savings updated every hour. Don't miss out.
              </p>
            </div>

            {/* Right: Stats Pills */}
            <div className="flex flex-wrap gap-3">
              <div className="flex items-center gap-2.5 px-4 py-2.5 rounded-xl bg-white/5 border border-white/10 backdrop-blur-sm">
                <Tag className="w-4 h-4 text-amber-400" />
                <div>
                  <span className="text-2xl font-bold text-white">{totalDeals}</span>
                  <span className="text-slate-400 text-sm ml-1.5">deals</span>
                </div>
              </div>

              <div className="flex items-center gap-2.5 px-4 py-2.5 rounded-xl bg-white/5 border border-white/10 backdrop-blur-sm">
                <Clock className="w-4 h-4 text-emerald-400" />
                <div>
                  <span className="text-2xl font-bold text-white">{activeDeals}</span>
                  <span className="text-slate-400 text-sm ml-1.5">active</span>
                </div>
              </div>

              <div className="flex items-center gap-2.5 px-4 py-2.5 rounded-xl bg-amber-500/20 border border-amber-500/30">
                <Percent className="w-4 h-4 text-amber-400" />
                <div>
                  <span className="text-2xl font-bold text-amber-400">{avgDiscount}%</span>
                  <span className="text-amber-400/70 text-sm ml-1.5">avg off</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Toolbar */}
      <div className="sticky top-14 sm:top-16 z-30 bg-background/80 backdrop-blur-xl border-b border-border/40">
        <div className="container mx-auto px-4 py-3">
          <Suspense fallback={<div className="h-11 bg-muted animate-pulse rounded-xl max-w-md" />}>
            <DealsSearchBar
              placeholder="Search deals..."
              className="max-w-md"
            />
          </Suspense>
        </div>
      </div>

      {/* Deals Grid */}
      <section className="container mx-auto px-4 py-8 md:py-12">
        {deals.length > 0 ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5 md:gap-6">
              {deals.map(deal => (
                <DealCard key={deal.id} deal={deal} />
              ))}
            </div>
            <div className="mt-12">
              <DealPagination total={total} pageSize={pageSize} currentPage={currentPage} />
            </div>
          </>
        ) : (
          <EmptyState
            icon="bag"
            title="No deals found"
            description="We couldn't find any deals matching your criteria. Try adjusting your search or check back later."
          />
        )}
      </section>
    </main>
  );
}
