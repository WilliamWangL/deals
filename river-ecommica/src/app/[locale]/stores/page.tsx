import { Metadata } from 'next';
import Link from 'next/link';
import { Suspense } from 'react';
import { getTranslations } from 'next-intl/server';
import { fetchStores } from '@/lib/api';
import { PAGINATION } from '@/constants/pagination';
import StoreCard from '@/components/store/StoreCard';
import { StoresSearchBar } from '@/components/store/StoresSearchBar';
import { StorePagination } from '@/components/store/StorePagination';
import { Button } from '@/components/ui/button';
import {
  Store as StoreIcon,
  Tag,
  Ticket,
  Building2,
  SlidersHorizontal,
  MapPin,
  TrendingUp,
  LayoutGrid,
  List,
  Sparkles
} from 'lucide-react';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'stores' });

  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function StoresPage({
  params,
  searchParams
}: {
  params: Promise<{ locale: string }>;
  searchParams: Promise<{ q?: string; page?: string }>
}) {
  const { locale } = await params;
  const queryParams = await searchParams;
  const searchQuery = queryParams.q?.trim() || '';
  const currentPage = parseInt(queryParams.page || String(PAGINATION.DEFAULT_PAGE), 10);
  const pageSize = PAGINATION.PAGE_SIZE.STORE;

  const storesResult = await fetchStores({
    pageNo: currentPage,
    pageSize,
    name: searchQuery || undefined
  });
  const stores = storesResult.list || [];
  const total = storesResult.total || 0;

  const totalStores = total;
  const totalDeals = stores.reduce((acc, s) => acc + (s.dealCount || 0), 0);
  const totalCoupons = stores.reduce((acc, s) => acc + (s.couponCount || 0), 0);

  return (
    <main className="min-h-screen bg-background">
      {/* Hero Header */}
      <section className="page-header py-12 md:py-16">
        {/* Background decoration */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-20 -right-20 w-96 h-96 bg-gradient-to-br from-violet-200/30 to-purple-200/30 rounded-full blur-3xl" />
          <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-gradient-to-br from-indigo-200/20 to-blue-200/20 rounded-full blur-3xl" />
        </div>

        <div className="container mx-auto px-4 relative">
          <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-8">
            {/* Title Section */}
            <div className="max-w-2xl">
              <div className="flex items-center gap-3 mb-5">
                <div className="p-3 rounded-2xl bg-gradient-to-br from-violet-100 to-purple-100">
                  <StoreIcon className="w-8 h-8 text-violet-600" />
                </div>
                <div className="badge-exclusive">
                  <Sparkles className="w-3.5 h-3.5" />
                  Updated Daily
                </div>
              </div>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-display font-bold text-foreground tracking-tight mb-4">
                Explore Top Stores
              </h1>
              <p className="text-lg md:text-xl text-muted-foreground leading-relaxed">
                Browse our curated collection of premium brands. Discover exclusive coupons,
                limited-time deals, and verified discounts from your favorite retailers.
              </p>
            </div>

            {/* Stats Cards */}
            <div className="flex gap-4 md:gap-6 flex-wrap lg:flex-nowrap">
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Building2 className="w-4 h-4" />
                  <span>Stores</span>
                </div>
                <span className="stat-value">{totalStores.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Tag className="w-4 h-4" />
                  <span>Deals</span>
                </div>
                <span className="stat-value">{totalDeals.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Ticket className="w-4 h-4" />
                  <span>Coupons</span>
                </div>
                <span className="stat-value text-gradient-primary">{totalCoupons.toLocaleString()}</span>
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
            <Suspense fallback={<div className="w-full md:w-96 h-12 bg-muted animate-pulse rounded-xl" />}>
              <StoresSearchBar
                placeholder="Search stores..."
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
                <MapPin className="w-4 h-4" />
                Region
              </Button>
              <Button
                variant="ghost"
                size="sm"
                className="h-10 rounded-xl text-muted-foreground hover:text-primary hover:bg-primary/5 gap-2 shrink-0"
              >
                <TrendingUp className="w-4 h-4" />
                Popular
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

      {/* Stores Grid */}
      <section className="container mx-auto px-4 py-10">
        {stores.length > 0 ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {stores.map(store => (
                <StoreCard key={store.id} store={store} locale={locale} />
              ))}
            </div>
            <div className="mt-12">
              <StorePagination total={total} pageSize={pageSize} currentPage={currentPage} />
            </div>
          </>
        ) : (
          <div className="flex flex-col items-center justify-center py-24 text-center">
            <div className="w-20 h-20 rounded-2xl bg-muted flex items-center justify-center mb-6">
              <Building2 className="w-10 h-10 text-muted-foreground/50" />
            </div>
            <h3 className="text-xl font-display font-bold text-foreground mb-2">No stores found</h3>
            <p className="text-muted-foreground max-w-md mx-auto">
              We couldn&apos;t find any stores matching your criteria. Try adjusting your search or filters.
            </p>
            <Button variant="outline" className="mt-6 rounded-xl" asChild>
              <Link href={`/${locale}/stores`}>Clear all filters</Link>
            </Button>
          </div>
        )}
      </section>
    </main>
  );
}
