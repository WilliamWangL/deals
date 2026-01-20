import { getTranslations } from 'next-intl/server';
import { DealCard } from '@/components/deal/DealCard';
import { StoreCard } from '@/components/store/StoreCard';
import { AffiliateNetworks } from '@/components/home/AffiliateNetworks';
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
  ChevronRight,
  Star,
  Users,
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
      {/* ============================================
          HERO SECTION - Premium Dark Theme
          ============================================ */}
      <section className="relative overflow-hidden bg-gradient-to-b from-slate-950 via-indigo-950/90 to-slate-900">
        {/* Animated Background Mesh */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          {/* Primary glow - top left */}
          <div className="absolute -top-32 -left-32 w-[600px] h-[600px] bg-indigo-600/30 rounded-full blur-[120px] animate-pulse-glow" />
          {/* Secondary glow - right */}
          <div className="absolute top-1/4 -right-20 w-[500px] h-[500px] bg-violet-600/25 rounded-full blur-[100px]" />
          {/* Accent glow - bottom */}
          <div className="absolute bottom-0 left-1/3 w-[400px] h-[400px] bg-amber-500/15 rounded-full blur-[80px]" />

          {/* Subtle grid overlay */}
          <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)] bg-[size:60px_60px]" />

          {/* Gradient fade at bottom */}
          <div className="absolute inset-x-0 bottom-0 h-40 bg-gradient-to-t from-slate-900 to-transparent" />
        </div>

        <div className="container relative mx-auto px-4 pt-16 pb-28 lg:pt-24 lg:pb-36">
          <div className="mx-auto max-w-4xl flex flex-col items-center text-center">
            {/* Trust Badge - Floating pill */}
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-700">
              <div className="inline-flex items-center gap-2.5 px-5 py-2.5 rounded-full bg-white/[0.07] backdrop-blur-xl border border-white/[0.08] text-sm font-medium mb-10 shadow-2xl shadow-black/20">
                <div className="flex items-center gap-1">
                  <Star className="w-4 h-4 text-amber-400 fill-amber-400" />
                  <Star className="w-4 h-4 text-amber-400 fill-amber-400" />
                  <Star className="w-4 h-4 text-amber-400 fill-amber-400" />
                  <Star className="w-4 h-4 text-amber-400 fill-amber-400" />
                  <Star className="w-4 h-4 text-amber-400 fill-amber-400" />
                </div>
                <span className="text-white/90">Trusted by 50,000+ Shoppers</span>
                <div className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
              </div>
            </div>

            {/* Headline - Bold & Clear */}
            <h1 className="text-5xl md:text-6xl lg:text-7xl font-display font-bold tracking-tight mb-8 animate-in fade-in slide-in-from-bottom-6 duration-700 delay-150">
              <span className="text-white">Smart Savings,</span>
              <br />
              <span className="bg-gradient-to-r from-amber-300 via-yellow-200 to-amber-400 bg-clip-text text-transparent">
                Every Purchase
              </span>
            </h1>

            {/* Subtitle */}
            <p className="text-lg md:text-xl text-slate-300/90 max-w-2xl mb-10 leading-relaxed animate-in fade-in slide-in-from-bottom-6 duration-700 delay-300">
              {t('heroSubtitle')}
            </p>

            {/* Search Form */}
            <div className="w-full max-w-2xl mb-14 animate-in fade-in zoom-in-95 duration-700 delay-500">
              <HeroSearchForm
                placeholder={tCommon('searchPlaceholder')}
                buttonText={tCommon('search')}
              />
            </div>

            {/* Trust Indicators - Refined Pills */}
            <div className="flex flex-wrap justify-center gap-4 md:gap-6 animate-in fade-in slide-in-from-bottom-4 duration-1000 delay-700">
              {[
                { icon: ShieldCheck, text: 'Verified Codes', color: 'text-emerald-400', bg: 'bg-emerald-500/10' },
                { icon: TrendingUp, text: 'Updated Daily', color: 'text-amber-400', bg: 'bg-amber-500/10' },
                { icon: Users, text: '10,000+ Stores', color: 'text-blue-400', bg: 'bg-blue-500/10' }
              ].map(({ icon: Icon, text, color, bg }) => (
                <div key={text} className={`flex items-center gap-2.5 px-4 py-2 rounded-full ${bg} border border-white/5`}>
                  <Icon className={`w-4 h-4 ${color}`} />
                  <span className="text-sm font-medium text-white/80">{text}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Smooth curve transition */}
        <div className="absolute bottom-0 left-0 right-0">
          <svg viewBox="0 0 1440 80" fill="none" className="w-full h-auto" preserveAspectRatio="none">
            <path d="M0 80V40C360 10 720 0 1080 10C1260 20 1380 35 1440 40V80H0Z" className="fill-background"/>
          </svg>
        </div>
      </section>

      {/* ============================================
          CATEGORY BAR - Elevated Navigation
          ============================================ */}
      <section className="sticky top-0 z-40 bg-background/80 backdrop-blur-2xl border-b border-border/30">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-2 overflow-x-auto py-3 no-scrollbar">
            {categories.map((category, index) => {
              const Icon = IconMap[category.icon] || Tag;
              return (
                <Link
                  key={category.id}
                  href={`/${locale}/category/${category.slug}`}
                  className="group flex-shrink-0 flex items-center gap-2 px-4 py-2 rounded-xl bg-transparent hover:bg-primary/5 border border-transparent hover:border-primary/20 text-sm font-medium text-muted-foreground hover:text-primary transition-all duration-200 whitespace-nowrap"
                  style={{ animationDelay: `${index * 50}ms` }}
                >
                  <Icon className="w-4 h-4 opacity-60 group-hover:opacity-100 transition-opacity" />
                  <span>{category.name}</span>
                </Link>
              );
            })}
          </div>
        </div>
      </section>

      {/* ============================================
          FEATURED DEALS - With Visual Hierarchy
          ============================================ */}
      <section className="py-16 lg:py-24">
        <div className="container mx-auto px-4">
          {/* Section Header - Unified Style */}
          <div className="flex items-end justify-between mb-10 lg:mb-12">
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <div className="p-2 rounded-xl bg-gradient-to-br from-amber-500 to-orange-600 shadow-lg shadow-amber-500/20">
                  <Zap className="w-5 h-5 text-white" />
                </div>
                <span className="text-sm font-semibold text-amber-600 dark:text-amber-400 uppercase tracking-wider">
                  Hot Deals
                </span>
              </div>
              <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground">
                {t('featuredDeals')}
              </h2>
              <p className="text-muted-foreground text-lg">
                Handpicked savings, updated every hour
              </p>
            </div>
            <Link
              href={`/${locale}/deals`}
              className="group hidden md:flex items-center gap-2 text-primary font-semibold hover:gap-3 transition-all duration-200"
            >
              {tCommon('viewAll')}
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>

          {/* Deals Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5 lg:gap-6">
            {featuredDeals.length > 0 ? featuredDeals.map((deal, index) => (
              <div
                key={deal.id}
                className="animate-in fade-in slide-in-from-bottom-4"
                style={{ animationDelay: `${index * 50}ms`, animationFillMode: 'both' }}
              >
                <DealCard deal={deal} />
              </div>
            )) : (
              <div className="col-span-full py-20 text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-muted mb-4">
                  <Tag className="w-8 h-8 text-muted-foreground" />
                </div>
                <p className="text-muted-foreground text-lg">No deals found at the moment.</p>
              </div>
            )}
          </div>

          {/* Mobile CTA */}
          <div className="mt-10 text-center md:hidden">
            <Button asChild className="rounded-xl px-8 h-12 font-semibold">
              <Link href={`/${locale}/deals`}>
                {tCommon('viewAll')}
                <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* ============================================
          POPULAR STORES - Brand Showcase
          ============================================ */}
      <section className="py-16 lg:py-24 bg-gradient-to-b from-slate-50/50 via-slate-100/50 to-slate-50/50 dark:from-slate-900/50 dark:via-slate-800/30 dark:to-slate-900/50">
        <div className="container mx-auto px-4">
          {/* Section Header */}
          <div className="flex items-end justify-between mb-10 lg:mb-12">
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <div className="p-2 rounded-xl bg-gradient-to-br from-violet-500 to-purple-600 shadow-lg shadow-violet-500/20">
                  <Store className="w-5 h-5 text-white" />
                </div>
                <span className="text-sm font-semibold text-violet-600 dark:text-violet-400 uppercase tracking-wider">
                  Top Brands
                </span>
              </div>
              <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground">
                {t('popularStores')}
              </h2>
              <p className="text-muted-foreground text-lg">
                Shop from brands you trust
              </p>
            </div>
            <Link
              href={`/${locale}/stores`}
              className="group hidden md:flex items-center gap-2 text-primary font-semibold hover:gap-3 transition-all duration-200"
            >
              {tCommon('viewAll')}
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>

          {/* Stores Grid */}
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 md:gap-5">
            {popularStores.map((store, index) => (
              <div
                key={store.id}
                className="animate-in fade-in zoom-in-95"
                style={{ animationDelay: `${index * 50}ms`, animationFillMode: 'both' }}
              >
                <StoreCard store={store} locale={locale} />
              </div>
            ))}
          </div>

          {/* Mobile CTA */}
          <div className="mt-10 text-center md:hidden">
            <Button asChild variant="outline" className="rounded-xl px-8 h-12 font-semibold">
              <Link href={`/${locale}/stores`}>
                {tCommon('viewAll')}
                <ArrowRight className="w-4 h-4 ml-2" />
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* ============================================
          FEATURES SECTION - Why Choose Us
          ============================================ */}
      <section className="py-16 lg:py-24">
        <div className="container mx-auto px-4">
          {/* Section Header - Centered */}
          <div className="text-center max-w-2xl mx-auto mb-14">
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-primary/5 border border-primary/10 text-primary text-sm font-medium mb-6">
              <Sparkles className="w-4 h-4" />
              <span>Why Ecommica</span>
            </div>
            <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground mb-4">
              Save Smarter, Not Harder
            </h2>
            <p className="text-muted-foreground text-lg">
              We do the work so you can enjoy the savings
            </p>
          </div>

          {/* Features Grid - Refined Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 lg:gap-8">
            {[
              {
                icon: BadgePercent,
                title: 'Verified Codes',
                description: 'Every coupon is tested and verified by our team before publishing. No expired codes, ever.',
                gradient: 'from-emerald-500 to-teal-600',
                shadowColor: 'shadow-emerald-500/20'
              },
              {
                icon: Gift,
                title: 'Exclusive Deals',
                description: 'Access to partner-exclusive discounts you won\'t find anywhere else online.',
                gradient: 'from-violet-500 to-purple-600',
                shadowColor: 'shadow-violet-500/20'
              },
              {
                icon: Clock,
                title: 'Real-Time Updates',
                description: 'Fresh deals and coupons added hourly. Be the first to grab the best offers.',
                gradient: 'from-amber-500 to-orange-600',
                shadowColor: 'shadow-amber-500/20'
              }
            ].map(({ icon: Icon, title, description, gradient, shadowColor }, index) => (
              <div
                key={title}
                className="group relative p-8 rounded-2xl bg-card border border-border/50 hover:border-border hover:shadow-xl transition-all duration-300 animate-in fade-in slide-in-from-bottom-4"
                style={{ animationDelay: `${index * 100}ms`, animationFillMode: 'both' }}
              >
                {/* Icon */}
                <div className={`inline-flex p-3.5 rounded-xl bg-gradient-to-br ${gradient} ${shadowColor} shadow-lg mb-6`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>

                {/* Content */}
                <h3 className="text-xl font-display font-bold text-foreground mb-3">{title}</h3>
                <p className="text-muted-foreground leading-relaxed">{description}</p>

                {/* Hover accent */}
                <div className={`absolute inset-0 rounded-2xl bg-gradient-to-br ${gradient} opacity-0 group-hover:opacity-[0.02] transition-opacity duration-300`} />
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ============================================
          AFFILIATE NETWORKS - Partner Showcase
          ============================================ */}
      <AffiliateNetworks />

      {/* ============================================
          CTA SECTION - Final Push
          ============================================ */}
      <section className="relative py-20 lg:py-28 overflow-hidden">
        {/* Background */}
        <div className="absolute inset-0 bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900" />

        {/* Decorative elements */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute top-0 right-1/4 w-[500px] h-[500px] bg-indigo-500/20 rounded-full blur-[100px]" />
          <div className="absolute bottom-0 left-1/4 w-[400px] h-[400px] bg-violet-500/15 rounded-full blur-[80px]" />
          <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)] bg-[size:60px_60px]" />
        </div>

        <div className="container relative mx-auto px-4 text-center">
          <div className="max-w-2xl mx-auto">
            {/* Icon */}
            <div className="inline-flex p-4 rounded-2xl bg-gradient-to-br from-amber-500/20 to-orange-500/20 border border-amber-500/20 mb-8">
              <Sparkles className="w-10 h-10 text-amber-400" />
            </div>

            {/* Headline */}
            <h2 className="text-3xl md:text-4xl lg:text-5xl font-display font-bold text-white mb-6">
              Ready to Save?
            </h2>

            {/* Description */}
            <p className="text-slate-300 text-lg md:text-xl mb-10 leading-relaxed">
              Join thousands of smart shoppers saving money every day with verified coupons and exclusive deals.
            </p>

            {/* Buttons */}
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button
                size="lg"
                className="bg-white text-slate-900 hover:bg-slate-100 font-bold px-8 h-14 text-lg rounded-xl shadow-2xl shadow-black/20 hover:shadow-white/10 transition-all"
                asChild
              >
                <Link href={`/${locale}/deals`}>
                  Browse All Deals
                  <ChevronRight className="w-5 h-5 ml-1" />
                </Link>
              </Button>
              <Button
                size="lg"
                className="bg-white/10 border-2 border-white/30 text-white hover:bg-white/20 hover:border-white/50 font-bold px-8 h-14 text-lg rounded-xl backdrop-blur-sm"
                asChild
              >
                <Link href={`/${locale}/coupons`}>
                  Get Coupon Codes
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}
