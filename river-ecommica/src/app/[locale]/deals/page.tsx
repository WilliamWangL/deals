import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { fetchDeals } from '@/lib/api';
import DealCard from '@/components/deal/DealCard';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { 
  Zap,
  Search, 
  Tag, 
  Percent, 
  Clock, 
  SlidersHorizontal,
  TrendingUp,
  LayoutGrid,
  List,
  Flame,
  ShoppingBag
} from 'lucide-react';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'deals' });
  
  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function DealsPage() {
  const deals = await fetchDeals();
  const now = new Date();

  const totalDeals = deals.length;

  const activeDeals = deals.filter(d => !d.endTime || new Date(d.endTime) > now).length;
  
  const avgDiscount = totalDeals > 0 
    ? Math.round(deals.reduce((acc, d) => acc + (d.discountPercent || 0), 0) / totalDeals) 
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
                Don't miss out on these limited-time offers and exclusive savings.
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
            
            <div className="relative w-full md:w-96 group">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-hover:text-primary transition-colors">
                <Search className="w-5 h-5" />
              </div>
              <Input 
                placeholder="Search deals..." 
                className="pl-10 h-11 bg-slate-50 border-slate-200 focus:bg-white transition-all rounded-xl"
              />
            </div>

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
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {deals.map(deal => (
              <DealCard key={deal.id} deal={deal} />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-24 text-center">
            <div className="w-24 h-24 bg-slate-100 rounded-full flex items-center justify-center mb-6">
              <ShoppingBag className="w-12 h-12 text-slate-300" />
            </div>
            <h3 className="text-xl font-bold text-slate-900 mb-2">No deals found</h3>
            <p className="text-slate-500 max-w-md mx-auto">
              We couldn't find any deals matching your criteria. Try adjusting your search or check back later.
            </p>
            <Button variant="outline" className="mt-6">
              Clear all filters
            </Button>
          </div>
        )}
      </div>
    </main>
  );
}
