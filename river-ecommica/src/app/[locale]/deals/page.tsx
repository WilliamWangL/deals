import { Metadata } from 'next';
import { Suspense } from 'react';
import { getTranslations } from 'next-intl/server';
import { fetchDeals } from '@/lib/api';
import { PAGINATION } from '@/constants/pagination';
import DealCard from '@/components/deal/DealCard';
import { DealsSearchBar } from '@/components/deal/DealsSearchBar';
import { DealPagination } from '@/components/deal/DealPagination';
import { Button } from '@/components/ui/button';
import { EmptyState } from '@/components/ui/empty-state';
import {
  Zap,
  Tag,
  Percent,
  Clock,
  SlidersHorizontal,
  TrendingUp,
  LayoutGrid,
  List,
  Flame,
  Sparkles
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
  searchParams: Promise<{ q?: string; page?: string }>
}) {
  await params;
  const queryParams = await searchParams;
  const searchQuery = queryParams.q?.trim() || '';
  const currentPage = parseInt(queryParams.page || String(PAGINATION.DEFAULT_PAGE), 10);
  const pageSize = PAGINATION.PAGE_SIZE.DEAL;

  const dealsResult = await fetchDeals({
    pageNo: currentPage,
    pageSize
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
      {/* Hero Header */}
      <section className="page-header py-12 md:py-16">
        {/* Background decoration */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-20 -right-20 w-96 h-96 bg-gradient-to-br from-amber-200/30 to-orange-200/30 rounded-full blur-3xl" />
          <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-gradient-to-br from-rose-200/20 to-pink-200/20 rounded-full blur-3xl" />
        </div>

        <div className="container mx-auto px-4 relative">
          <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-8">
            {/* Title Section */}
            <div className="max-w-2xl">
              <div className="flex items-center gap-3 mb-5">
                <div className="p-3 rounded-2xl bg-gradient-to-br from-amber-100 to-orange-100">
                  <Flame className="w-8 h-8 text-amber-600" />
                </div>
                <div className="badge-featured">
                  <Sparkles className="w-3.5 h-3.5" />
                  Updated Hourly
                </div>
              </div>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-display font-bold text-foreground tracking-tight mb-4">
                Top Deals &amp; Discounts
              </h1>
              <p className="text-lg md:text-xl text-muted-foreground leading-relaxed">
                Discover the best bargains from your favorite brands.
                Don&apos;t miss out on these limited-time offers and exclusive savings.
              </p>
            </div>

            {/* Stats Cards */}
            <div className="flex gap-4 md:gap-6 flex-wrap lg:flex-nowrap">
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Tag className="w-4 h-4" />
                  <span>Total Deals</span>
                </div>
                <span className="stat-value">{totalDeals.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Clock className="w-4 h-4" />
                  <span>Active Now</span>
                </div>
                <span className="stat-value">{activeDeals.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Percent className="w-4 h-4" />
                  <span>Avg. Saving</span>
                </div>
                <span className="stat-value text-gradient-savings">{avgDiscount}%</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Toolbar */}
      <section className="sticky top-0 z-30 bg-background/95 backdrop-blur-xl border-b border-border/50 shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <div className="flex flex-col md:flex-row gap-4 justify-between items-center">
            {/* Search */}
            <Suspense fallback={<div className="md:w-96 h-12 bg-muted animate-pulse rounded-xl" />}>
              <DealsSearchBar
                placeholder="Search deals..."
                className="md:w-96"
              />
            </Suspense>

            {/* Filters & View */}
            <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-2 md:pb-0 no-scrollbar">
              <Button
                variant="outline"
                size="sm"
                className="h-10 rounded-xl border-border/80 text-muted-foreground hover:text-primary hover:border-primary/50 gap-2 shrink-0"
              >
                <SlidersHorizontal className="w-4 h-4" />
                Filter
              </Button>
              <div className="w-px h-6 bg-border mx-1 shrink-0" />
              <Button
                variant="ghost"
                size="sm"
                className="h-10 rounded-xl text-muted-foreground hover:text-primary hover:bg-primary/5 gap-2 shrink-0"
              >
                <TrendingUp className="w-4 h-4" />
                Trending
              </Button>
              <Button
                variant="ghost"
                size="sm"
                className="h-10 rounded-xl text-muted-foreground hover:text-primary hover:bg-primary/5 gap-2 shrink-0"
              >
                <Clock className="w-4 h-4" />
                Ending Soon
              </Button>
              <div className="ml-auto flex items-center gap-1 p-1 rounded-xl border border-border/80 bg-muted/30 shrink-0">
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 rounded-lg bg-card text-foreground shadow-sm hover:bg-card"
                >
                  <LayoutGrid className="w-4 h-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 rounded-lg text-muted-foreground hover:text-foreground hover:bg-muted/50"
                >
                  <List className="w-4 h-4" />
                </Button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Deals Grid */}
      <section className="container mx-auto px-4 py-10">
        {deals.length > 0 ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
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
