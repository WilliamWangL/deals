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
  Compass,
  TrendingUp,
  Star
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
      {/* Hero Section - Brand Discovery Style */}
      <section className="relative overflow-hidden bg-gradient-to-b from-violet-50/80 via-background to-background">
        {/* Abstract brand shapes background */}
        <div className="absolute inset-0 pointer-events-none overflow-hidden">
          {/* Floating brand cards effect */}
          <div className="absolute top-8 left-[10%] w-16 h-16 rounded-2xl bg-white shadow-lg border border-violet-100 rotate-12 opacity-60" />
          <div className="absolute top-20 right-[15%] w-12 h-12 rounded-xl bg-white shadow-md border border-violet-100 -rotate-6 opacity-50" />
          <div className="absolute bottom-12 left-[20%] w-14 h-14 rounded-2xl bg-white shadow-lg border border-violet-100 rotate-6 opacity-40" />
          <div className="absolute top-1/3 right-[8%] w-10 h-10 rounded-lg bg-white shadow-md border border-violet-100 rotate-12 opacity-30" />

          {/* Gradient orbs */}
          <div className="absolute -top-20 left-1/2 -translate-x-1/2 w-[600px] h-[300px] bg-gradient-to-br from-violet-200/30 via-purple-100/20 to-transparent rounded-full blur-3xl" />
        </div>

        <div className="container mx-auto px-4 py-12 md:py-16 relative">
          <div className="max-w-3xl">
            {/* Breadcrumb-like intro */}
            <div className="flex items-center gap-2 text-sm text-muted-foreground mb-6">
              <Compass className="w-4 h-4" />
              <span>Explore</span>
              <span className="text-violet-400">/</span>
              <span className="text-violet-600 font-medium">All Brands</span>
            </div>

            {/* Main Title */}
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground tracking-tight mb-4">
              Discover
              <span className="relative ml-3">
                <span className="relative z-10 text-violet-600">Top Stores</span>
                <svg className="absolute -bottom-2 left-0 w-full h-3 text-violet-200" viewBox="0 0 100 12" preserveAspectRatio="none">
                  <path d="M0,8 Q50,0 100,8" stroke="currentColor" strokeWidth="4" fill="none" />
                </svg>
              </span>
            </h1>

            <p className="text-lg md:text-xl text-muted-foreground max-w-2xl mb-8">
              Browse {totalStores}+ premium brands with exclusive deals and verified coupon codes.
            </p>

            {/* Stats Row - Inline Pills */}
            <div className="flex flex-wrap items-center gap-3">
              <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white border border-violet-100 shadow-sm">
                <StoreIcon className="w-4 h-4 text-violet-600" />
                <span className="font-bold text-foreground">{totalStores}</span>
                <span className="text-muted-foreground text-sm">stores</span>
              </div>

              <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white border border-violet-100 shadow-sm">
                <Tag className="w-4 h-4 text-amber-500" />
                <span className="font-bold text-foreground">{totalDeals}</span>
                <span className="text-muted-foreground text-sm">deals</span>
              </div>

              <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white border border-violet-100 shadow-sm">
                <Ticket className="w-4 h-4 text-emerald-500" />
                <span className="font-bold text-foreground">{totalCoupons}</span>
                <span className="text-muted-foreground text-sm">codes</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Toolbar */}
      <div className="sticky top-14 sm:top-16 z-30 bg-background/80 backdrop-blur-xl border-b border-border/40">
        <div className="container mx-auto px-4 py-3">
          <Suspense fallback={<div className="h-11 bg-muted animate-pulse rounded-xl max-w-md" />}>
            <StoresSearchBar
              placeholder="Search stores..."
              className="max-w-md"
            />
          </Suspense>
        </div>
      </div>

      {/* Stores Grid */}
      <section className="container mx-auto px-4 py-8 md:py-12">
        {stores.length > 0 ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5 md:gap-6">
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
            <div className="w-20 h-20 rounded-2xl bg-muted/50 flex items-center justify-center mb-6">
              <StoreIcon className="w-10 h-10 text-muted-foreground/50" />
            </div>
            <h3 className="text-xl font-bold text-foreground mb-2">No stores found</h3>
            <p className="text-muted-foreground max-w-md mx-auto">
              We couldn&apos;t find any stores matching your search. Try a different keyword.
            </p>
            <Button variant="outline" className="mt-6 rounded-xl" asChild>
              <Link href={`/${locale}/stores`}>Clear search</Link>
            </Button>
          </div>
        )}
      </section>
    </main>
  );
}
