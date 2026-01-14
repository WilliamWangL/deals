import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import { getTranslations } from 'next-intl/server';
import { fetchDeals, fetchCoupons } from '@/lib/api';
import { getCategoryBySlug, mockCategories } from '@/lib/mock/categories';
import DealCard from '@/components/deal/DealCard';
import CouponCard from '@/components/coupon/CouponCard';
import { Badge } from '@/components/ui/badge';
import { 
  Laptop,
  Shirt,
  Home,
  Sparkles,
  Dumbbell,
  Baby,
  ShoppingBasket,
  Heart,
  Tag,
  Ticket,
  ArrowRight
} from 'lucide-react';
import Link from 'next/link';

const iconMap: Record<string, React.ComponentType<{ className?: string }>> = {
  Laptop,
  Shirt,
  Home,
  Sparkles,
  Dumbbell,
  Baby,
  ShoppingBasket,
  Heart,
};

interface CategoryPageProps {
  params: Promise<{ locale: string; slug: string }>;
}

export async function generateStaticParams() {
  const params: { slug: string }[] = [];
  for (const category of mockCategories) {
    params.push({ slug: category.slug });
    if (category.children) {
      for (const child of category.children) {
        params.push({ slug: child.slug });
      }
    }
  }
  return params;
}

export async function generateMetadata({ params }: CategoryPageProps): Promise<Metadata> {
  const { locale, slug } = await params;
  const category = getCategoryBySlug(slug);
  
  if (!category) {
    return { title: 'Category Not Found' };
  }

  const t = await getTranslations({ locale, namespace: 'common' });
  
  return {
    title: `${category.name} Deals & Coupons | Ecommica`,
    description: `Find the best ${category.name.toLowerCase()} deals, discounts and coupon codes. Save money on your favorite ${category.name.toLowerCase()} products.`,
  };
}

export default async function CategoryPage({ params }: CategoryPageProps) {
  const { locale, slug } = await params;
  const category = getCategoryBySlug(slug);

  if (!category) {
    notFound();
  }

  const [allDeals, allCoupons] = await Promise.all([
    fetchDeals(),
    fetchCoupons(),
  ]);

  const deals = allDeals.slice(0, 8);
  const coupons = allCoupons.slice(0, 6);

  const IconComponent = iconMap[category.icon || 'Tag'] || Tag;

  const parentCategory = mockCategories.find(c => 
    c.slug === slug || c.children?.some(child => child.slug === slug)
  );
  const isSubcategory = parentCategory?.slug !== slug;
  const subcategories = isSubcategory ? [] : (parentCategory?.children || []);

  return (
    <main className="min-h-screen bg-slate-50/50 pb-12">
      <div className="bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 text-white relative overflow-hidden">
        <div className="absolute inset-0 opacity-30">
          <div className="absolute top-20 left-10 w-72 h-72 bg-cyan-500/20 rounded-full blur-[100px]" />
          <div className="absolute bottom-10 right-20 w-96 h-96 bg-indigo-500/20 rounded-full blur-[120px]" />
        </div>
        
        <div className="container mx-auto px-4 py-12 md:py-16 relative z-10">
          <nav className="flex items-center gap-2 text-sm text-slate-400 mb-6">
            <Link href={`/${locale}`} className="hover:text-white transition-colors">Home</Link>
            <span>/</span>
            {isSubcategory && parentCategory && (
              <>
                <Link href={`/${locale}/category/${parentCategory.slug}`} className="hover:text-white transition-colors">
                  {parentCategory.name}
                </Link>
                <span>/</span>
              </>
            )}
            <span className="text-white">{category.name}</span>
          </nav>

          <div className="flex items-center gap-4 mb-6">
            <div className="p-4 bg-white/10 backdrop-blur-sm rounded-2xl">
              <IconComponent className="w-10 h-10 text-cyan-400" />
            </div>
            <div>
              <h1 className="text-3xl md:text-4xl font-bold tracking-tight">
                {category.name}
              </h1>
              <p className="text-slate-400 mt-1">
                {deals.length} deals • {coupons.length} coupons available
              </p>
            </div>
          </div>

          {subcategories.length > 0 && (
            <div className="flex flex-wrap gap-2 mt-6">
              {subcategories.map(sub => (
                <Link
                  key={sub.id}
                  href={`/${locale}/category/${sub.slug}`}
                  className="px-4 py-2 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-full text-sm font-medium transition-colors"
                >
                  {sub.name}
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="container mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-amber-50 rounded-xl">
              <Tag className="w-5 h-5 text-amber-600" />
            </div>
            <h2 className="text-2xl font-bold text-slate-900">Top Deals</h2>
            <Badge variant="secondary" className="bg-amber-50 text-amber-700">
              {deals.length} available
            </Badge>
          </div>
          <Link 
            href={`/${locale}/deals`}
            className="flex items-center gap-1 text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
          >
            View all <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {deals.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {deals.map(deal => (
              <DealCard key={deal.id} deal={deal} />
            ))}
          </div>
        ) : (
          <div className="text-center py-12 bg-white rounded-2xl border border-slate-200">
            <Tag className="w-12 h-12 text-slate-300 mx-auto mb-4" />
            <p className="text-slate-500">No deals in this category yet</p>
          </div>
        )}
      </div>

      <div className="container mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-emerald-50 rounded-xl">
              <Ticket className="w-5 h-5 text-emerald-600" />
            </div>
            <h2 className="text-2xl font-bold text-slate-900">Coupon Codes</h2>
            <Badge variant="secondary" className="bg-emerald-50 text-emerald-700">
              {coupons.length} available
            </Badge>
          </div>
          <Link 
            href={`/${locale}/coupons`}
            className="flex items-center gap-1 text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors"
          >
            View all <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {coupons.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {coupons.map(coupon => (
              <CouponCard key={coupon.id} coupon={coupon} />
            ))}
          </div>
        ) : (
          <div className="text-center py-12 bg-white rounded-2xl border border-slate-200">
            <Ticket className="w-12 h-12 text-slate-300 mx-auto mb-4" />
            <p className="text-slate-500">No coupons in this category yet</p>
          </div>
        )}
      </div>
    </main>
  );
}
