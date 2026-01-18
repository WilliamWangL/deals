import { Deal, Store, Coupon, BlogPost, Category } from '@/types'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:48080/app-api'

// BlogPost type 映射
const POST_TYPE_MAP: Record<number, BlogPost['type']> = {
  1: 'deal',
  2: 'review',
  3: 'tutorial',
  4: 'news'
}

function mapPostType(post: Record<string, unknown>): BlogPost {
  return {
    ...post,
    type: POST_TYPE_MAP[post.type as number] || 'news'
  } as BlogPost
}

export async function fetchDeals(params?: { merchantId?: number; featured?: boolean }): Promise<Deal[]> {
  const url = new URL(`${API_BASE_URL}/coupon/deal/list`)
  if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId))
  if (params?.featured !== undefined) url.searchParams.set('featured', String(params.featured))

  const res = await fetch(url.toString(), { next: { revalidate: 300 } })
  if (!res.ok) throw new Error('Fetch deals failed')
  const json = await res.json()
  return json.data || []
}

export async function fetchDealBySlug(slug: string): Promise<Deal | null> {
  const res = await fetch(`${API_BASE_URL}/coupon/deal/get-by-slug?slug=${encodeURIComponent(slug)}`, {
    next: { revalidate: 300 },
  })
  if (!res.ok) throw new Error('Fetch deal failed')
  const json = await res.json()
  return json.data || null
}

export async function fetchStores(): Promise<Store[]> {
  const res = await fetch(`${API_BASE_URL}/affiliate/merchant/list`, { next: { revalidate: 3600 } })
  if (!res.ok) throw new Error('Fetch stores failed')
  const json = await res.json()
  return json.data || []
}

export async function fetchStoreBySlug(slug: string): Promise<Store | null> {
  const res = await fetch(`${API_BASE_URL}/affiliate/merchant/get-by-slug?slug=${encodeURIComponent(slug)}`, {
    next: { revalidate: 3600 },
  })
  if (!res.ok) throw new Error('Fetch store failed')
  const json = await res.json()
  return json.data || null
}

export async function fetchCoupons(params?: { merchantId?: number; verified?: boolean }): Promise<Coupon[]> {
  const url = new URL(`${API_BASE_URL}/coupon/coupon/list`)
  if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId))
  if (params?.verified !== undefined) url.searchParams.set('verified', String(params.verified))

  const res = await fetch(url.toString(), { next: { revalidate: 300 } })
  if (!res.ok) throw new Error('Fetch coupons failed')
  const json = await res.json()
  return json.data || []
}

export async function fetchPosts(params?: { type?: string; featured?: boolean }): Promise<BlogPost[]> {
  const url = new URL(`${API_BASE_URL}/blog/post/list`)
  if (params?.type) url.searchParams.set('type', params.type)
  if (params?.featured !== undefined) url.searchParams.set('featured', String(params.featured))

  const res = await fetch(url.toString(), { next: { revalidate: 300 } })
  if (!res.ok) throw new Error('Fetch posts failed')
  const json = await res.json()
  return (json.data || []).map(mapPostType)
}

export async function fetchPostBySlug(slug: string): Promise<BlogPost | null> {
  const res = await fetch(`${API_BASE_URL}/blog/post/get-by-slug?slug=${encodeURIComponent(slug)}`, {
    next: { revalidate: 300 },
  })
  if (!res.ok) throw new Error('Fetch post failed')
  const json = await res.json()
  return json.data ? mapPostType(json.data) : null
}

export async function fetchCategories(): Promise<Category[]> {
  const res = await fetch(`${API_BASE_URL}/affiliate/category/tree`, { next: { revalidate: 3600 } })
  if (!res.ok) throw new Error('Fetch categories failed')
  const json = await res.json()
  return json.data || []
}
