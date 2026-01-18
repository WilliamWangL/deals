import { MetadataRoute } from 'next';
import { fetchStores, fetchDeals, fetchPosts, fetchCategories } from '@/lib/api';
import { Category } from '@/types';

const BASE_URL = process.env.NEXT_PUBLIC_SITE_URL || 'https://deals.ecommica.com';

// Helper function to build category pages from category tree
function buildCategoryPages(categories: Category[]): MetadataRoute.Sitemap {
  const pages: MetadataRoute.Sitemap = [];
  for (const category of categories) {
    pages.push({
      url: `${BASE_URL}/category/${category.slug}`,
      lastModified: new Date(),
      changeFrequency: 'weekly' as const,
      priority: 0.7,
    });
    if (category.children) {
      for (const child of category.children) {
        pages.push({
          url: `${BASE_URL}/category/${child.slug}`,
          lastModified: new Date(),
          changeFrequency: 'weekly' as const,
          priority: 0.6,
        });
      }
    }
  }
  return pages;
}

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  const [stores, deals, posts, categories] = await Promise.all([
    fetchStores(),
    fetchDeals(),
    fetchPosts(),
    fetchCategories(),
  ]);

  const staticPages = [
    { url: BASE_URL, lastModified: new Date(), changeFrequency: 'daily' as const, priority: 1 },
    { url: `${BASE_URL}/stores`, lastModified: new Date(), changeFrequency: 'daily' as const, priority: 0.9 },
    { url: `${BASE_URL}/deals`, lastModified: new Date(), changeFrequency: 'hourly' as const, priority: 0.9 },
    { url: `${BASE_URL}/coupons`, lastModified: new Date(), changeFrequency: 'hourly' as const, priority: 0.9 },
    { url: `${BASE_URL}/blog`, lastModified: new Date(), changeFrequency: 'daily' as const, priority: 0.8 },
  ];

  const categoryPages = buildCategoryPages(categories);

  const storePages = stores.map(store => ({
    url: `${BASE_URL}/stores/${store.slug}`,
    lastModified: new Date(),
    changeFrequency: 'weekly' as const,
    priority: 0.7,
  }));

  const dealPages = deals.map(deal => ({
    url: `${BASE_URL}/deals/${deal.slug}`,
    lastModified: new Date(),
    changeFrequency: 'daily' as const,
    priority: 0.8,
  }));

  const blogPages = posts.map(post => ({
    url: `${BASE_URL}/blog/${post.slug}`,
    lastModified: new Date(post.publishedAt),
    changeFrequency: 'weekly' as const,
    priority: 0.6,
  }));

  return [...staticPages, ...categoryPages, ...storePages, ...dealPages, ...blogPages];
}
