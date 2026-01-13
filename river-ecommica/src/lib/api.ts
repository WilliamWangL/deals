import { Deal, Store } from '@/types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/app-api';

const MOCK_DEALS: Deal[] = [
    {
        id: 1,
        title: "50% Off Everything",
        slug: "50-off-everything",
        description: "Get 50% off sitewide",
        originalPrice: 100,
        dealPrice: 50,
        discountPercent: 50,
        merchantName: "Amazon",
        merchantLogo: "",
        imageUrl: "",
        startTime: "2023-01-01",
        endTime: "2023-12-31",
        featured: true
    },
    {
        id: 2,
        title: "$20 Off Orders Over $100",
        slug: "20-off-orders-over-100",
        description: "Save $20 when you spend $100 or more",
        originalPrice: 0,
        dealPrice: 0,
        discountPercent: 20,
        merchantName: "Nike",
        merchantLogo: "",
        imageUrl: "",
        startTime: "2023-01-01",
        endTime: "2023-12-31",
        featured: false
    }
];

const MOCK_STORES: Store[] = [
    {
        id: 1,
        name: "Amazon",
        slug: "amazon",
        logoUrl: "",
        description: "Everything store",
        domain: "amazon.com",
        rating: 4.5,
        dealCount: 120,
        couponCount: 50
    },
    {
        id: 2,
        name: "Nike",
        slug: "nike",
        logoUrl: "",
        description: "Just do it",
        domain: "nike.com",
        rating: 4.8,
        dealCount: 45,
        couponCount: 10
    }
];

export async function fetchDeals(params?: { page?: number; category?: string }): Promise<{ data: Deal[] }> {
  const url = new URL(`${API_BASE_URL}/affiliate/deal/list`);
  if (params?.page) url.searchParams.set('pageNo', String(params.page));
  if (params?.category) url.searchParams.set('category', params.category);
  
  try {
    const res = await fetch(url.toString(), { next: { revalidate: 300 } });
    if (!res.ok) throw new Error("Fetch failed");
    return res.json();
  } catch (error) {
    console.error('Failed to fetch deals, using mock data:', error);
    return { data: MOCK_DEALS };
  }
}

export async function fetchStores(): Promise<{ data: Store[] }> {
  try {
    const res = await fetch(`${API_BASE_URL}/affiliate/merchant/list`, { next: { revalidate: 3600 } });
    if (!res.ok) throw new Error("Fetch failed");
    return res.json();
  } catch (error) {
    console.error('Failed to fetch stores, using mock data:', error);
    return { data: MOCK_STORES };
  }
}
