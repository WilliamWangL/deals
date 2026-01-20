import { getTranslations } from 'next-intl/server';
import { DealCard } from '@/components/deal/DealCard';
import { StoreCard } from '@/components/store/StoreCard';
import { fetchDeals, fetchStores, fetchCategories } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { HeroSearchForm } from '@/components/home/HeroSearchForm';
import Link from 'next/link';
import {
  Tag,
  Store,
  TrendingUp,
  ArrowRight,
  Zap,
  ShieldCheck,
  Sparkles,
  Laptop,
  Shirt,
  Home,
  Dumbbell,
  Baby,
  ShoppingBasket,
  Heart,
  BadgePercent,
  Gift,
  Clock,
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
    <div className="min-h-screen bg-background">
      {/* Hero Section */}
      <section className="relative overflow-hidden hero-gradient text-white">
        {/* Background Effects */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="hero-glow w-[600px] h-[600px] bg-indigo-500 -top-[20%] -left-[10%] animate-pulse-glow" />
          <div className="hero-glow w-[500px] h-[500px] bg-violet-600 top-[30%] -right-[15%]" />
          <div className="hero-glow w-[400px] h-[400px] bg-amber-500/50 bottom-[10%] left-[20%]" />
          <div className="absolute inset-0 bg-[url('/grid.svg')] bg-center opacity-[0.03]" />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-transparent to-transparent" />
        </div>

        <div className="container relative mx-auto px-4 pt-20 pb-32 lg:pt-28 lg:pb-40">
          <div className="mx-auto max-w-4xl flex flex-col items-center text-center">
            {/* Trust Badge */}
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-700">
              <div className="inline-flex items-center gap-2 px-5 py-2 rounded-full bg-white/[0.08] backdrop-blur-md border border-white/10 text-sm font-medium text-indigo-200 mb-8 shadow-lg">
                <Sparkles className="w-4 h-4 text-amber-400" />
                <span>#1 Trusted Coupon Platform</span>
                <div className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              </div>
            </div>

            {/* Headline */}
            <h1 className="text-5xl md:text-6xl lg:text-7xl font-display font-bold tracking-tight mb-6 animate-in fade-in slide-in-from-bottom-6 duration-700 delay-150">
              <span className="block text-white">Save More on</span>
              <span className="block mt-2 bg-gradient-to-r from-amber-200 via-amber-300 to-orange-300 bg-clip-text text-transparent">
                Every Purchase
              </span>
            </h1>

            {/* Subtitle */}
            <p className="text-xl md:text-2xl text-indigo-100/80 max-w-2xl mb-10 leading-relaxed animate-in fade-in slide-in-from-bottom-6 duration-700 delay-300">
              {t('heroSubtitle')}
            </p>

            {/* Search Form */}
            <div className="w-full max-w-2xl mb-12 animate-in fade-in zoom-in-95 duration-700 delay-500">
              <HeroSearchForm
                placeholder={tCommon('searchPlaceholder')}
                buttonText={tCommon('search')}
              />
            </div>

            {/* Trust Indicators */}
            <div className="flex flex-wrap justify-center gap-6 md:gap-10 animate-in fade-in slide-in-from-bottom-4 duration-1000 delay-700">
              {[
                { icon: ShieldCheck, text: '100% Verified Codes', color: 'text-emerald-400' },
                { icon: TrendingUp, text: 'Updated Daily', color: 'text-amber-400' },
                { icon: Store, text: '10,000+ Stores', color: 'text-rose-400' }
              ].map(({ icon: Icon, text, color }) => (
                <div key={text} className="flex items-center gap-2.5 text-sm font-medium text-indigo-100/90">
                  <div className="p-2 rounded-xl bg-white/[0.08] backdrop-blur-sm">
                    <Icon className={`w-5 h-5 ${color}`} />
                  </div>
                  <span>{text}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Wave Divider */}
        <div className="absolute bottom-0 left-0 right-0">
          <svg viewBox="0 0 1440 100" fill="none" className="w-full h-auto">
            <path d="M0 100V50C240 16.67 480 0 720 0C960 0 1200 16.67 1440 50V100H0Z" className="fill-background"/>
          </svg>
        </div>
      </section>

      {/* Category Bar */}
      <section className="sticky top-0 z-40 bg-background/95 backdrop-blur-xl border-b border-border/50 shadow-sm">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-3 overflow-x-auto py-4 no-scrollbar">
            {categories.map((category) => {
              const Icon = IconMap[category.icon] || Tag;
              return (
                <Link
                  key={category.id}
                  href={`/${locale}/category/${category.slug}`}
                  className="group flex-shrink-0 flex items-center gap-2.5 px-5 py-2.5 rounded-full border border-border/80 bg-card text-sm font-medium text-muted-foreground hover:text-primary hover:border-primary/50 hover:bg-primary/5 transition-all duration-200 whitespace-nowrap"
                >
                  <Icon className="w-4 h-4 transition-colors group-hover:text-primary" />
                  <span>{category.name}</span>
                </Link>
              );
            })}
          </div>
        </div>
      </section>

      {/* Featured Deals Section */}
      <section className="py-16 lg:py-20">
        <div className="container mx-auto px-4">
          {/* Section Header */}
          <div className="flex items-center justify-between mb-10">
            <div className="flex items-center gap-4">
              <div className="p-3 rounded-2xl bg-gradient-to-br from-amber-100 to-orange-100 dark:from-amber-900/30 dark:to-orange-900/30">
                <Zap className="w-7 h-7 text-amber-600 dark:text-amber-400" />
              </div>
              <div>
                <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground">
                  {t('featuredDeals')}
                </h2>
                <p className="text-muted-foreground mt-1">Handpicked savings just for you</p>
              </div>
            </div>
            <Link
              href={`/${locale}/deals`}
              className="group hidden md:flex items-center gap-2 px-5 py-2.5 rounded-full bg-primary/5 text-primary font-semibold hover:bg-primary/10 transition-colors"
            >
              {tCommon('viewAll')}
              <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
            </Link>
          </div>

          {/* Deals Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {featuredDeals.length > 0 ? featuredDeals.map(deal => (
              <DealCard key={deal.id} deal={deal} />
            )) : (
              <div className="col-span-full py-16 text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-muted mb-4">
                  <Tag className="w-8 h-8 text-muted-foreground" />
                </div>
                <p className="text-muted-foreground text-lg">No deals found at the moment.</p>
              </div>
            )}
          </div>

          {/* Mobile View All */}
          <div className="mt-8 text-center md:hidden">
            <Link
              href={`/${locale}/deals`}
              className="inline-flex items-center gap-2 px-6 py-3 rounded-full bg-primary text-primary-foreground font-semibold"
            >
              {tCommon('viewAll')}
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </section>

      {/* Popular Stores Section */}
      <section className="py-16 lg:py-20 bg-gradient-to-b from-muted/30 via-muted/50 to-muted/30">
        <div className="container mx-auto px-4">
          {/* Section Header */}
          <div className="flex items-center justify-between mb-10">
            <div className="flex items-center gap-4">
              <div className="p-3 rounded-2xl bg-gradient-to-br from-violet-100 to-purple-100 dark:from-violet-900/30 dark:to-purple-900/30">
                <Store className="w-7 h-7 text-violet-600 dark:text-violet-400" />
              </div>
              <div>
                <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground">
                  {t('popularStores')}
                </h2>
                <p className="text-muted-foreground mt-1">Shop from brands you love</p>
              </div>
            </div>
            <Link
              href={`/${locale}/stores`}
              className="group hidden md:flex items-center gap-2 px-5 py-2.5 rounded-full bg-primary/5 text-primary font-semibold hover:bg-primary/10 transition-colors"
            >
              {tCommon('viewAll')}
              <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
            </Link>
          </div>

          {/* Stores Grid */}
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 md:gap-6">
            {popularStores.map(store => (
              <StoreCard key={store.id} store={store} locale={locale} />
            ))}
          </div>

          {/* Mobile View All */}
          <div className="mt-8 text-center md:hidden">
            <Link
              href={`/${locale}/stores`}
              className="inline-flex items-center gap-2 px-6 py-3 rounded-full bg-primary text-primary-foreground font-semibold"
            >
              {tCommon('viewAll')}
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 lg:py-20">
        <div className="container mx-auto px-4">
          <div className="text-center max-w-2xl mx-auto mb-12">
            <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground mb-4">
              Why Choose Ecommica?
            </h2>
            <p className="text-muted-foreground text-lg">
              We're committed to helping you save money on every purchase
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              {
                icon: BadgePercent,
                title: 'Verified Codes',
                description: 'Every coupon is tested and verified by our team before publishing.',
                gradient: 'from-emerald-500 to-teal-500'
              },
              {
                icon: Gift,
                title: 'Exclusive Deals',
                description: 'Access to exclusive discounts you won\'t find anywhere else.',
                gradient: 'from-violet-500 to-purple-500'
              },
              {
                icon: Clock,
                title: 'Updated Daily',
                description: 'Fresh deals and coupons added every day to maximize your savings.',
                gradient: 'from-amber-500 to-orange-500'
              }
            ].map(({ icon: Icon, title, description, gradient }) => (
              <div
                key={title}
                className="group relative p-8 rounded-3xl bg-card border border-border/50 hover:border-primary/20 hover:shadow-xl transition-all duration-300"
              >
                <div className={`inline-flex p-4 rounded-2xl bg-gradient-to-br ${gradient} mb-6 shadow-lg`}>
                  <Icon className="w-7 h-7 text-white" />
                </div>
                <h3 className="text-xl font-display font-bold text-foreground mb-3">{title}</h3>
                <p className="text-muted-foreground leading-relaxed">{description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 lg:py-24 relative overflow-hidden">
        <div className="absolute inset-0 hero-gradient" />
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="hero-glow w-[500px] h-[500px] bg-indigo-500 top-[-30%] right-[-10%]" />
          <div className="hero-glow w-[400px] h-[400px] bg-violet-600 bottom-[-20%] left-[-5%]" />
        </div>

        <div className="container relative mx-auto px-4 text-center">
          <div className="max-w-2xl mx-auto">
            <div className="inline-flex p-4 rounded-2xl bg-white/10 backdrop-blur-sm mb-8">
              <Sparkles className="w-10 h-10 text-amber-300" />
            </div>
            <h2 className="text-3xl md:text-4xl lg:text-5xl font-display font-bold text-white mb-6">
              Start Saving Today
            </h2>
            <p className="text-indigo-100/80 text-lg md:text-xl mb-10 leading-relaxed">
              Join thousands of smart shoppers who save money every day with our verified coupons and exclusive deals.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button
                size="lg"
                className="bg-white text-slate-900 hover:bg-slate-100 font-bold px-8 h-14 text-lg rounded-xl shadow-xl hover:shadow-2xl transition-all"
              >
                Browse All Deals
              </Button>
              <Button
                size="lg"
                variant="outline"
                className="border-white/30 text-white hover:bg-white/10 font-bold px-8 h-14 text-lg rounded-xl"
              >
                Join Newsletter
              </Button>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}
