import Link from 'next/link';
import { Category } from '@/types';
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
  ArrowRight,
  ChevronRight,
  type LucideIcon
} from 'lucide-react';

const IconMap: Record<string, LucideIcon> = {
  Laptop,
  Shirt,
  Home,
  Sparkles,
  Dumbbell,
  Baby,
  ShoppingBasket,
  Heart
};

interface CategorySectionProps {
  categories: Category[];
  locale: string;
  showViewAll?: boolean;
  /** 最多展示的分类数量，默认 8 */
  maxCategories?: number;
}

export function CategorySection({ categories, locale, showViewAll = false, maxCategories = 8 }: CategorySectionProps) {
  if (categories.length === 0) return null;

  // 限制展示的分类数量
  const displayCategories = categories.slice(0, maxCategories);
  const hasMore = categories.length > maxCategories;

  return (
    <section className="py-12 lg:py-16 bg-background">
      <div className="container mx-auto px-4">
        {/* Section Header */}
        <div className="flex items-end justify-between mb-10">
          <div className="space-y-3">
            <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-primary/10 text-primary text-sm font-medium">
              <Tag className="w-3.5 h-3.5" />
              Browse by Category
            </div>
            <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground">
              Shop by Category
            </h2>
          </div>
          {showViewAll && (
            <Link
              href={`/${locale}/categories`}
              className="group hidden md:flex items-center gap-2 text-primary font-semibold hover:gap-3 transition-all duration-200"
            >
              View All
              <ArrowRight className="w-4 h-4" />
            </Link>
          )}
        </div>

        {/* Category Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
          {displayCategories.map((category, index) => {
            const Icon = IconMap[category.icon] || Tag;
            const maxChildren = 5;
            const visibleChildren = category.children?.slice(0, maxChildren) || [];
            const remainingCount = (category.children?.length || 0) - maxChildren;

            return (
              <article
                key={category.id}
                className="group card-interactive p-5 animate-in fade-in slide-in-from-bottom-4"
                style={{ animationDelay: `${index * 50}ms`, animationFillMode: 'both' }}
              >
                {/* Header */}
                <Link
                  href={`/${locale}/category/${category.slug}`}
                  className="flex items-center gap-3 mb-4"
                >
                  <div className="p-2.5 rounded-xl bg-primary/10 text-primary group-hover:bg-primary group-hover:text-white transition-colors">
                    <Icon className="w-5 h-5" />
                  </div>
                  <h3 className="font-display font-bold text-lg text-foreground group-hover:text-primary transition-colors">
                    {category.name}
                  </h3>
                  <ChevronRight className="w-4 h-4 text-muted-foreground ml-auto opacity-0 group-hover:opacity-100 transition-opacity" />
                </Link>

                {/* Subcategories */}
                {visibleChildren.length > 0 && (
                  <div className="flex flex-wrap gap-2">
                    {visibleChildren.map((child) => (
                      <Link
                        key={child.id}
                        href={`/${locale}/category/${child.slug}`}
                        className="px-3 py-1.5 text-sm text-muted-foreground bg-muted/50 hover:bg-primary/10 hover:text-primary rounded-full transition-colors"
                      >
                        {child.name}
                      </Link>
                    ))}
                    {remainingCount > 0 && (
                      <Link
                        href={`/${locale}/category/${category.slug}`}
                        className="px-3 py-1.5 text-sm text-primary font-medium hover:underline"
                      >
                        +{remainingCount} more
                      </Link>
                    )}
                  </div>
                )}
              </article>
            );
          })}
        </div>

        {/* Mobile View All */}
        {showViewAll && (
          <div className="mt-10 text-center md:hidden">
            <Link
              href={`/${locale}/categories`}
              className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-white font-semibold rounded-xl hover:bg-primary/90 transition-colors"
            >
              View All Categories
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        )}
      </div>
    </section>
  );
}
