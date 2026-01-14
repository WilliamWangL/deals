import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { fetchCoupons } from '@/lib/api';
import CouponCard from '@/components/coupon/CouponCard';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'coupons' });
  
  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function CouponsPage() {
  const coupons = await fetchCoupons();

  return (
    <main className="container mx-auto px-4 py-8">
      <section className="mb-8">
        <h1 className="text-3xl font-bold mb-2">Coupon Codes</h1>
        <p className="text-gray-600">Find verified coupon codes and promo codes for your favorite stores</p>
      </section>

      <section>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {coupons.length > 0 ? coupons.map(coupon => (
            <CouponCard key={coupon.id} coupon={coupon} />
          )) : (
            <p className="col-span-3 text-center text-gray-500">No coupons found</p>
          )}
        </div>
      </section>
    </main>
  );
}
