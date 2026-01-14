import Link from "next/link"
import { Facebook, Twitter, Instagram, Linkedin, Send, ArrowRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

interface FooterProps {
  locale?: string;
}

export function Footer({ locale = 'en' }: FooterProps) {
  return (
    <footer className="bg-slate-950 text-slate-300 py-16 mt-16 relative overflow-hidden border-t border-slate-800/50">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_right,_var(--tw-gradient-stops))] from-slate-900 via-slate-950 to-slate-950 opacity-50 pointer-events-none" />
      
      <div className="container mx-auto px-4 relative z-10">
        <div className="mb-16 p-8 rounded-2xl bg-slate-900/50 border border-slate-800/50 backdrop-blur-sm">
            <div className="flex flex-col lg:flex-row items-center justify-between gap-8">
                <div className="text-center lg:text-left max-w-xl">
                    <h3 className="text-2xl md:text-3xl font-bold text-white mb-3 tracking-tight">Stay ahead of the best deals</h3>
                    <p className="text-slate-400 text-lg">Join 50,000+ smart shoppers saving money every day.</p>
                </div>
                <div className="flex w-full max-w-md items-center gap-3">
                    <div className="relative flex-1">
                        <Input 
                            type="email" 
                            placeholder="Enter your email address" 
                            className="bg-slate-950/80 border-slate-700/50 text-white placeholder:text-slate-500 focus-visible:ring-primary h-12 rounded-lg pl-4"
                        />
                    </div>
                    <Button size="lg" className="h-12 px-8 font-semibold shadow-[0_0_20px_rgba(59,130,246,0.15)] hover:shadow-[0_0_25px_rgba(59,130,246,0.25)] transition-all">
                        Subscribe
                    </Button>
                </div>
            </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-12 mb-16">
          <div className="lg:col-span-4 space-y-6">
            <Link href={`/${locale}`} className="inline-block">
                <h2 className="text-2xl font-bold text-white tracking-tighter flex items-center gap-2">
                    Ecommica
                    <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                </h2>
            </Link>
            <p className="text-slate-400 leading-relaxed text-sm max-w-sm">
              Your ultimate destination for verified coupon codes, exclusive deals, and smart shopping guides. Save more on brands you love.
            </p>
            <div className="flex space-x-4 pt-2">
              {[
                { Icon: Twitter, label: "Twitter" },
                { Icon: Facebook, label: "Facebook" },
                { Icon: Instagram, label: "Instagram" },
                { Icon: Linkedin, label: "LinkedIn" }
              ].map(({ Icon, label }) => (
                <Link 
                    key={label}
                    href="#" 
                    className="w-10 h-10 rounded-full bg-slate-900 flex items-center justify-center text-slate-400 hover:text-white hover:bg-primary/10 hover:scale-110 transition-all duration-300 border border-slate-800"
                >
                  <Icon className="h-5 w-5" />
                  <span className="sr-only">{label}</span>
                </Link>
              ))}
            </div>
          </div>

          <div className="lg:col-span-2 md:col-span-1">
            <h3 className="text-white font-semibold mb-6">Quick Links</h3>
            <ul className="space-y-4">
              {['Home', 'Deals', 'Coupons', 'Stores'].map((item) => (
                <li key={item}>
                    <Link href={`/${locale}/${item.toLowerCase() === 'home' ? '' : item.toLowerCase()}`} className="text-slate-400 hover:text-primary transition-colors text-sm flex items-center group">
                        <span className="w-0 group-hover:w-2 h-px bg-primary mr-0 group-hover:mr-2 transition-all duration-300" />
                        {item}
                    </Link>
                </li>
              ))}
            </ul>
          </div>

          <div className="lg:col-span-3 md:col-span-1">
            <h3 className="text-white font-semibold mb-6">Popular Categories</h3>
            <ul className="space-y-4">
              {[
                  { label: 'Electronics', href: `/${locale}/category/electronics` },
                  { label: 'Fashion', href: `/${locale}/category/fashion` },
                  { label: 'Home & Garden', href: `/${locale}/category/home-garden` },
                  { label: 'Beauty', href: `/${locale}/category/beauty` },
                  { label: 'Sports', href: `/${locale}/category/sports-outdoors` }
              ].map((item) => (
                <li key={item.label}>
                    <Link 
                        href={item.href} 
                        className="text-sm flex items-center group text-slate-400 hover:text-primary transition-colors"
                    >
                         <span className="w-0 group-hover:w-2 h-px bg-primary mr-0 group-hover:mr-2 transition-all duration-300" />
                        {item.label}
                    </Link>
                </li>
              ))}
            </ul>
          </div>

          <div className="lg:col-span-3 md:col-span-1">
            <h3 className="text-white font-semibold mb-6">Support & Resources</h3>
            <ul className="space-y-4">
              {[
{ label: 'Blog', href: `/${locale}/blog` },
                  { label: 'About Us', href: `/${locale}/about` },
                  { label: 'Contact', href: `/${locale}/contact` },
                  { label: 'FAQ', href: `/${locale}/faq` }
              ].map((item) => (
                <li key={item.label}>
                    <Link href={item.href} className="text-slate-400 hover:text-primary transition-colors text-sm flex items-center group">
                        <span className="w-0 group-hover:w-2 h-px bg-primary mr-0 group-hover:mr-2 transition-all duration-300" />
                        {item.label}
                    </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <div className="border-t border-slate-800 pt-8 flex flex-col md:flex-row justify-between items-center gap-6">
          <p className="text-sm text-slate-500">
            &copy; {new Date().getFullYear()} Ecommica. All rights reserved.
          </p>
          <div className="flex flex-wrap justify-center gap-x-8 gap-y-2 text-sm">
            {['Privacy Policy', 'Terms of Service', 'Cookie Policy'].map((item) => (
                <Link 
                    key={item} 
                    href={`/${locale}/${item.toLowerCase().replace(/ /g, '-')}`} 
                    className="text-slate-500 hover:text-slate-300 transition-colors"
                >
                    {item}
                </Link>
            ))}
          </div>
        </div>
      </div>
    </footer>
  )
}
