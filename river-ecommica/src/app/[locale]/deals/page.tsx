import { Metadata } from 'next';
import { Suspense } from 'react';
import { getTranslations } from 'next-intl/server';
import { fetchDeals } from '@/lib/api';
import { PAGINATION } from '@/constants/pagination';
import DealCard from '@/components/deal/DealCard';
import { DealsSearchBar } from '@/components/deal/DealsSearchBar';
import { DealPagination } from '@/components/deal/DealPagination';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
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
  Flame
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
  await params; // Extract locale when i18n is implemented
  const queryParams = await searchParams;
  const searchQuery = queryParams.q?.trim() || '';
  const currentPage = parseInt(queryParams.page || String(PAGINATION.DEFAULT_PAGE), 10);
  const pageSize = PAGINATION.PAGE_SIZE.DEAL;

  const dealsResult = await fetchDeals({
    pageNo: currentPage,
    pageSize,
    featured: true
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
    <main className="min-h-screen bg-slate-50/50 pb-12">
      <div className="bg-white border-b border-slate-200 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 right-0 p-12 opacity-5 pointer-events-none">
           <Zap className="w-64 h-64 text-amber-500 transform -rotate-12 translate-x-20 -translate-y-20" />
        </div>

        <div className="container mx-auto px-4 py-10 md:py-14 relative z-10">
          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
            <div className="max-w-2xl">
              <div className="flex items-center gap-3 mb-4">
                <div className="p-3 bg-amber-50 rounded-2xl">
                  <Flame className="w-8 h-8 text-amber-500" />
                </div>
                <Badge variant="secondary" className="px-3 py-1 bg-amber-50 text-amber-700 border-amber-100">
                  Updated Hourly
                </Badge>
              </div>
              <h1 className="text-4xl md:text-5xl font-extrabold text-slate-900 tracking-tight mb-4">
                Top Deals & Discounts
              </h1>
              <p className="text-lg text-slate-600 leading-relaxed">
                Discover the best bargains from your favorite brands. 
                Don&apos;t miss out on these limited-time offers and exclusive savings.
              </p>
            </div>

            <div className="flex gap-4 md:gap-8 flex-wrap">
              <div className="flex flex-col">
                <div className="flex items-center gap-2 text-slate-500 text-sm font-medium mb-1">
                  <Tag className="w-4 h-4" /> Total Deals
                </div>
                <span className="text-2xl font-bold text-slate-900">{totalDeals.toLocaleString()}</span>
              </div>
              <div className="w-px h-12 bg-slate-200 hidden md:block" />
              <div className="flex flex-col">
                <div className="flex items-center gap-2 text-slate-500 text-sm font-medium mb-1">
                  <Clock className="w-4 h-4" /> Active Now
                </div>
                <span className="text-2xl font-bold text-slate-900">{activeDeals.toLocaleString()}</span>
              </div>
              <div className="w-px h-12 bg-slate-200 hidden md:block" />
              <div className="flex flex-col">
                <div className="flex items-center gap-2 text-slate-500 text-sm font-medium mb-1">
                  <Percent className="w-4 h-4" /> Avg. Saving
                </div>
                <span className="text-2xl font-bold text-emerald-600">{avgDiscount}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="sticky top-0 z-30 bg-white/80 backdrop-blur-md border-b border-slate-200 shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <div className="flex flex-col md:flex-row gap-4 justify-between items-center">

            <Suspense fallback={<div className="md:w-96 h-11 bg-slate-100 animate-pulse rounded-xl" />}>
              <DealsSearchBar
                placeholder="Search deals..."
                className="md:w-96"
              />
            </Suspense>

            <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-2 md:pb-0 scrollbar-hide">
              <Button variant="outline" size="sm" className="h-10 rounded-lg border-slate-200 text-slate-600 hover:text-primary hover:border-primary/50 gap-2 shrink-0">
                <SlidersHorizontal className="w-4 h-4" />
                Filter
              </Button>
              <div className="w-px h-6 bg-slate-200 mx-1 shrink-0" />
              <Button variant="ghost" size="sm" className="h-10 rounded-lg text-slate-600 hover:text-primary hover:bg-primary/5 gap-2 shrink-0">
                <TrendingUp className="w-4 h-4" />
                Trending
              </Button>
              <Button variant="ghost" size="sm" className="h-10 rounded-lg text-slate-600 hover:text-primary hover:bg-primary/5 gap-2 shrink-0">
                <Clock className="w-4 h-4" />
                Ending Soon
              </Button>
              <div className="ml-auto flex items-center gap-1 border rounded-lg p-1 border-slate-200 shrink-0 bg-slate-50">
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

      <div className="container mx-auto px-4 py-8">
        {deals.length > 0 ? (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {deals.map(deal => (
                <DealCard key={deal.id} deal={deal} />
              ))}
            </div>
            <DealPagination total={total} pageSize={pageSize} currentPage={currentPage} />
          </>
        ) : (
          <EmptyState
            icon="bag"
            title="No deals found"
            description="We couldn't find any deals matching your criteria. Try adjusting your search or check back later."
          />
        )}
      </div>
    </main>
  );
}
