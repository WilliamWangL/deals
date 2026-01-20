import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { fetchCoupons } from '@/lib/api';
import { getCurrentRegion } from '@/lib/region';
import { PAGINATION } from '@/constants/pagination';
import CouponCard from '@/components/coupon/CouponCard';
import CouponsToolbar from '@/components/coupon/CouponsToolbar';
import { CouponPagination } from '@/components/coupon/CouponPagination';
import { EmptyState } from '@/components/ui/empty-state';
import {
  Ticket,
  BadgeCheck,
  Timer,
  Scissors,
  Copy
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

  const q = typeof searchParams.q === 'string' ? searchParams.q : '';
  const verifiedOnly = searchParams.verified === 'true';
  const currentPage = parseInt(typeof searchParams.page === 'string' ? searchParams.page : String(PAGINATION.DEFAULT_PAGE), 10);
  const pageSize = PAGINATION.PAGE_SIZE.COUPON;
  const region = await getCurrentRegion({ region: typeof searchParams.region === 'string' ? searchParams.region : undefined });

  const { list: allCoupons, total } = await fetchCoupons({
    pageNo: currentPage,
    pageSize,
    verified: verifiedOnly ? true : undefined,
    region
  });

  const displayCoupons = q
    ? allCoupons.filter(c =>
        c.code.toLowerCase().includes(q.toLowerCase()) ||
        c.merchant.name?.toLowerCase().includes(q.toLowerCase()) ||
        c.description?.toLowerCase().includes(q.toLowerCase())
      )
    : allCoupons;

  const now = new Date();
  const threeDaysFromNow = new Date();
  threeDaysFromNow.setDate(now.getDate() + 3);

  // Fetch all coupons for stats (without pagination)
  const allCouponsResult = await fetchCoupons({ verified: verifiedOnly ? true : undefined, region });
  const allCouponsForStats = allCouponsResult.list;
  const totalCoupons = total;
  const verifiedCount = allCouponsForStats.filter(c => c.verified).length;

  const expiringCount = allCouponsForStats.filter(c => {
    if (!c.endTime) return false;
    const end = new Date(c.endTime);
    return end > now && end <= threeDaysFromNow;
  }).length;

  return (
    <main className="min-h-screen bg-background">
      {/* Hero Section - Ticket/Coupon Style */}
      <section className="relative overflow-hidden">
        {/* Dashed border pattern background */}
        <div className="absolute inset-0 bg-gradient-to-br from-emerald-50 via-teal-50/50 to-cyan-50/30" />

        {/* Decorative coupon shapes */}
        <div className="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none">
          <div className="absolute -top-4 -left-4 w-24 h-24 border-4 border-dashed border-emerald-200/50 rounded-full" />
          <div className="absolute top-1/2 -right-8 w-32 h-32 border-4 border-dashed border-teal-200/40 rounded-full" />
          <div className="absolute -bottom-6 left-1/4 w-20 h-20 border-4 border-dashed border-cyan-200/40 rounded-full" />
        </div>

        <div className="container mx-auto px-4 py-10 md:py-14 relative">
          {/* Main Hero Card - Ticket Style */}
          <div className="relative max-w-4xl mx-auto">
            <div className="bg-white rounded-2xl shadow-lg border border-emerald-100 overflow-hidden">
              {/* Notch decorations */}
              <div className="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/2 w-6 h-6 bg-gradient-to-br from-emerald-50 to-teal-50 rounded-full border-r-2 border-emerald-100" />
              <div className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 w-6 h-6 bg-gradient-to-br from-emerald-50 to-teal-50 rounded-full border-l-2 border-emerald-100" />

              <div className="flex flex-col md:flex-row">
                {/* Left: Main Content */}
                <div className="flex-1 p-6 md:p-8">
                  <div className="flex items-center gap-2 mb-4">
                    <Scissors className="w-5 h-5 text-emerald-600" />
                    <span className="text-emerald-600 font-semibold text-sm">COUPON CODES</span>
                  </div>

                  <h1 className="text-3xl md:text-4xl font-bold text-foreground tracking-tight mb-3">
                    Save More with
                    <span className="text-emerald-600"> Verified Codes</span>
                  </h1>

                  <p className="text-muted-foreground text-base max-w-lg">
                    {t('subtitle')}. Copy, paste, and save instantly at checkout.
                  </p>

                  {/* Quick action hint */}
                  <div className="mt-5 inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-50 text-slate-600 text-sm">
                    <Copy className="w-4 h-4" />
                    <span>Click any code to copy</span>
                  </div>
                </div>

                {/* Right: Stats - Vertical Dashed Separator */}
                <div className="relative md:border-l-2 md:border-dashed border-emerald-100 p-6 md:p-8 bg-gradient-to-br from-emerald-50/50 to-transparent">
                  {/* Mobile top border */}
                  <div className="md:hidden absolute top-0 left-6 right-6 border-t-2 border-dashed border-emerald-100" />

                  <div className="grid grid-cols-3 md:grid-cols-1 gap-4 md:gap-5">
                    <div className="text-center md:text-left">
                      <div className="flex items-center justify-center md:justify-start gap-1.5 text-muted-foreground text-xs mb-1">
                        <Ticket className="w-3.5 h-3.5" />
                        <span>Total</span>
                      </div>
                      <span className="text-2xl md:text-3xl font-bold text-foreground">
                        {totalCoupons}
                      </span>
                    </div>

                    <div className="text-center md:text-left">
                      <div className="flex items-center justify-center md:justify-start gap-1.5 text-emerald-600 text-xs mb-1">
                        <BadgeCheck className="w-3.5 h-3.5" />
                        <span>Verified</span>
                      </div>
                      <span className="text-2xl md:text-3xl font-bold text-emerald-600">
                        {verifiedCount}
                      </span>
                    </div>

                    <div className="text-center md:text-left">
                      <div className="flex items-center justify-center md:justify-start gap-1.5 text-amber-600 text-xs mb-1">
                        <Timer className="w-3.5 h-3.5" />
                        <span>Expiring</span>
                      </div>
                      <span className="text-2xl md:text-3xl font-bold text-amber-600">
                        {expiringCount}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Toolbar */}
      <CouponsToolbar />

      {/* Coupons Grid */}
      <section className="container mx-auto px-4 py-8 md:py-12">
        {displayCoupons.length > 0 ? (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5 md:gap-6">
              {displayCoupons.map(coupon => (
                <CouponCard key={coupon.id} coupon={coupon} />
              ))}
            </div>
            <div className="mt-12">
              <CouponPagination total={total} pageSize={pageSize} currentPage={currentPage} />
            </div>
          </>
        ) : (
          <EmptyState
            icon="ticket"
            title="No coupons found"
            description="We couldn't find any coupons matching your criteria. Try adjusting your search or filters."
          />
        )}
      </section>
    </main>
  );
}
