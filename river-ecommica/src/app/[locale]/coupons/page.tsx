import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { fetchCoupons } from '@/lib/api';
import { PAGINATION } from '@/constants/pagination';
import CouponCard from '@/components/coupon/CouponCard';
import CouponsToolbar from '@/components/coupon/CouponsToolbar';
import { CouponPagination } from '@/components/coupon/CouponPagination';
import { EmptyState } from '@/components/ui/empty-state';
import {
  Ticket,
  CheckCircle2,
  Clock,
  Sparkles,
  TicketPercent
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

  const { list: allCoupons, total } = await fetchCoupons({
    pageNo: currentPage,
    pageSize,
    verified: verifiedOnly ? true : undefined
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
  const allCouponsResult = await fetchCoupons({ verified: verifiedOnly ? true : undefined });
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
      {/* Hero Header */}
      <section className="page-header py-12 md:py-16">
        {/* Background decoration */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-20 -right-20 w-96 h-96 bg-gradient-to-br from-emerald-200/30 to-teal-200/30 rounded-full blur-3xl" />
          <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-gradient-to-br from-cyan-200/20 to-blue-200/20 rounded-full blur-3xl" />
        </div>

        <div className="container mx-auto px-4 relative">
          <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-8">
            {/* Title Section */}
            <div className="max-w-2xl">
              <div className="flex items-center gap-3 mb-5">
                <div className="p-3 rounded-2xl bg-gradient-to-br from-emerald-100 to-teal-100">
                  <TicketPercent className="w-8 h-8 text-emerald-600" />
                </div>
                <div className="badge-exclusive">
                  <Sparkles className="w-3.5 h-3.5" />
                  100% Verified
                </div>
              </div>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-display font-bold text-foreground tracking-tight mb-4">
                {t('title')}
              </h1>
              <p className="text-lg md:text-xl text-muted-foreground leading-relaxed">
                {t('subtitle')}. {t('meta.description')}
              </p>
            </div>

            {/* Stats Cards */}
            <div className="flex gap-4 md:gap-6 flex-wrap lg:flex-nowrap">
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Ticket className="w-4 h-4" />
                  <span>Total</span>
                </div>
                <span className="stat-value">{totalCoupons.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                  <span>Verified</span>
                </div>
                <span className="stat-value text-gradient-savings">{verifiedCount.toLocaleString()}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Clock className="w-4 h-4 text-amber-500" />
                  <span>Expiring</span>
                </div>
                <span className="stat-value">{expiringCount.toLocaleString()}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <CouponsToolbar />

      {/* Coupons Grid */}
      <section className="container mx-auto px-4 py-10">
        {displayCoupons.length > 0 ? (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
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
