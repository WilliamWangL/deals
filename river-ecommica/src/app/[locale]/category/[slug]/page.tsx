import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import Link from 'next/link';
import { fetchDeals, fetchCoupons, fetchCategories } from '@/lib/api';
import { Category } from '@/types';
import DealCard from '@/components/deal/DealCard';
import CouponCard from '@/components/coupon/CouponCard';
import { EmptyState } from '@/components/ui/empty-state';
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
  ArrowRight,
  ChevronRight,
  type LucideIcon
} from 'lucide-react';

const iconMap: Record<string, LucideIcon> = {
  Laptop,
  Shirt,
  Home,
  Sparkles,
  Dumbbell,
  Baby,
  ShoppingBasket,
  Heart,
};

// Helper function to find category by slug in category tree
function findCategoryBySlug(categories: Category[], slug: string): Category | null {
  for (const category of categories) {
    if (category.slug === slug) return category;
    if (category.children) {
      const found = findCategoryBySlug(category.children, slug);
      if (found) return found;
    }
  }
  return null;
}

// Helper function to find parent category
function findParentCategory(categories: Category[], slug: string): Category | null {
  for (const category of categories) {
    if (category.slug === slug) return category;
    if (category.children?.some((child: Category) => child.slug === slug)) return category;
  }
  return null;
}

// Helper function to collect all slugs from category tree
function collectCategorySlugs(categories: Category[]): { slug: string }[] {
  const params: { slug: string }[] = [];
  for (const category of categories) {
    params.push({ slug: category.slug });
    if (category.children) {
      for (const child of category.children) {
        params.push({ slug: child.slug });
      }
    }
  }
  return params;
}

interface CategoryPageProps {
  params: Promise<{ locale: string; slug: string }>;
}

export async function generateStaticParams() {
  const categories = await fetchCategories();
  return collectCategorySlugs(categories);
}

export async function generateMetadata({ params }: CategoryPageProps): Promise<Metadata> {
  const { slug } = await params;
  const categories = await fetchCategories();
  const category = findCategoryBySlug(categories, slug);

  if (!category) {
    return { title: 'Category Not Found' };
  }

  return {
    title: `${category.name} Deals & Coupons | Ecommica`,
    description: `Find the best ${category.name.toLowerCase()} deals, discounts and coupon codes. Save money on your favorite ${category.name.toLowerCase()} products.`,
  };
}

export default async function CategoryPage({ params }: CategoryPageProps) {
  const { locale, slug } = await params;

  const [categories, allDealsResult, allCouponsResult] = await Promise.all([
    fetchCategories(),
    fetchDeals(),
    fetchCoupons(),
  ]);

  const category = findCategoryBySlug(categories, slug);

  if (!category) {
    notFound();
  }

  const deals = allDealsResult.list.slice(0, 8);
  const coupons = allCouponsResult.list.slice(0, 6);

  const IconComponent = iconMap[category.icon || 'Tag'] || Tag;

  const parentCategory = findParentCategory(categories, slug);
  const isSubcategory = parentCategory?.slug !== slug;
  const subcategories = isSubcategory ? [] : (parentCategory?.children || []);

  return (
    <main className="min-h-screen bg-background">
      {/* Hero Header */}
      <section className="page-header py-12 md:py-16">
        {/* Background decoration */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-20 -right-20 w-96 h-96 bg-gradient-to-br from-cyan-200/30 to-blue-200/30 rounded-full blur-3xl" />
          <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-gradient-to-br from-indigo-200/20 to-violet-200/20 rounded-full blur-3xl" />
        </div>

        <div className="container mx-auto px-4 relative">
          {/* Breadcrumb */}
          <nav className="flex items-center gap-2 text-sm text-muted-foreground mb-6">
            <Link href={`/${locale}`} className="hover:text-primary transition-colors">Home</Link>
            <ChevronRight className="w-4 h-4" />
            {isSubcategory && parentCategory && (
              <>
                <Link href={`/${locale}/category/${parentCategory.slug}`} className="hover:text-primary transition-colors">
                  {parentCategory.name}
                </Link>
                <ChevronRight className="w-4 h-4" />
              </>
            )}
            <span className="text-foreground font-medium">{category.name}</span>
          </nav>

          <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-8">
            {/* Title Section */}
            <div className="max-w-2xl">
              <div className="flex items-center gap-3 mb-5">
                <div className="p-3 rounded-2xl bg-gradient-to-br from-cyan-100 to-blue-100">
                  <IconComponent className="w-8 h-8 text-cyan-600" />
                </div>
                <div className="badge-exclusive">
                  <Sparkles className="w-3.5 h-3.5" />
                  Best Deals
                </div>
              </div>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-display font-bold text-foreground tracking-tight mb-4">
                {category.name}
              </h1>
              <p className="text-lg md:text-xl text-muted-foreground leading-relaxed">
                Find the best {category.name.toLowerCase()} deals, discounts and coupon codes.
                Save money on your favorite products.
              </p>
            </div>

            {/* Stats Cards */}
            <div className="flex gap-4 md:gap-6 flex-wrap lg:flex-nowrap">
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Tag className="w-4 h-4" />
                  <span>Deals</span>
                </div>
                <span className="stat-value">{deals.length}</span>
              </div>
              <div className="stat-card min-w-[120px]">
                <div className="flex items-center gap-2 text-muted-foreground text-sm font-medium mb-1">
                  <Ticket className="w-4 h-4" />
                  <span>Coupons</span>
                </div>
                <span className="stat-value text-gradient-savings">{coupons.length}</span>
              </div>
            </div>
          </div>

          {/* Subcategories */}
          {subcategories.length > 0 && (
            <div className="flex flex-wrap gap-2 mt-8">
              {subcategories.map(sub => (
                <Link
                  key={sub.id}
                  href={`/${locale}/category/${sub.slug}`}
                  className="group px-4 py-2 bg-card/80 backdrop-blur-sm hover:bg-primary/10 border border-border/60 hover:border-primary/30 rounded-full text-sm font-medium text-muted-foreground hover:text-primary transition-all duration-200"
                >
                  {sub.name}
                </Link>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Deals Section */}
      <section className="container mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
            <div className="p-3 rounded-2xl bg-gradient-to-br from-amber-100 to-orange-100">
              <Tag className="w-6 h-6 text-amber-600" />
            </div>
            <div>
              <h2 className="text-2xl lg:text-3xl font-display font-bold text-foreground">
                Top Deals
              </h2>
              <p className="text-muted-foreground text-sm mt-0.5">
                {deals.length} deals available
              </p>
            </div>
          </div>
          <Link
            href={`/${locale}/deals`}
            className="group hidden md:flex items-center gap-2 px-5 py-2.5 rounded-full bg-primary/5 text-primary font-semibold hover:bg-primary/10 transition-colors"
          >
            View all
            <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
          </Link>
        </div>

        {deals.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {deals.map(deal => (
              <DealCard key={deal.id} deal={deal} />
            ))}
          </div>
        ) : (
          <EmptyState
            icon="bag"
            title="No deals yet"
            description={`We don't have any ${category.name.toLowerCase()} deals at the moment. Check back soon!`}
          />
        )}
      </section>

      {/* Coupons Section */}
      <section className="container mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
            <div className="p-3 rounded-2xl bg-gradient-to-br from-emerald-100 to-teal-100">
              <Ticket className="w-6 h-6 text-emerald-600" />
            </div>
            <div>
              <h2 className="text-2xl lg:text-3xl font-display font-bold text-foreground">
                Coupon Codes
              </h2>
              <p className="text-muted-foreground text-sm mt-0.5">
                {coupons.length} coupons available
              </p>
            </div>
          </div>
          <Link
            href={`/${locale}/coupons`}
            className="group hidden md:flex items-center gap-2 px-5 py-2.5 rounded-full bg-primary/5 text-primary font-semibold hover:bg-primary/10 transition-colors"
          >
            View all
            <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
          </Link>
        </div>

        {coupons.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {coupons.map(coupon => (
              <CouponCard key={coupon.id} coupon={coupon} />
            ))}
          </div>
        ) : (
          <EmptyState
            icon="ticket"
            title="No coupons yet"
            description={`We don't have any ${category.name.toLowerCase()} coupons at the moment. Check back soon!`}
          />
        )}
      </section>
    </main>
  );
}
