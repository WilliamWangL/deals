export interface Deal {
  id: number;
  title: string;
  slug: string;
  description: string;
  originalPrice: number;
  dealPrice: number;
  discountPercent: number;
  merchantId: number;
  merchantName: string;
  merchantLogo: string;
  imageUrl: string;
  startTime: string;
  endTime: string;
  featured: boolean;
  trackingLinkId?: string;
}

export interface Store {
  id: number;
  name: string;
  slug: string;
  logoUrl: string;
  description: string;
  domain: string;
  rating: number;
  dealCount: number;
  couponCount: number;
  regions?: string[];
}

export interface Coupon {
  id: number;
  code: string;
  description: string;
  discountType: number;
  discountValue: number;
  minPurchase?: number;
  merchantId: number;
  merchantName: string;
  merchantLogo: string;
  endTime: string;
  verified: boolean;
  trackingLinkId?: string;
}

export interface BlogPost {
  id: number;
  title: string;
  slug: string;
  excerpt: string;
  coverImage: string;
  authorName: string;
  publishedAt: string;
  type: 'deal' | 'review' | 'tutorial' | 'news';
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  icon: string;
  children?: Category[];
}
