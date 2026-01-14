import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { fetchCoupons } from '@/lib/api';
import CouponCard from '@/components/coupon/CouponCard';
import CouponsToolbar from '@/components/coupon/CouponsToolbar';
import { Badge } from '@/components/ui/badge';
import { 
  Ticket, 
  CheckCircle2, 
  Clock,
  Sparkles
} from 'lucide-react';

export async function generateMetadata({ params }: { params: Promise<{ locale: string }> }): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'coupons' });
  
  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function CouponsPage(props: { 
  params: Promise<{ locale: string }>;
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}) {
  const { locale } = await props.params;
  const searchParams = await props.searchParams;
  
  const t = await getTranslations({ locale, namespace: 'coupons' });
  const tCommon = await getTranslations({ locale, namespace: 'Common' });
  
  const q = typeof searchParams.q === 'string' ? searchParams.q : '';
  const verifiedOnly = searchParams.verified === 'true';

  const allCoupons = await fetchCoupons({ 
    verified: verifiedOnly ? true : undefined 
  });

  const globalCoupons = verifiedOnly ? await fetchCoupons() : allCoupons;

  const totalCoupons = globalCoupons.length;
  const verifiedCount = globalCoupons.filter(c => c.verified).length;
  
  let displayCoupons = allCoupons;
  if (q) {
    const query = q.toLowerCase();
    displayCoupons = displayCoupons.filter(c => 
      c.code.toLowerCase().includes(query) ||
      c.merchantName?.toLowerCase().includes(query) ||
      c.description?.toLowerCase().includes(query)
    );
  }

  const now = new Date();
  const threeDaysFromNow = new Date();
  threeDaysFromNow.setDate(now.getDate() + 3);
  
  const expiringCount = globalCoupons.filter(c => {
    if (!c.endTime) return false;
    const end = new Date(c.endTime);
    return end > now && end <= threeDaysFromNow;
  }).length;

  return (
    <main className="min-h-screen bg-slate-50/50 pb-12">
      <div className="bg-white border-b border-slate-200 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 right-0 p-12 opacity-5 pointer-events-none">
           <Ticket className="w-64 h-64 text-primary transform -rotate-12 translate-x-20 -translate-y-20" />
        </div>

        <div className="container mx-auto px-4 py-10 md:py-14 relative z-10">
          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
            <div className="max-w-2xl">
              <div className="flex items-center gap-3 mb-4">
                <div className="p-3 bg-primary/10 rounded-2xl">
                  <Ticket className="w-8 h-8 text-primary" />
                </div>
                <Badge variant="secondary" className="px-3 py-1 bg-green-50 text-green-700 border-green-100 gap-1.5">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  {tCommon('stores')} & {tCommon('categories')}
                </Badge>
              </div>
              <h1 className="text-4xl md:text-5xl font-extrabold text-slate-900 tracking-tight mb-4">
                {t('title')}
              </h1>
              <p className="text-lg text-slate-600 leading-relaxed">
                {t('subtitle')}. {t('meta.description')}
              </p>
            </div>

            <div className="flex gap-4 md:gap-8 flex-wrap">
              <div className="flex flex-col">
                <div className="flex items-center gap-2 text-slate-500 text-sm font-medium mb-1">
                  <Ticket className="w-4 h-4" /> Total
                </div>
                <span className="text-2xl font-bold text-slate-900">{totalCoupons.toLocaleString()}</span>
              </div>
              <div className="w-px h-12 bg-slate-200 hidden md:block" />
              <div className="flex flex-col">
                <div className="flex items-center gap-2 text-slate-500 text-sm font-medium mb-1">
                  <CheckCircle2 className="w-4 h-4 text-green-600" /> Verified
                </div>
                <span className="text-2xl font-bold text-slate-900">{verifiedCount.toLocaleString()}</span>
              </div>
              <div className="w-px h-12 bg-slate-200 hidden md:block" />
              <div className="flex flex-col">
                <div className="flex items-center gap-2 text-slate-500 text-sm font-medium mb-1">
                  <Clock className="w-4 h-4 text-orange-500" /> Expiring
                </div>
                <span className="text-2xl font-bold text-slate-900">{expiringCount.toLocaleString()}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <CouponsToolbar />

      <div className="container mx-auto px-4 py-8">
        {displayCoupons.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {displayCoupons.map(coupon => (
              <CouponCard key={coupon.id} coupon={coupon} />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-24 text-center">
            <div className="w-24 h-24 bg-slate-100 rounded-full flex items-center justify-center mb-6">
              <Sparkles className="w-12 h-12 text-slate-300" />
            </div>
            <h3 className="text-xl font-bold text-slate-900 mb-2">No coupons found</h3>
            <p className="text-slate-500 max-w-md mx-auto">
              We couldn&apos;t find any coupons matching your criteria. Try adjusting your search or filters.
            </p>
          </div>
        )}
      </div>
    </main>
  );
}
