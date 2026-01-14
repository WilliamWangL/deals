import Link from "next/link"
import { Facebook, Twitter, Instagram, Linkedin } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

export function Footer() {
  return (
    <footer className="bg-slate-900 text-slate-300 py-12 mt-12">
      <div className="container mx-auto px-4">
        <div className="mb-12 border-b border-slate-800 pb-12">
            <div className="flex flex-col lg:flex-row items-center justify-between gap-6">
                <div className="text-center lg:text-left">
                    <h3 className="text-2xl font-bold text-white mb-2">Join our newsletter</h3>
                    <p className="text-slate-400">Get the latest deals and coupons delivered right to your inbox.</p>
                </div>
                <div className="flex w-full max-w-md items-center space-x-2">
                    <Input 
                        type="email" 
                        placeholder="Enter your email" 
                        className="bg-slate-800 border-slate-700 text-white placeholder:text-slate-500 focus-visible:ring-slate-500"
                    />
                    <Button variant="secondary">Subscribe</Button>
                </div>
            </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
          <div className="space-y-4">
            <h2 className="text-xl font-bold text-white">Ecommica</h2>
            <p className="text-sm text-slate-400 leading-relaxed">
              Your ultimate destination for the best deals, coupons, and savings from your favorite stores.
            </p>
            <div className="flex space-x-4 pt-2">
              <Link href="#" className="hover:text-white transition-colors duration-200">
                <Twitter className="h-5 w-5" />
                <span className="sr-only">Twitter</span>
              </Link>
              <Link href="#" className="hover:text-white transition-colors duration-200">
                <Facebook className="h-5 w-5" />
                <span className="sr-only">Facebook</span>
              </Link>
              <Link href="#" className="hover:text-white transition-colors duration-200">
                <Instagram className="h-5 w-5" />
                <span className="sr-only">Instagram</span>
              </Link>
              <Link href="#" className="hover:text-white transition-colors duration-200">
                <Linkedin className="h-5 w-5" />
                <span className="sr-only">LinkedIn</span>
              </Link>
            </div>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-white mb-4">Quick Links</h3>
            <ul className="space-y-2">
              <li><Link href="/" className="hover:text-white transition-colors duration-200">Home</Link></li>
              <li><Link href="/deals" className="hover:text-white transition-colors duration-200">Deals</Link></li>
              <li><Link href="/coupons" className="hover:text-white transition-colors duration-200">Coupons</Link></li>
              <li><Link href="/stores" className="hover:text-white transition-colors duration-200">Stores</Link></li>
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-white mb-4">Categories</h3>
            <ul className="space-y-2">
              <li><Link href="/category/electronics" className="hover:text-white transition-colors duration-200">Electronics</Link></li>
              <li><Link href="/category/fashion" className="hover:text-white transition-colors duration-200">Fashion</Link></li>
              <li><Link href="/category/home" className="hover:text-white transition-colors duration-200">Home & Garden</Link></li>
              <li><Link href="/category/beauty" className="hover:text-white transition-colors duration-200">Beauty</Link></li>
              <li><Link href="/categories" className="hover:text-white transition-colors duration-200">View All</Link></li>
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-white mb-4">Resources</h3>
            <ul className="space-y-2">
              <li><Link href="/blog" className="hover:text-white transition-colors duration-200">Blog</Link></li>
              <li><Link href="/about" className="hover:text-white transition-colors duration-200">About Us</Link></li>
              <li><Link href="/contact" className="hover:text-white transition-colors duration-200">Contact</Link></li>
              <li><Link href="/faq" className="hover:text-white transition-colors duration-200">FAQ</Link></li>
            </ul>
          </div>
        </div>

        <div className="border-t border-slate-800 pt-8 flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-sm text-slate-500">
            &copy; {new Date().getFullYear()} Ecommica. All rights reserved.
          </p>
          <div className="flex flex-wrap justify-center gap-x-6 gap-y-2 text-sm">
            <Link href="/privacy" className="hover:text-white transition-colors duration-200">Privacy Policy</Link>
            <Link href="/terms" className="hover:text-white transition-colors duration-200">Terms of Service</Link>
            <Link href="/cookies" className="hover:text-white transition-colors duration-200">Cookie Policy</Link>
          </div>
        </div>
      </div>
    </footer>
  )
}
