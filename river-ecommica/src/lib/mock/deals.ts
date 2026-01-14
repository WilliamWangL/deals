import type { Deal } from '@/types'

// 生成未来日期
const futureDate = (days: number) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return date.toISOString()
}

// 生成过去日期
const pastDate = (days: number) => {
  const date = new Date()
  date.setDate(date.getDate() - days)
  return date.toISOString()
}

export const mockDeals: Deal[] = [
  // 1. Featured - 高折扣 Apple 产品
  {
    id: 1,
    title: 'Apple MacBook Air M3 15" - Save $200 on the Latest Model',
    slug: 'apple-macbook-air-m3-15-save-200',
    description: 'Get the powerful MacBook Air M3 with 15-inch Liquid Retina display, 8GB RAM, and 256GB SSD at an incredible price.',
    originalPrice: 1299,
    dealPrice: 1099,
    discountPercent: 15,
    merchantId: 1,
    merchantName: 'Amazon',
    merchantLogo: 'https://logo.clearbit.com/amazon.com',
    imageUrl: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&h=400&fit=crop',
    startTime: pastDate(5),
    endTime: futureDate(10),
    featured: true,
    trackingLinkId: 'trk_001',
  },
  // 2. Featured - 超高折扣
  {
    id: 2,
    title: 'Sony WH-1000XM5 Wireless Noise Cancelling Headphones - 40% OFF!',
    slug: 'sony-wh1000xm5-40-off',
    description: 'Industry-leading noise cancellation with premium sound quality. Limited time offer.',
    originalPrice: 399,
    dealPrice: 239,
    discountPercent: 40,
    merchantId: 2,
    merchantName: 'Best Buy',
    merchantLogo: 'https://logo.clearbit.com/bestbuy.com',
    imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=400&fit=crop',
    startTime: pastDate(2),
    endTime: futureDate(5),
    featured: true,
    trackingLinkId: 'trk_002',
  },
  // 3. 即将过期 - 紧迫感
  {
    id: 3,
    title: 'Nintendo Switch OLED Model Bundle - Ends Tonight!',
    slug: 'nintendo-switch-oled-bundle-ending',
    description: 'Switch OLED + Mario Kart 8 Deluxe + 3 Month Online Membership. Deal ends in hours!',
    originalPrice: 449,
    dealPrice: 349,
    discountPercent: 22,
    merchantId: 3,
    merchantName: 'Target',
    merchantLogo: 'https://logo.clearbit.com/target.com',
    imageUrl: 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=600&h=400&fit=crop',
    startTime: pastDate(7),
    endTime: futureDate(1),
    featured: false,
    trackingLinkId: 'trk_003',
  },
  // 4. 家居产品
  {
    id: 4,
    title: 'Dyson V15 Detect Absolute Cordless Vacuum - Lowest Price Ever',
    slug: 'dyson-v15-detect-lowest-price',
    description: 'Laser reveals hidden dust. Powerful suction with LCD screen showing real-time particle counts.',
    originalPrice: 749,
    dealPrice: 549,
    discountPercent: 27,
    merchantId: 1,
    merchantName: 'Amazon',
    merchantLogo: 'https://logo.clearbit.com/amazon.com',
    imageUrl: 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600&h=400&fit=crop',
    startTime: pastDate(1),
    endTime: futureDate(14),
    featured: true,
    trackingLinkId: 'trk_004',
  },
  // 5. 低折扣但热门
  {
    id: 5,
    title: 'Apple AirPods Pro 2nd Gen - 12% Off',
    slug: 'airpods-pro-2-12-off',
    description: 'Active Noise Cancellation, Spatial Audio, MagSafe Charging Case included.',
    originalPrice: 249,
    dealPrice: 219,
    discountPercent: 12,
    merchantId: 1,
    merchantName: 'Amazon',
    merchantLogo: 'https://logo.clearbit.com/amazon.com',
    imageUrl: 'https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=600&h=400&fit=crop',
    startTime: pastDate(10),
    endTime: futureDate(20),
    featured: false,
    trackingLinkId: 'trk_005',
  },
  // 6. 时尚类 - 高折扣
  {
    id: 6,
    title: 'Nike Air Max 270 - Up to 55% Off Selected Colors',
    slug: 'nike-air-max-270-55-off',
    description: 'Iconic style meets all-day comfort. Multiple colorways available at clearance prices.',
    originalPrice: 150,
    dealPrice: 67,
    discountPercent: 55,
    merchantId: 4,
    merchantName: 'Nike',
    merchantLogo: 'https://logo.clearbit.com/nike.com',
    imageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&h=400&fit=crop',
    startTime: pastDate(3),
    endTime: futureDate(7),
    featured: false,
    trackingLinkId: 'trk_006',
  },
  // 7. 游戏类
  {
    id: 7,
    title: 'PlayStation 5 Slim Console + Free Game - Limited Stock',
    slug: 'ps5-slim-free-game',
    description: 'PS5 Slim with Spider-Man 2 included. While supplies last.',
    originalPrice: 559,
    dealPrice: 499,
    discountPercent: 11,
    merchantId: 3,
    merchantName: 'Target',
    merchantLogo: 'https://logo.clearbit.com/target.com',
    imageUrl: 'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=600&h=400&fit=crop',
    startTime: pastDate(1),
    endTime: futureDate(3),
    featured: true,
    trackingLinkId: 'trk_007',
  },
  // 8. 厨房电器
  {
    id: 8,
    title: 'Ninja Foodi 9-in-1 Pressure Cooker & Air Fryer - 35% OFF',
    slug: 'ninja-foodi-9-in-1-35-off',
    description: 'Pressure cook, air fry, slow cook, steam, and more. Perfect for busy families.',
    originalPrice: 229,
    dealPrice: 149,
    discountPercent: 35,
    merchantId: 5,
    merchantName: 'Walmart',
    merchantLogo: 'https://logo.clearbit.com/walmart.com',
    imageUrl: 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600&h=400&fit=crop',
    startTime: pastDate(4),
    endTime: futureDate(12),
    featured: false,
    trackingLinkId: 'trk_008',
  },
  // 9. 4K 电视
  {
    id: 9,
    title: 'LG 65" OLED C3 4K Smart TV - Save $500',
    slug: 'lg-65-oled-c3-save-500',
    description: 'Self-lit OLED pixels, α9 AI Processor, Dolby Vision & Atmos. Ultimate viewing experience.',
    originalPrice: 1799,
    dealPrice: 1299,
    discountPercent: 28,
    merchantId: 2,
    merchantName: 'Best Buy',
    merchantLogo: 'https://logo.clearbit.com/bestbuy.com',
    imageUrl: 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600&h=400&fit=crop',
    startTime: pastDate(2),
    endTime: futureDate(8),
    featured: true,
    trackingLinkId: 'trk_009',
  },
  // 10. 健身器材
  {
    id: 10,
    title: 'Theragun Elite Massage Gun - 30% Off',
    slug: 'theragun-elite-30-off',
    description: 'Professional-grade percussive therapy. 5 built-in speeds, QuietForce Technology.',
    originalPrice: 399,
    dealPrice: 279,
    discountPercent: 30,
    merchantId: 1,
    merchantName: 'Amazon',
    merchantLogo: 'https://logo.clearbit.com/amazon.com',
    imageUrl: 'https://images.unsplash.com/photo-1576678927484-cc907957088c?w=600&h=400&fit=crop',
    startTime: pastDate(6),
    endTime: futureDate(15),
    featured: false,
    trackingLinkId: 'trk_010',
  },
  // 11. 办公设备
  {
    id: 11,
    title: 'Samsung 34" Ultrawide QHD Monitor - Now $299',
    slug: 'samsung-34-ultrawide-299',
    description: '34" curved display, 100Hz refresh rate, AMD FreeSync. Perfect for productivity.',
    originalPrice: 449,
    dealPrice: 299,
    discountPercent: 33,
    merchantId: 2,
    merchantName: 'Best Buy',
    merchantLogo: 'https://logo.clearbit.com/bestbuy.com',
    imageUrl: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600&h=400&fit=crop',
    startTime: pastDate(8),
    endTime: futureDate(6),
    featured: false,
    trackingLinkId: 'trk_011',
  },
  // 12. 美妆产品
  {
    id: 12,
    title: 'Estée Lauder Advanced Night Repair Serum Set - 25% OFF',
    slug: 'estee-lauder-night-repair-set',
    description: 'Best-selling serum + eye concentrate + cleansing foam. Limited edition gift set.',
    originalPrice: 185,
    dealPrice: 139,
    discountPercent: 25,
    merchantId: 6,
    merchantName: 'Sephora',
    merchantLogo: 'https://logo.clearbit.com/sephora.com',
    imageUrl: 'https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=600&h=400&fit=crop',
    startTime: pastDate(5),
    endTime: futureDate(10),
    featured: false,
    trackingLinkId: 'trk_012',
  },
  // 13. 相机设备
  {
    id: 13,
    title: 'GoPro HERO12 Black Creator Edition - 20% Off',
    slug: 'gopro-hero12-creator-edition',
    description: 'Includes Volta grip, Media Mod, Light Mod. Everything you need to create.',
    originalPrice: 579,
    dealPrice: 463,
    discountPercent: 20,
    merchantId: 1,
    merchantName: 'Amazon',
    merchantLogo: 'https://logo.clearbit.com/amazon.com',
    imageUrl: 'https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&h=400&fit=crop',
    startTime: pastDate(3),
    endTime: futureDate(9),
    featured: false,
    trackingLinkId: 'trk_013',
  },
  // 14. 智能家居
  {
    id: 14,
    title: 'Ring Video Doorbell Pro 2 + Echo Show 5 Bundle - Save $80',
    slug: 'ring-doorbell-echo-bundle',
    description: 'Advanced motion detection, HD video, and hands-free video calls on Echo Show 5.',
    originalPrice: 329,
    dealPrice: 249,
    discountPercent: 24,
    merchantId: 1,
    merchantName: 'Amazon',
    merchantLogo: 'https://logo.clearbit.com/amazon.com',
    imageUrl: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&h=400&fit=crop',
    startTime: pastDate(2),
    endTime: futureDate(11),
    featured: false,
    trackingLinkId: 'trk_014',
  },
  // 15. 户外装备
  {
    id: 15,
    title: 'The North Face Thermoball Eco Jacket - 45% Off',
    slug: 'north-face-thermoball-45-off',
    description: 'Lightweight, packable warmth with recycled materials. Men\'s and Women\'s sizes.',
    originalPrice: 230,
    dealPrice: 127,
    discountPercent: 45,
    merchantId: 7,
    merchantName: 'REI',
    merchantLogo: 'https://logo.clearbit.com/rei.com',
    imageUrl: 'https://images.unsplash.com/photo-1544923246-77307dd628b5?w=600&h=400&fit=crop',
    startTime: pastDate(7),
    endTime: futureDate(4),
    featured: false,
    trackingLinkId: 'trk_015',
  },
  // 16. 咖啡机
  {
    id: 16,
    title: 'Breville Barista Express Espresso Machine - $100 Off',
    slug: 'breville-barista-express-100-off',
    description: 'Built-in grinder, steam wand, precise espresso extraction. Café quality at home.',
    originalPrice: 749,
    dealPrice: 649,
    discountPercent: 13,
    merchantId: 8,
    merchantName: 'Williams Sonoma',
    merchantLogo: 'https://logo.clearbit.com/williams-sonoma.com',
    imageUrl: 'https://images.unsplash.com/photo-1510017803434-a899398421b3?w=600&h=400&fit=crop',
    startTime: pastDate(4),
    endTime: futureDate(18),
    featured: false,
    trackingLinkId: 'trk_016',
  },
  // 17. 平板电脑 - 超高折扣
  {
    id: 17,
    title: 'Samsung Galaxy Tab S9 FE - Massive 50% Off Clearance',
    slug: 'galaxy-tab-s9-fe-50-off',
    description: 'S Pen included, IP68 water resistance, 10.9" display. While stocks last!',
    originalPrice: 449,
    dealPrice: 225,
    discountPercent: 50,
    merchantId: 2,
    merchantName: 'Best Buy',
    merchantLogo: 'https://logo.clearbit.com/bestbuy.com',
    imageUrl: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600&h=400&fit=crop',
    startTime: pastDate(1),
    endTime: futureDate(2),
    featured: true,
    trackingLinkId: 'trk_017',
  },
  // 18. 婴儿用品
  {
    id: 18,
    title: 'UPPAbaby VISTA V2 Stroller - 18% Off',
    slug: 'uppababy-vista-v2-18-off',
    description: 'Premium stroller with bassinet included. Expands to hold up to 3 children.',
    originalPrice: 1099,
    dealPrice: 899,
    discountPercent: 18,
    merchantId: 9,
    merchantName: 'buybuy BABY',
    merchantLogo: 'https://logo.clearbit.com/buybuybaby.com',
    imageUrl: 'https://images.unsplash.com/photo-1566004100631-35d015d6a491?w=600&h=400&fit=crop',
    startTime: pastDate(10),
    endTime: futureDate(20),
    featured: false,
    trackingLinkId: 'trk_018',
  },
]

// 获取精选 deals
export const getFeaturedDeals = (): Deal[] => {
  return mockDeals.filter(deal => deal.featured)
}

// 获取即将过期的 deals (3天内)
export const getExpiringDeals = (): Deal[] => {
  const threeDaysLater = new Date()
  threeDaysLater.setDate(threeDaysLater.getDate() + 3)
  return mockDeals.filter(deal => new Date(deal.endTime) <= threeDaysLater && new Date(deal.endTime) > new Date())
}

// 获取高折扣 deals (30%+)
export const getHighDiscountDeals = (): Deal[] => {
  return mockDeals.filter(deal => deal.discountPercent >= 30)
}
