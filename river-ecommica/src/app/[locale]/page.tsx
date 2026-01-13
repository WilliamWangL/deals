import { getTranslations } from 'next-intl/server';
import { DealCard } from '@/components/deal/DealCard';
import { StoreCard } from '@/components/store/StoreCard';
import { fetchDeals, fetchStores } from '@/lib/api';

export async function generateMetadata({params}: {params: Promise<{locale: string}>}) {
  const { locale } = await params;
  const t = await getTranslations({locale, namespace: 'Home'});
 
  return {
    title: t('title')
  };
}

export default async function HomePage() {
  const t = await getTranslations('Home');
  const deals = await fetchDeals();
  const stores = await fetchStores();
  
  return (
    <div className="container mx-auto px-4 py-8">
      <section className="mb-12 text-center">
         <h1 className="text-4xl font-bold mb-4">{t('heroTitle')}</h1>
         <p className="text-xl text-gray-600">{t('heroSubtitle')}</p>
      </section>

      <section className="mb-12">
        <h2 className="text-2xl font-bold mb-6">{t('featuredDeals')}</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
           {deals.data.length > 0 ? deals.data.map(deal => (
             <DealCard key={deal.id} deal={deal} />
           )) : (
             <p className="col-span-4 text-center text-gray-500">No deals found (Mock Data)</p>
           )}
        </div>
      </section>

      <section className="mb-12">
        <h2 className="text-2xl font-bold mb-6">{t('popularStores')}</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
           {stores.data.length > 0 ? stores.data.map(store => (
             <StoreCard key={store.id} store={store} />
           )) : (
             <p className="col-span-6 text-center text-gray-500">No stores found (Mock Data)</p>
           )}
        </div>
      </section>
    </div>
  )
}
