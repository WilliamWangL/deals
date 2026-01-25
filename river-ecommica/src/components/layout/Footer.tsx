import Link from "next/link"
import { Facebook, Twitter, Instagram, Linkedin, Mail, Sparkles } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

interface FooterProps {
  locale?: string;
}

export function Footer({ locale = 'en' }: FooterProps) {
  const quickLinks = [
    { label: 'Home', href: `/${locale}` },
    { label: 'Deals', href: `/${locale}/deals` },
    { label: 'Coupons', href: `/${locale}/coupons` },
    { label: 'Stores', href: `/${locale}/stores` },
  ];

  const supportLinks = [
    { label: 'Blog', href: `/${locale}/blog` },
    { label: 'About Us', href: `/${locale}/about` },
    { label: 'Contact', href: `/${locale}/contact` },
    { label: 'FAQ', href: `/${locale}/faq` },
  ];

  const legalLinks = [
    { label: 'Privacy Policy', href: `/${locale}/privacy-policy` },
    { label: 'Terms of Service', href: `/${locale}/terms-of-service` },
    { label: 'Cookie Policy', href: `/${locale}/cookie-policy` },
  ];

  const socialLinks = [
    { Icon: Twitter, label: "Twitter", href: "#" },
    { Icon: Facebook, label: "Facebook", href: "#" },
    { Icon: Instagram, label: "Instagram", href: "#" },
    { Icon: Linkedin, label: "LinkedIn", href: "#" },
  ];

  return (
    <footer className="bg-slate-950 text-slate-300 pt-16 pb-8 mt-16 relative overflow-hidden border-t border-white/5">
      {/* Background decorations */}
      <div className="absolute inset-0 pointer-events-none">
        <div className="absolute top-0 left-1/4 w-[500px] h-[500px] bg-primary/5 rounded-full blur-[120px]" />
        <div className="absolute bottom-0 right-1/4 w-[400px] h-[400px] bg-accent/5 rounded-full blur-[100px]" />
      </div>

      <div className="container mx-auto px-4 relative z-10">
        {/* Newsletter Section */}
        <div className="mb-12 p-8 rounded-3xl bg-gradient-to-r from-slate-900/80 via-slate-900/90 to-slate-900/80 border border-white/10 backdrop-blur-sm shadow-2xl shadow-black/20">
          <div className="flex flex-col lg:flex-row items-center justify-between gap-8">
            <div className="text-center lg:text-left flex-1">
              <div className="flex items-center justify-center lg:justify-start gap-2 mb-3">
                <div className="p-1.5 rounded-lg bg-primary/10">
                  <Sparkles className="w-5 h-5 text-primary" />
                </div>
                <span className="text-sm font-bold text-primary uppercase tracking-wider">Newsletter</span>
              </div>
              <h3 className="text-2xl md:text-3xl font-display font-bold text-white mb-2 tracking-tight">
                Stay ahead of the best deals
              </h3>
              <p className="text-slate-400">
                Join 50,000+ smart shoppers saving money every day.
              </p>
            </div>
            <div className="flex w-full max-w-md items-center gap-3">
              <div className="relative flex-1">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-500" />
                <Input
                  type="email"
                  placeholder="Enter your email"
                  className="bg-slate-950/50 border-white/10 text-white placeholder:text-slate-500 focus-visible:ring-primary h-12 rounded-full pl-12"
                />
              </div>
              <Button size="lg" className="h-12 px-6 font-semibold rounded-full bg-primary text-primary-foreground hover:bg-primary/90 shadow-lg shadow-primary/20 transition-all">
                Subscribe
              </Button>
            </div>
          </div>
        </div>

        {/* Main Footer Content */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-12 gap-10 lg:gap-8 mb-12">
          {/* Brand Section */}
          <div className="lg:col-span-5 space-y-5">
            <Link href={`/${locale}`} className="inline-block">
              <h2 className="text-2xl font-display font-bold text-white tracking-tighter flex items-center gap-2">
                Ecommica
                <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
              </h2>
            </Link>
            <p className="text-slate-400 leading-relaxed text-sm max-w-md">
              Your ultimate destination for verified coupon codes, exclusive deals, and smart shopping guides. Save more on brands you love.
            </p>
            {/* Social Links */}
            <div className="flex items-center gap-3 pt-2">
              {socialLinks.map(({ Icon, label, href }) => (
                <Link
                  key={label}
                  href={href}
                  className="w-10 h-10 rounded-full bg-white/5 flex items-center justify-center text-slate-400 hover:text-primary hover:bg-primary/10 hover:scale-105 transition-all duration-300 border border-white/5"
                >
                  <Icon className="h-4 w-4" />
                  <span className="sr-only">{label}</span>
                </Link>
              ))}
            </div>
          </div>

          {/* Link Columns */}
          {[
            { title: 'Quick Links', links: quickLinks, span: 'lg:col-span-2' },
            { title: 'Support', links: supportLinks, span: 'lg:col-span-2' },
            { title: 'Legal', links: legalLinks, span: 'lg:col-span-3' },
          ].map((section) => (
            <div key={section.title} className={section.span}>
              <h3 className="text-white font-semibold mb-5 text-sm uppercase tracking-wider">
                {section.title}
              </h3>
              <ul className="space-y-3">
                {section.links.map((item) => (
                  <li key={item.label}>
                    <Link
                      href={item.href}
                      className="text-slate-400 hover:text-primary transition-colors text-sm inline-flex items-center group"
                    >
                      <span className="w-0 group-hover:w-2 h-px bg-primary mr-0 group-hover:mr-2 transition-all duration-200" />
                      {item.label}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-white/5 pt-8 flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-sm text-slate-500">
            &copy; {new Date().getFullYear()} Ecommica. All rights reserved.
          </p>
          <p className="text-sm text-slate-600">
            Made with <span className="text-red-500">♥</span> for smart shoppers
          </p>
        </div>
      </div>
    </footer>
  )
}
