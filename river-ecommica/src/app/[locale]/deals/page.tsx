import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { fetchDeals } from '@/lib/api';
import DealCard from '@/components/deal/DealCard';

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

  return (
    <main className="container mx-auto px-4 py-8">
      <section className="mb-8">
        <h1 className="text-3xl font-bold mb-2">All Deals</h1>
        <p className="text-gray-600">Find the best deals and discounts from top brands</p>
      </section>

      <section>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {deals.length > 0 ? deals.map(deal => (
            <DealCard key={deal.id} deal={deal} />
          )) : (
            <p className="col-span-4 text-center text-gray-500">No deals found</p>
          )}
        </div>
      </section>
    </main>
  );
}
