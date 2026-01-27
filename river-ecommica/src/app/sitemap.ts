import { MetadataRoute } from 'next';
import { fetchStores, fetchDeals, fetchPosts, fetchCategories } from '@/lib/api';
import { Category } from '@/types';

// 强制动态生成，确保每次访问都获取最新数据
export const dynamic = 'force-dynamic';

const BASE_URL = process.env.NEXT_PUBLIC_SITE_URL || 'https://deals.ecommica.com';
const LOCALES = ['en', 'zh'];
const DEFAULT_LOCALE = 'en';

// Helper function to build category pages from category tree
function buildCategoryPages(categories: Category[], locale: string): MetadataRoute.Sitemap {
  const pages: MetadataRoute.Sitemap = [];
  for (const category of categories) {
    pages.push({
      url: `${BASE_URL}/${locale}/category/${category.slug}`,
      lastModified: new Date(),
      changeFrequency: 'weekly' as const,
      priority: 0.7,
      alternates: {
        languages: Object.fromEntries(
          LOCALES.map(l => [l, `${BASE_URL}/${l}/category/${category.slug}`])
        ),
      },
    });
    if (category.children) {
      for (const child of category.children) {
        pages.push({
          url: `${BASE_URL}/${locale}/category/${child.slug}`,
          lastModified: new Date(),
          changeFrequency: 'weekly' as const,
          priority: 0.6,
          alternates: {
            languages: Object.fromEntries(
              LOCALES.map(l => [l, `${BASE_URL}/${l}/category/${child.slug}`])
            ),
          },
        });
      }
    }
  }
  return pages;
}

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  // 静态页面（为默认语言生成，包含 alternates）
  const staticPages: MetadataRoute.Sitemap = [
    {
      url: `${BASE_URL}/${DEFAULT_LOCALE}`,
      lastModified: new Date(),
      changeFrequency: 'daily' as const,
      priority: 1,
      alternates: {
        languages: Object.fromEntries(LOCALES.map(l => [l, `${BASE_URL}/${l}`])),
      },
    },
    {
      url: `${BASE_URL}/${DEFAULT_LOCALE}/stores`,
      lastModified: new Date(),
      changeFrequency: 'daily' as const,
      priority: 0.9,
      alternates: {
        languages: Object.fromEntries(LOCALES.map(l => [l, `${BASE_URL}/${l}/stores`])),
      },
    },
    {
      url: `${BASE_URL}/${DEFAULT_LOCALE}/deals`,
      lastModified: new Date(),
      changeFrequency: 'hourly' as const,
      priority: 0.9,
      alternates: {
        languages: Object.fromEntries(LOCALES.map(l => [l, `${BASE_URL}/${l}/deals`])),
      },
    },
    {
      url: `${BASE_URL}/${DEFAULT_LOCALE}/coupons`,
      lastModified: new Date(),
      changeFrequency: 'hourly' as const,
      priority: 0.9,
      alternates: {
        languages: Object.fromEntries(LOCALES.map(l => [l, `${BASE_URL}/${l}/coupons`])),
      },
    },
    {
      url: `${BASE_URL}/${DEFAULT_LOCALE}/blog`,
      lastModified: new Date(),
      changeFrequency: 'daily' as const,
      priority: 0.8,
      alternates: {
        languages: Object.fromEntries(LOCALES.map(l => [l, `${BASE_URL}/${l}/blog`])),
      },
    },
    {
      url: `${BASE_URL}/${DEFAULT_LOCALE}/categories`,
      lastModified: new Date(),
      changeFrequency: 'weekly' as const,
      priority: 0.8,
      alternates: {
        languages: Object.fromEntries(LOCALES.map(l => [l, `${BASE_URL}/${l}/categories`])),
      },
    },
  ];

  try {
    const [storesResult, dealsResult, postsResult, categories] = await Promise.all([
      fetchStores(),
      fetchDeals(),
      fetchPosts(),
      fetchCategories(),
    ]);

    const stores = storesResult.list;
    const deals = dealsResult.list;
    const posts = postsResult.list;

    const categoryPages = buildCategoryPages(categories, DEFAULT_LOCALE);

    const storePages: MetadataRoute.Sitemap = stores.map(store => ({
      url: `${BASE_URL}/${DEFAULT_LOCALE}/stores/${store.slug}`,
      lastModified: new Date(),
      changeFrequency: 'weekly' as const,
      priority: 0.7,
      alternates: {
        languages: Object.fromEntries(
          LOCALES.map(l => [l, `${BASE_URL}/${l}/stores/${store.slug}`])
        ),
      },
    }));

    const dealPages: MetadataRoute.Sitemap = deals.map(deal => ({
      url: `${BASE_URL}/${DEFAULT_LOCALE}/deals/${deal.slug}`,
      lastModified: new Date(),
      changeFrequency: 'daily' as const,
      priority: 0.8,
      alternates: {
        languages: Object.fromEntries(
          LOCALES.map(l => [l, `${BASE_URL}/${l}/deals/${deal.slug}`])
        ),
      },
    }));

    const blogPages: MetadataRoute.Sitemap = posts.map(post => ({
      url: `${BASE_URL}/${DEFAULT_LOCALE}/blog/${post.slug}`,
      lastModified: new Date(post.publishedAt),
      changeFrequency: 'weekly' as const,
      priority: 0.6,
      alternates: {
        languages: Object.fromEntries(
          LOCALES.map(l => [l, `${BASE_URL}/${l}/blog/${post.slug}`])
        ),
      },
    }));

    return [...staticPages, ...categoryPages, ...storePages, ...dealPages, ...blogPages];
  } catch {
    // 构建时 API 不可用，只返回静态页面，动态页面将在运行时生成
    return staticPages;
  }
}
