export interface Deal {
  id: number;
  title: string;
  slug: string;
  description: string;
  originalPrice: number;
  dealPrice: number;
  discountPercent: number;
  merchantName: string;
  merchantLogo: string;
  imageUrl: string;
  startTime: string;
  endTime: string;
  featured: boolean;
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
}

export interface Coupon {
  id: number;
  code: string;
  description: string;
  discountType: 'percent' | 'fixed' | 'free_shipping';
  discountValue: number;
  merchantName: string;
  merchantLogo: string;
  endTime: string;
  verified: boolean;
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
