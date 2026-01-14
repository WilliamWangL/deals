import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { fetchStores } from '@/lib/api';
import StoreCard from '@/components/store/StoreCard';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'stores' });
  
  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function StoresPage() {
  const stores = await fetchStores();

  return (
    <main className="container mx-auto px-4 py-8">
      <section className="mb-8">
        <h1 className="text-3xl font-bold mb-2">All Stores</h1>
        <p className="text-gray-600">Browse our collection of stores and find the best deals</p>
      </section>

      <section>
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
          {stores.length > 0 ? stores.map(store => (
            <StoreCard key={store.id} store={store} />
          )) : (
            <p className="col-span-6 text-center text-gray-500">No stores found</p>
          )}
        </div>
      </section>
    </main>
  );
}
