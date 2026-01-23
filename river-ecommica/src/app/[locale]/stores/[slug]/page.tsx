import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import Image from 'next/image';
import { fetchStores, fetchStoreBySlug, fetchDeals } from '@/lib/api';
import DealCard from '@/components/deal/DealCard';
import { Badge } from '@/components/ui/badge';
import { JsonLd, generateStoreJsonLd, generateBreadcrumbJsonLd } from '@/components/seo/JsonLd';
import { Breadcrumbs } from '@/components/layout/Breadcrumbs';

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
  const { slug } = await params;
  const store = await fetchStoreBySlug(slug);
  
  if (!store) {
    return { title: 'Store Not Found' };
  }
  
  return {
    title: `${store.name} Deals & Coupons`,
    description: store.description || `Find the best deals and coupons for ${store.name}`,
  };
}

export default async function StoreDetailPage({ params }: Props) {
  const { locale, slug } = await params;
  const store = await fetchStoreBySlug(slug);

  if (!store) {
    notFound();
  }

  const { list: deals } = await fetchDeals({ merchantId: store.id });

  const breadcrumbs = [
    { name: 'Home', url: `/${locale}` },
    { name: 'Stores', url: `/${locale}/stores` },
    { name: store.name, url: `/${locale}/stores/${store.slug}` }
  ];

  const breadcrumbItems = [
    { label: 'Home', href: `/${locale}` },
    { label: 'Stores', href: `/${locale}/stores` },
    { label: store.name, href: `/${locale}/stores/${store.slug}` }
  ];

  return (
    <>
      <JsonLd data={generateStoreJsonLd(store)} />
      <JsonLd data={generateBreadcrumbJsonLd(breadcrumbs)} />
      <Breadcrumbs items={breadcrumbItems} />
      <main className="container mx-auto px-4 py-8">
      <section className="mb-8 flex items-start gap-6">
        <div className="w-24 h-24 bg-gray-100 rounded-lg flex items-center justify-center relative">
          {store.logoUrl ? (
            <Image src={store.logoUrl} alt={store.name} fill className="object-contain p-2" />
          ) : (
            <span className="text-2xl font-bold text-gray-400">{store.name.charAt(0)}</span>
          )}
        </div>
        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2">{store.name}</h1>
          <p className="text-gray-600 mb-3">{store.description}</p>
          <div className="flex gap-2">
            <Badge variant="secondary">{store.dealCount} deals</Badge>
            <Badge variant="secondary">{store.couponCount} coupons</Badge>
            {store.rating && <Badge variant="outline">★ {store.rating}</Badge>}
          </div>
        </div>
      </section>

      <section>
        <h2 className="text-2xl font-bold mb-4">Deals from {store.name}</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {deals.length > 0 ? deals.map(deal => (
            <DealCard key={deal.id} deal={deal} />
          )) : (
            <p className="col-span-4 text-center text-gray-500">No deals available</p>
          )}
        </div>
      </section>
    </main>
    </>
  );
}
