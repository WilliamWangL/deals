import type { Category } from '@/types'

export const mockCategories: Category[] = [
  {
    id: 1,
    name: 'Electronics',
    slug: 'electronics',
    icon: 'Laptop',
    children: [
      { id: 11, name: 'Computers', slug: 'computers', icon: 'Monitor' },
      { id: 12, name: 'Smartphones', slug: 'smartphones', icon: 'Smartphone' },
      { id: 13, name: 'Audio', slug: 'audio', icon: 'Headphones' },
      { id: 14, name: 'Gaming', slug: 'gaming', icon: 'Gamepad2' },
      { id: 15, name: 'Cameras', slug: 'cameras', icon: 'Camera' },
    ],
  },
  {
    id: 2,
    name: 'Fashion',
    slug: 'fashion',
    icon: 'Shirt',
    children: [
      { id: 21, name: 'Men\'s Clothing', slug: 'mens-clothing', icon: 'Shirt' },
      { id: 22, name: 'Women\'s Clothing', slug: 'womens-clothing', icon: 'Shirt' },
      { id: 23, name: 'Shoes', slug: 'shoes', icon: 'Footprints' },
      { id: 24, name: 'Accessories', slug: 'accessories', icon: 'Watch' },
    ],
  },
  {
    id: 3,
    name: 'Home & Garden',
    slug: 'home-garden',
    icon: 'Home',
    children: [
      { id: 31, name: 'Furniture', slug: 'furniture', icon: 'Sofa' },
      { id: 32, name: 'Kitchen', slug: 'kitchen', icon: 'ChefHat' },
      { id: 33, name: 'Bedding', slug: 'bedding', icon: 'Bed' },
      { id: 34, name: 'Outdoor', slug: 'outdoor', icon: 'Tent' },
    ],
  },
  {
    id: 4,
    name: 'Beauty',
    slug: 'beauty',
    icon: 'Sparkles',
    children: [
      { id: 41, name: 'Skincare', slug: 'skincare', icon: 'Droplet' },
      { id: 42, name: 'Makeup', slug: 'makeup', icon: 'Palette' },
      { id: 43, name: 'Fragrance', slug: 'fragrance', icon: 'Flower' },
      { id: 44, name: 'Haircare', slug: 'haircare', icon: 'Scissors' },
    ],
  },
  {
    id: 5,
    name: 'Sports & Outdoors',
    slug: 'sports-outdoors',
    icon: 'Dumbbell',
    children: [
      { id: 51, name: 'Fitness', slug: 'fitness', icon: 'Dumbbell' },
      { id: 52, name: 'Camping', slug: 'camping', icon: 'Tent' },
      { id: 53, name: 'Cycling', slug: 'cycling', icon: 'Bike' },
      { id: 54, name: 'Running', slug: 'running', icon: 'Footprints' },
    ],
  },
  {
    id: 6,
    name: 'Baby & Kids',
    slug: 'baby-kids',
    icon: 'Baby',
    children: [
      { id: 61, name: 'Baby Gear', slug: 'baby-gear', icon: 'Baby' },
      { id: 62, name: 'Toys', slug: 'toys', icon: 'Puzzle' },
      { id: 63, name: 'Kids\' Clothing', slug: 'kids-clothing', icon: 'Shirt' },
    ],
  },
  {
    id: 7,
    name: 'Food & Grocery',
    slug: 'food-grocery',
    icon: 'ShoppingBasket',
    children: [
      { id: 71, name: 'Pantry', slug: 'pantry', icon: 'Package' },
      { id: 72, name: 'Beverages', slug: 'beverages', icon: 'Coffee' },
      { id: 73, name: 'Snacks', slug: 'snacks', icon: 'Cookie' },
    ],
  },
  {
    id: 8,
    name: 'Health',
    slug: 'health',
    icon: 'Heart',
    children: [
      { id: 81, name: 'Vitamins', slug: 'vitamins', icon: 'Pill' },
      { id: 82, name: 'Personal Care', slug: 'personal-care', icon: 'Bath' },
      { id: 83, name: 'Medical', slug: 'medical', icon: 'Stethoscope' },
    ],
  },
]

export const getCategoryBySlug = (slug: string): Category | undefined => {
  for (const category of mockCategories) {
    if (category.slug === slug) return category
    if (category.children) {
      const child = category.children.find(c => c.slug === slug)
      if (child) return child
    }
  }
  return undefined
}
