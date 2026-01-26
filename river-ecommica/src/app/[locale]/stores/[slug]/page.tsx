import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import Image from 'next/image';
import { getTranslations } from 'next-intl/server';
import { fetchStores, fetchStoreBySlug, fetchDeals } from '@/lib/api';
import DealCard from '@/components/deal/DealCard';
import { Badge } from '@/components/ui/badge';
import { JsonLd, BASE_URL, generateStoreJsonLd, generateBreadcrumbJsonLd } from '@/components/seo/JsonLd';
import { Breadcrumbs } from '@/components/layout/Breadcrumbs';
import { Star, ShoppingBag, Ticket, ShieldCheck, Store as StoreIcon } from 'lucide-react';

type Props = {
  params: Promise<{ locale: string; slug: string }>;
};

export async function generateStaticParams() {
  const { list: stores } = await fetchStores();
  const locales = ['en', 'zh'];

  return locales.flatMap(locale =>
    stores
      .filter(store => store.slug && typeof store.slug === 'string')
      .map(store => ({
        locale,
        slug: store.slug,
      }))
  );
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { locale, slug } = await params;
  const t = await getTranslations({ locale, namespace: 'StoreDetail' });
  const store = await fetchStoreBySlug(slug);

  if (!store) {
    return { title: t('meta.notFound') };
  }

  return {
    title: t('meta.title', { name: store.name }),
    description: store.description || t('meta.description', { name: store.name }),
    openGraph: {
      title: t('meta.title', { name: store.name }),
      description: store.description || t('meta.description', { name: store.name }),
      url: `${BASE_URL}/${locale}/stores/${store.slug}`,
      type: 'website',
    }
  };
}

export default async function StoreDetailPage({ params }: Props) {
  const { locale, slug } = await params;
  const t = await getTranslations({ locale, namespace: 'StoreDetail' });
  const store = await fetchStoreBySlug(slug);

  if (!store) {
    notFound();
  }

  const { list: deals } = await fetchDeals({ merchantId: store.id });

  const breadcrumbs = [
    { label: t('breadcrumbHome'), href: `/${locale}` },
    { label: t('breadcrumbStores'), href: `/${locale}/stores` },
    { label: store.name, href: `/${locale}/stores/${store.slug}` }
  ];

  const breadcrumbJsonLdItems = breadcrumbs.map(item => ({
    name: item.label,
    url: `${BASE_URL}${item.href}`
  }));

  return (
    <>
      <JsonLd data={generateStoreJsonLd(store)} />
      <JsonLd data={generateBreadcrumbJsonLd(breadcrumbJsonLdItems)} />
      <Breadcrumbs items={breadcrumbs} />
      
      <main className="min-h-screen pb-12">
        <div className="page-header py-12 mb-12">
          <div className="container mx-auto px-4">
            <div className="flex flex-col md:flex-row items-center md:items-start gap-8 max-w-5xl mx-auto">
              
              <div className="card-elevated p-4 bg-white shrink-0 relative group">
                <div className="w-32 h-32 relative flex items-center justify-center">
                  {store.logoUrl ? (
                    <Image 
                      src={store.logoUrl} 
                      alt={store.name} 
                      fill 
                      className="object-contain p-2 transition-transform duration-300 group-hover:scale-105" 
                    />
                  ) : (
                    <StoreIcon size={48} className="text-gray-300" />
                  )}
                </div>
                <div className="absolute -bottom-3 left-1/2 -translate-x-1/2 bg-green-100 text-green-700 text-xs font-bold px-3 py-1 rounded-full flex items-center gap-1 shadow-sm whitespace-nowrap">
                  <ShieldCheck size={12} /> {t('verified')}
                </div>
              </div>

              <div className="flex-1 text-center md:text-left space-y-4">
                <div>
                  <h1 className="text-4xl font-bold font-display text-foreground mb-2">{store.name}</h1>
                  <p className="text-lg text-muted-foreground max-w-2xl">{store.description}</p>
                </div>

                <div className="flex flex-wrap justify-center md:justify-start gap-4 mt-6">
                  <div className="stat-card min-w-[120px]">
                    <div className="flex items-center gap-2 text-primary mb-1">
                      <ShoppingBag size={16} />
                      <span className="stat-label">{t('statDeals')}</span>
                    </div>
                    <span className="stat-value">{store.dealCount}</span>
                  </div>

                  <div className="stat-card min-w-[120px]">
                    <div className="flex items-center gap-2 text-orange-500 mb-1">
                      <Ticket size={16} />
                      <span className="stat-label">{t('statCoupons')}</span>
                    </div>
                    <span className="stat-value">{store.couponCount}</span>
                  </div>

                  {store.rating && (
                    <div className="stat-card min-w-[120px]">
                      <div className="flex items-center gap-2 text-yellow-500 mb-1">
                        <Star size={16} />
                        <span className="stat-label">{t('statRating')}</span>
                      </div>
                      <span className="stat-value">{store.rating}</span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="container mx-auto px-4 max-w-7xl">
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-2xl font-bold font-display flex items-center gap-2">
              <span className="w-1 h-8 bg-primary rounded-full block"></span>
              {t('sectionTitle')}
            </h2>
            <Badge variant="outline" className="text-sm px-3 py-1">
              {t('offersAvailable', { count: deals.length })}
            </Badge>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {deals.length > 0 ? deals.map(deal => (
              <DealCard key={deal.id} deal={deal} />
            )) : (
              <div className="col-span-full py-12 text-center bg-gray-50 rounded-2xl border border-dashed border-gray-200">
                <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <ShoppingBag size={24} className="text-gray-400" />
                </div>
                <h3 className="text-lg font-medium text-gray-900">{t('emptyTitle')}</h3>
                <p className="text-gray-500">{t('emptyDescription', { name: store.name })}</p>
              </div>
            )}
          </div>
        </div>
      </main>
    </>
  );
}
