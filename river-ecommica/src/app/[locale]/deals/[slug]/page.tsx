import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import Link from 'next/link';
import Image from 'next/image';
import { fetchDeals, fetchDealBySlug } from '@/lib/api';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { JsonLd, generateDealJsonLd } from '@/components/seo/JsonLd';

type Props = {
  params: Promise<{ locale: string; slug: string }>;
};

export async function generateStaticParams() {
  const { list: deals } = await fetchDeals();
  const locales = ['en', 'zh'];

  return locales.flatMap(locale =>
    deals
      .filter(deal => deal.slug && typeof deal.slug === 'string')
      .map(deal => ({
        locale,
        slug: deal.slug,
      }))
  );
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { slug } = await params;
  const deal = await fetchDealBySlug(slug);
  
  if (!deal) {
    return { title: 'Deal Not Found' };
  }
  
  return {
    title: `${deal.title} - ${deal.merchant.name}`,
    description: deal.description || `Get ${deal.discountPercent}% off at ${deal.merchant.name}`,
  };
}

export default async function DealDetailPage({ params }: Props) {
  const { locale, slug } = await params;
  const deal = await fetchDealBySlug(slug);

  if (!deal) {
    notFound();
  }

  const trackingUrl = deal.gotoUrl || '#';

  return (
    <>
      <JsonLd data={generateDealJsonLd(deal)} />
      <main className="container mx-auto px-4 py-8 max-w-4xl">
      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        {deal.imageUrl && (
          <div className="h-64 bg-gray-100 relative">
            <Image src={deal.imageUrl} alt={deal.title} fill className="object-cover" />
          </div>
        )}
        
        <div className="p-6">
          <div className="flex items-center gap-2 mb-4">
            {deal.featured && <Badge>Featured</Badge>}
            {deal.discountPercent > 0 && (
              <Badge variant="destructive">{deal.discountPercent}% Off</Badge>
            )}
          </div>

          <h1 className="text-3xl font-bold mb-2">{deal.title}</h1>
          
          <Link href={`/${locale}/stores/${deal.merchant.slug}`} className="text-blue-600 hover:underline mb-4 block">
            {deal.merchant.name}
          </Link>

          <p className="text-gray-600 mb-6">{deal.description}</p>

          {(deal.originalPrice > 0 || deal.dealPrice > 0) && (
            <div className="flex items-center gap-4 mb-6">
              {deal.originalPrice > 0 && (
                <span className="text-gray-400 line-through text-lg">${deal.originalPrice}</span>
              )}
              {deal.dealPrice > 0 && (
                <span className="text-green-600 font-bold text-2xl">${deal.dealPrice}</span>
              )}
            </div>
          )}

          {deal.endTime && (
            <p className="text-sm text-gray-500 mb-6">
              Expires: {new Date(deal.endTime).toLocaleDateString()}
            </p>
          )}

          <Button asChild size="lg" className="w-full">
            <a href={trackingUrl} target="_blank" rel="noopener noreferrer">
              Get This Deal
            </a>
          </Button>
        </div>
      </div>
    </main>
    </>
  );
}
