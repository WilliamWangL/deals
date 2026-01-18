import { Deal, Store, BlogPost, Coupon } from '@/types';

const BASE_URL = process.env.NEXT_PUBLIC_SITE_URL || 'https://deals.ecommica.com';

export function generateDealJsonLd(deal: Deal) {
  return {
    '@context': 'https://schema.org',
    '@type': 'Offer',
    name: deal.title,
    description: deal.description,
    url: `${BASE_URL}/deals/${deal.slug}`,
    priceCurrency: 'USD',
    price: deal.dealPrice || 0,
    priceValidUntil: deal.endTime,
    availability: 'https://schema.org/InStock',
    seller: {
      '@type': 'Organization',
      name: deal.merchant.name,
    },
    ...(deal.discountPercent > 0 && {
      discount: `${deal.discountPercent}%`,
    }),
  };
}

export function generateStoreJsonLd(store: Store) {
  return {
    '@context': 'https://schema.org',
    '@type': 'Organization',
    name: store.name,
    description: store.description,
    url: `${BASE_URL}/stores/${store.slug}`,
    logo: store.logoUrl || undefined,
    ...(store.rating && {
      aggregateRating: {
        '@type': 'AggregateRating',
        ratingValue: store.rating,
        bestRating: 5,
      },
    }),
  };
}

export function generateBlogPostJsonLd(post: BlogPost) {
  return {
    '@context': 'https://schema.org',
    '@type': 'Article',
    headline: post.title,
    description: post.excerpt,
    url: `${BASE_URL}/blog/${post.slug}`,
    image: post.coverImage || undefined,
    datePublished: post.publishedAt,
    author: {
      '@type': 'Person',
      name: post.authorName,
    },
    publisher: {
      '@type': 'Organization',
      name: 'Ecommica Deals',
      url: BASE_URL,
    },
  };
}

export function generateCouponJsonLd(coupon: Coupon) {
  const discountText = coupon.discountType === 1 
    ? `${coupon.discountValue}% off`
    : coupon.discountType === 2 
    ? `$${coupon.discountValue} off`
    : 'Free shipping';

  return {
    '@context': 'https://schema.org',
    '@type': 'Offer',
    name: `${coupon.merchant.name} - ${discountText}`,
    description: coupon.description,
    priceCurrency: 'USD',
    price: 0,
    priceValidUntil: coupon.endTime,
    availability: 'https://schema.org/InStock',
    seller: {
      '@type': 'Organization',
      name: coupon.merchant.name,
    },
    ...(coupon.code && {
      'schema:discountCode': coupon.code,
    }),
  };
}

export function generateBreadcrumbJsonLd(items: { name: string; url: string }[]) {
  return {
    '@context': 'https://schema.org',
    '@type': 'BreadcrumbList',
    itemListElement: items.map((item, index) => ({
      '@type': 'ListItem',
      position: index + 1,
      name: item.name,
      item: item.url,
    })),
  };
}

interface JsonLdProps {
  data: object;
}

export function JsonLd({ data }: JsonLdProps) {
  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(data) }}
    />
  );
}
