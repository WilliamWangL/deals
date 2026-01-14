import { Deal, Store, Coupon, BlogPost, Category } from '@/types'
import { mockDeals, mockStores, mockCoupons, mockCategories } from './mock'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:48080/app-api'
const USE_MOCK = process.env.NEXT_PUBLIC_USE_MOCK === 'true' || !process.env.NEXT_PUBLIC_API_URL

const MOCK_POSTS: BlogPost[] = [
  {
    id: 1,
    title: 'Best Tech Deals of 2024: A Complete Guide',
    slug: 'best-tech-deals-2024-complete-guide',
    excerpt: 'Discover the hottest tech deals and discounts for 2024. From laptops to smartphones, we cover everything.',
    content: '# Best Tech Deals of 2024\n\nHere are the best deals...',
    coverImage: 'https://images.unsplash.com/photo-1468495244123-6c6c332eeece?w=800&h=400&fit=crop',
    authorName: 'Sarah Chen',
    authorAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
    publishedAt: '2024-01-15',
    type: 'deal',
    viewCount: 12500,
    featured: true,
  },
  {
    id: 2,
    title: 'How to Save Money Online: 10 Pro Tips',
    slug: 'how-to-save-money-online-10-pro-tips',
    excerpt: 'Learn the best strategies for saving money when shopping online. Coupon stacking, cashback, and more.',
    content: '# How to Save Money Online\n\nTip 1: Always look for coupons...',
    coverImage: 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=800&h=400&fit=crop',
    authorName: 'Mike Johnson',
    authorAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop',
    publishedAt: '2024-01-10',
    type: 'tutorial',
    viewCount: 8900,
  },
  {
    id: 3,
    title: 'Amazon Prime Day 2024: What to Expect',
    slug: 'amazon-prime-day-2024-what-to-expect',
    excerpt: 'Get ready for Prime Day with our complete preview. Early deals, tips, and predictions.',
    content: '# Amazon Prime Day 2024\n\nPrime Day is coming...',
    coverImage: 'https://images.unsplash.com/photo-1556740758-90de374c12ad?w=800&h=400&fit=crop',
    authorName: 'Emily Davis',
    authorAvatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop',
    publishedAt: '2024-01-08',
    type: 'news',
    viewCount: 15200,
    featured: true,
  },
  {
    id: 4,
    title: 'Sony WH-1000XM5 Review: Worth the Price?',
    slug: 'sony-wh1000xm5-review-worth-the-price',
    excerpt: 'An in-depth review of Sony\'s flagship noise-cancelling headphones after 6 months of use.',
    content: '# Sony WH-1000XM5 Review\n\nAfter using these headphones...',
    coverImage: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=400&fit=crop',
    authorName: 'Alex Thompson',
    authorAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
    publishedAt: '2024-01-05',
    type: 'review',
    viewCount: 6800,
  },
]

export async function fetchDeals(params?: { merchantId?: number; featured?: boolean }): Promise<Deal[]> {
  if (USE_MOCK) {
    let result = [...mockDeals]
    if (params?.merchantId) {
      result = result.filter(d => d.merchantId === params.merchantId)
    }
    if (params?.featured !== undefined) {
      result = result.filter(d => d.featured === params.featured)
    }
    return result
  }

  const url = new URL(`${API_BASE_URL}/coupon/deal/list`)
  if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId))
  if (params?.featured !== undefined) url.searchParams.set('featured', String(params.featured))

  try {
    const res = await fetch(url.toString(), { next: { revalidate: 300 } })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || []
  } catch {
    return mockDeals
  }
}

export async function fetchDealBySlug(slug: string): Promise<Deal | null> {
  if (USE_MOCK) {
    return mockDeals.find(d => d.slug === slug) || null
  }

  try {
    const res = await fetch(`${API_BASE_URL}/coupon/deal/get-by-slug?slug=${encodeURIComponent(slug)}`, {
      next: { revalidate: 300 },
    })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || null
  } catch {
    return mockDeals.find(d => d.slug === slug) || null
  }
}

export async function fetchStores(): Promise<Store[]> {
  if (USE_MOCK) {
    return mockStores
  }

  try {
    const res = await fetch(`${API_BASE_URL}/affiliate/merchant/list`, { next: { revalidate: 3600 } })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || []
  } catch {
    return mockStores
  }
}

export async function fetchStoreBySlug(slug: string): Promise<Store | null> {
  if (USE_MOCK) {
    return mockStores.find(s => s.slug === slug) || null
  }

  try {
    const res = await fetch(`${API_BASE_URL}/affiliate/merchant/get-by-slug?slug=${encodeURIComponent(slug)}`, {
      next: { revalidate: 3600 },
    })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || null
  } catch {
    return mockStores.find(s => s.slug === slug) || null
  }
}

export async function fetchCoupons(params?: { merchantId?: number; verified?: boolean }): Promise<Coupon[]> {
  if (USE_MOCK) {
    let result = [...mockCoupons]
    if (params?.merchantId) {
      result = result.filter(c => c.merchantId === params.merchantId)
    }
    if (params?.verified !== undefined) {
      result = result.filter(c => c.verified === params.verified)
    }
    return result
  }

  const url = new URL(`${API_BASE_URL}/coupon/coupon/list`)
  if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId))
  if (params?.verified !== undefined) url.searchParams.set('verified', String(params.verified))

  try {
    const res = await fetch(url.toString(), { next: { revalidate: 300 } })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || []
  } catch {
    return mockCoupons
  }
}

export async function fetchPosts(params?: { type?: string; featured?: boolean }): Promise<BlogPost[]> {
  if (USE_MOCK) {
    let result = [...MOCK_POSTS]
    if (params?.type) {
      result = result.filter(p => p.type === params.type)
    }
    if (params?.featured !== undefined) {
      result = result.filter(p => p.featured === params.featured)
    }
    return result
  }

  const url = new URL(`${API_BASE_URL}/blog/post/list`)
  if (params?.type) url.searchParams.set('type', params.type)
  if (params?.featured !== undefined) url.searchParams.set('featured', String(params.featured))

  try {
    const res = await fetch(url.toString(), { next: { revalidate: 300 } })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || []
  } catch {
    return MOCK_POSTS
  }
}

export async function fetchPostBySlug(slug: string): Promise<BlogPost | null> {
  if (USE_MOCK) {
    return MOCK_POSTS.find(p => p.slug === slug) || null
  }

  try {
    const res = await fetch(`${API_BASE_URL}/blog/post/get-by-slug?slug=${encodeURIComponent(slug)}`, {
      next: { revalidate: 300 },
    })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || null
  } catch {
    return MOCK_POSTS.find(p => p.slug === slug) || null
  }
}

export async function fetchCategories(): Promise<Category[]> {
  if (USE_MOCK) {
    return mockCategories
  }

  try {
    const res = await fetch(`${API_BASE_URL}/coupon/category/list`, { next: { revalidate: 3600 } })
    if (!res.ok) throw new Error('Fetch failed')
    const json = await res.json()
    return json.data || []
  } catch {
    return mockCategories
  }
}
