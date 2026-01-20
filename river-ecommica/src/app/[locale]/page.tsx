import { getTranslations } from 'next-intl/server';
import { DealCard } from '@/components/deal/DealCard';
import { StoreCard } from '@/components/store/StoreCard';
import { fetchDeals, fetchStores, fetchCategories } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { HeroSearchForm } from '@/components/home/HeroSearchForm';
import Link from 'next/link';
import {
  Tag,
  Store,
  TrendingUp,
  ArrowRight,
  Zap,
  ShieldCheck,
  Star,
  Laptop,
  Shirt,
  Home,
  Sparkles,
  Dumbbell,
  Baby,
  ShoppingBasket,
  Heart,
  type LucideIcon
} from 'lucide-react';

const IconMap: Record<string, LucideIcon> = {
  Laptop,
  Shirt,
  Home,
  Sparkles,
  Dumbbell,
  Baby,
  ShoppingBasket,
  Heart
};

export async function generateMetadata({params}: {params: Promise<{locale: string}>}) {
  const { locale } = await params;
  const t = await getTranslations({locale, namespace: 'Home'});
  return { title: t('title') };
}

export default async function HomePage({params}: {params: Promise<{locale: string}>}) {
  const { locale } = await params;
  const t = await getTranslations({locale, namespace: 'Home'});
  const tCommon = await getTranslations({locale, namespace: 'Common'});
  
  const [dealsResult, storesResult, categories] = await Promise.all([
    fetchDeals({ featured: true }),
    fetchStores({ pageNo: 1, pageSize: 6 }),
    fetchCategories()
  ]);

  const featuredDealsRaw = dealsResult.list.length > 0 ? dealsResult.list : (await fetchDeals()).list;
  const featuredDeals = featuredDealsRaw.slice(0, 8);
  const popularStores = storesResult.list.slice(0, 6);

  return (
    <div className="min-h-screen bg-gray-50/50">
      <section className="relative overflow-hidden bg-gradient-to-br from-slate-900 via-blue-950 to-cyan-900 text-white pb-32 pt-20 lg:pt-32">
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-[20%] -left-[10%] w-[700px] h-[700px] rounded-full bg-blue-600/20 blur-[100px] mix-blend-screen animate-pulse duration-[4000ms]"></div>
          <div className="absolute top-[20%] -right-[10%] w-[600px] h-[600px] rounded-full bg-teal-500/10 blur-[80px] mix-blend-screen"></div>
          <div className="absolute bottom-[-10%] left-[20%] w-[500px] h-[500px] rounded-full bg-indigo-600/20 blur-[100px] mix-blend-screen"></div>
          <div className="absolute inset-0 bg-[url('/grid.svg')] bg-center [mask-image:linear-gradient(180deg,white,rgba(255,255,255,0))] opacity-10"></div>
        </div>

        <div className="container relative mx-auto px-4 text-center z-10">
          <div className="mx-auto max-w-4xl flex flex-col items-center">
            
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-700 ease-out fill-mode-both">
              <Badge variant="secondary" className="mb-8 px-4 py-1.5 text-sm font-medium bg-white/5 text-cyan-100 hover:bg-white/10 border border-white/10 backdrop-blur-md shadow-[0_0_15px_rgba(34,211,238,0.2)] transition-all">
                <Sparkles className="mr-2 h-4 w-4 text-cyan-300" />
                #1 Trusted Coupon Site
              </Badge>
            </div>

            <h1 className="mb-8 text-5xl font-black tracking-tighter md:text-7xl lg:text-8xl bg-clip-text text-transparent bg-gradient-to-b from-white to-blue-200 drop-shadow-sm animate-in fade-in slide-in-from-bottom-8 duration-700 delay-150 ease-out fill-mode-both leading-[1.1]">
              {t('heroTitle')}
            </h1>
            
            <p className="mb-12 text-xl text-blue-100/90 md:text-2xl max-w-2xl font-light leading-relaxed animate-in fade-in slide-in-from-bottom-8 duration-700 delay-300 ease-out fill-mode-both">
              {t('heroSubtitle')}
            </p>
            
            <div className="w-full max-w-2xl mb-16 animate-in fade-in zoom-in-95 duration-700 delay-500 ease-out fill-mode-both">
              <HeroSearchForm
                placeholder={tCommon('searchPlaceholder')}
                buttonText={tCommon('search')}
              />
            </div>

            <div className="flex flex-wrap justify-center gap-4 md:gap-8 text-sm md:text-base font-medium text-slate-300 animate-in fade-in slide-in-from-bottom-4 duration-1000 delay-700 fill-mode-both">
              <div className="flex items-center gap-2.5 px-5 py-2.5 rounded-full bg-slate-900/30 border border-white/5 backdrop-blur-md hover:bg-slate-900/50 transition-colors cursor-default">
                <ShieldCheck className="h-5 w-5 text-emerald-400" />
                <span>100% Verified Codes</span>
              </div>
              <div className="flex items-center gap-2.5 px-5 py-2.5 rounded-full bg-slate-900/30 border border-white/5 backdrop-blur-md hover:bg-slate-900/50 transition-colors cursor-default">
                <TrendingUp className="h-5 w-5 text-amber-400" />
                <span>Updated Daily</span>
              </div>
              <div className="flex items-center gap-2.5 px-5 py-2.5 rounded-full bg-slate-900/30 border border-white/5 backdrop-blur-md hover:bg-slate-900/50 transition-colors cursor-default">
                <Store className="h-5 w-5 text-pink-400" />
                <span>10,000+ Stores</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="border-b bg-white sticky top-0 z-40 shadow-sm/50 backdrop-blur-xl bg-white/80">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-4 overflow-x-auto py-4 no-scrollbar [-ms-overflow-style:none] [scrollbar-width:none]">
            {categories.map((category) => {
              const Icon = IconMap[category.icon] || Tag;
              return (
                <Link 
                  key={category.id} 
                  href={`/${locale}/category/${category.slug}`}
                  className="flex flex-shrink-0 items-center gap-2 rounded-full border border-gray-200 bg-white px-5 py-2.5 text-sm font-medium text-gray-700 hover:border-blue-500 hover:text-blue-600 hover:shadow-md transition-all duration-200 whitespace-nowrap"
                >
                  <Icon className="h-4 w-4" />
                  {category.name}
                </Link>
              );
            })}
          </div>
        </div>
      </section>

      <section className="py-16 container mx-auto px-4">
        <div className="flex items-center justify-between mb-10">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-orange-100 rounded-lg text-orange-600">
              <Zap className="h-6 w-6" />
            </div>
            <h2 className="text-3xl font-bold tracking-tight text-gray-900">{t('featuredDeals')}</h2>
          </div>
          <Link 
            href={`/${locale}/deals`} 
            className="group flex items-center gap-1 text-sm font-semibold text-blue-600 hover:text-blue-700 transition-colors"
          >
            {tCommon('viewAll')}
            <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
          </Link>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 md:gap-8">
           {featuredDeals.length > 0 ? featuredDeals.map(deal => (
             <div key={deal.id} className="transition-transform duration-300 hover:-translate-y-1">
               <DealCard deal={deal} />
             </div>
           )) : (
             <div className="col-span-full py-12 text-center">
               <div className="inline-flex h-12 w-12 items-center justify-center rounded-full bg-gray-100 mb-4">
                 <Tag className="h-6 w-6 text-gray-400" />
               </div>
               <p className="text-gray-500 text-lg">No deals found at the moment.</p>
             </div>
           )}
        </div>
      </section>

      <section className="py-16 bg-white border-y border-gray-100">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between mb-10">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-purple-100 rounded-lg text-purple-600">
                <Store className="h-6 w-6" />
              </div>
              <h2 className="text-3xl font-bold tracking-tight text-gray-900">{t('popularStores')}</h2>
            </div>
            <Link 
              href={`/${locale}/stores`} 
              className="group flex items-center gap-1 text-sm font-semibold text-blue-600 hover:text-blue-700 transition-colors"
            >
              {tCommon('viewAll')}
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
            </Link>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 md:gap-6">
             {popularStores.map(store => (
               <div key={store.id} className="transition-transform duration-300 hover:scale-105">
                 <StoreCard store={store} locale={locale} />
               </div>
             ))}
          </div>
        </div>
      </section>

      <section className="py-20 bg-gray-900 text-white overflow-hidden relative">
        <div className="absolute top-0 left-0 w-full h-full overflow-hidden opacity-20 pointer-events-none">
          <div className="absolute top-[-50%] right-[-10%] w-[500px] h-[500px] rounded-full bg-blue-500 blur-[100px]"></div>
          <div className="absolute bottom-[-50%] left-[-10%] w-[500px] h-[500px] rounded-full bg-purple-500 blur-[100px]"></div>
        </div>
        
        <div className="container relative mx-auto px-4 text-center max-w-2xl">
          <Star className="h-10 w-10 text-yellow-400 mx-auto mb-6" />
          <h2 className="text-3xl md:text-4xl font-bold mb-6">Start Saving Today</h2>
          <p className="text-gray-300 mb-8 text-lg">
            Join thousands of smart shoppers who save money every day with our verified coupons and deals.
          </p>
          <Button size="lg" className="bg-white text-gray-900 hover:bg-gray-100 font-bold px-8 h-12 text-lg">
            Get Started Now
          </Button>
        </div>
      </section>
    </div>
  )
}
