import { Deal, Store, Coupon } from '@/types';

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
        merchantId: 1,
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
        merchantId: 2,
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
        couponCount: 50,
        regions: ["US", "UK"]
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
        couponCount: 10,
        regions: ["US"]
    }
];

const MOCK_COUPONS: Coupon[] = [
    {
        id: 1,
        code: "SAVE20",
        description: "Save 20% on your order",
        discountType: 1,
        discountValue: 20,
        minPurchase: 50,
        merchantId: 1,
        merchantName: "Amazon",
        merchantLogo: "",
        endTime: "2024-12-31",
        verified: true
    },
    {
        id: 2,
        code: "FREESHIP",
        description: "Free shipping on all orders",
        discountType: 3,
        discountValue: 0,
        minPurchase: 0,
        merchantId: 2,
        merchantName: "Nike",
        merchantLogo: "",
        endTime: "2024-12-31",
        verified: true
    }
];

export async function fetchDeals(params?: { merchantId?: number; featured?: boolean }): Promise<Deal[]> {
    const url = new URL(`${API_BASE_URL}/coupon/deal/list`);
    if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId));
    if (params?.featured !== undefined) url.searchParams.set('featured', String(params.featured));

    try {
        const res = await fetch(url.toString(), { next: { revalidate: 300 } });
        if (!res.ok) throw new Error("Fetch failed");
        const json = await res.json();
        return json.data || [];
    } catch (error) {
        console.error('Failed to fetch deals, using mock data:', error);
        return MOCK_DEALS;
    }
}

export async function fetchDealBySlug(slug: string): Promise<Deal | null> {
    try {
        const res = await fetch(`${API_BASE_URL}/coupon/deal/get-by-slug?slug=${encodeURIComponent(slug)}`, {
            next: { revalidate: 300 }
        });
        if (!res.ok) throw new Error("Fetch failed");
        const json = await res.json();
        return json.data || null;
    } catch (error) {
        console.error('Failed to fetch deal by slug, using mock data:', error);
        return MOCK_DEALS.find(d => d.slug === slug) || null;
    }
}

export async function fetchStores(): Promise<Store[]> {
    try {
        const res = await fetch(`${API_BASE_URL}/affiliate/merchant/list`, { next: { revalidate: 3600 } });
        if (!res.ok) throw new Error("Fetch failed");
        const json = await res.json();
        return json.data || [];
    } catch (error) {
        console.error('Failed to fetch stores, using mock data:', error);
        return MOCK_STORES;
    }
}

export async function fetchStoreBySlug(slug: string): Promise<Store | null> {
    try {
        const res = await fetch(`${API_BASE_URL}/affiliate/merchant/get-by-slug?slug=${encodeURIComponent(slug)}`, {
            next: { revalidate: 3600 }
        });
        if (!res.ok) throw new Error("Fetch failed");
        const json = await res.json();
        return json.data || null;
    } catch (error) {
        console.error('Failed to fetch store by slug, using mock data:', error);
        return MOCK_STORES.find(s => s.slug === slug) || null;
    }
}

export async function fetchCoupons(params?: { merchantId?: number; verified?: boolean }): Promise<Coupon[]> {
    const url = new URL(`${API_BASE_URL}/coupon/coupon/list`);
    if (params?.merchantId) url.searchParams.set('merchantId', String(params.merchantId));
    if (params?.verified !== undefined) url.searchParams.set('verified', String(params.verified));

    try {
        const res = await fetch(url.toString(), { next: { revalidate: 300 } });
        if (!res.ok) throw new Error("Fetch failed");
        const json = await res.json();
        return json.data || [];
    } catch (error) {
        console.error('Failed to fetch coupons, using mock data:', error);
        return MOCK_COUPONS;
    }
}
