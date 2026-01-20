# Ecommica Footer Pages Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Create 6 missing footer pages (About, Contact, FAQ, Privacy Policy, Terms of Service, Cookie Policy) for the river-ecommica project.

**Architecture:** Static pages using Next.js App Router with Server Components. Shared PageHero component for consistent headers. LegalPageLayout component for the 3 legal pages with sticky sidebar navigation. FAQ uses Accordion component from shadcn/ui.

**Tech Stack:** Next.js 16, React 19, Tailwind CSS 4, shadcn/ui, Lucide React, next-intl

---

## Task 1: Install Accordion Component

**Files:**
- Create: `river-ecommica/src/components/ui/accordion.tsx`

**Step 1: Install Accordion from shadcn/ui**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm dlx shadcn@latest add accordion
```

Expected: Component installed successfully, accordion.tsx created

**Step 2: Verify installation**

Run:
```bash
ls -la /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/components/ui/accordion.tsx
```

Expected: File exists

---

## Task 2: Add i18n Messages for Footer Pages

**Files:**
- Modify: `river-ecommica/src/messages/en.json`
- Modify: `river-ecommica/src/messages/zh.json`

**Step 1: Update en.json with new translations**

Add the following sections to `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/messages/en.json`:

```json
{
  "about": {
    "meta": {
      "title": "About Us - Ecommica",
      "description": "Learn about Ecommica's mission to help shoppers save money with verified deals and coupons"
    },
    "heroTitle": "Your Savings, Our Mission",
    "heroSubtitle": "We're dedicated to helping shoppers find the best deals and save money on every purchase.",
    "storyTitle": "Our Story",
    "storyContent": "Ecommica was founded with a simple mission: make saving money effortless. We believe everyone deserves access to the best deals without spending hours searching. Our team works around the clock to verify coupons, curate exclusive offers, and bring you savings from thousands of trusted brands.",
    "statsUsers": "Happy Shoppers",
    "statsStores": "Partner Stores",
    "statsBrands": "Top Brands",
    "statsValid": "Code Success Rate",
    "valuesTitle": "What We Stand For",
    "valueTransparency": "Transparency",
    "valueTransparencyDesc": "Every deal and coupon is clearly labeled. No hidden terms, no misleading offers.",
    "valueQuality": "Quality",
    "valueQualityDesc": "We verify every code before publishing. If it doesn't work, it doesn't go live.",
    "valueTrust": "Trust",
    "valueTrustDesc": "Built on honest recommendations. We only partner with reputable brands."
  },
  "contact": {
    "meta": {
      "title": "Contact Us - Ecommica",
      "description": "Get in touch with the Ecommica team for support, partnerships, or feedback"
    },
    "heroTitle": "Get in Touch",
    "heroSubtitle": "We'd love to hear from you. Reach out for support, partnerships, or just to say hello.",
    "emailTitle": "Email Us",
    "emailAddress": "support@ecommica.com",
    "emailDesc": "For general inquiries and support",
    "socialTitle": "Follow Us",
    "socialDesc": "Stay updated with the latest deals",
    "infoTitle": "Quick Info",
    "responseTime": "Response within 24 hours",
    "businessHours": "Mon-Fri, 9AM-6PM EST",
    "faqCallout": "Looking for quick answers?",
    "faqCalloutDesc": "Check our FAQ for instant help with common questions.",
    "viewFaq": "View FAQ"
  },
  "faq": {
    "meta": {
      "title": "FAQ - Frequently Asked Questions",
      "description": "Find answers to common questions about using coupons, deals, and shopping on Ecommica"
    },
    "heroTitle": "Frequently Asked Questions",
    "heroSubtitle": "Everything you need to know about using Ecommica",
    "categoryUsing": "Using Coupons",
    "categoryDeals": "Deals & Offers",
    "categorySupport": "Account & Support",
    "q1": "How do I use a coupon code?",
    "a1": "Click the \"Get Code\" button on any coupon to reveal the code. Copy it, then paste it at checkout on the retailer's website. The discount will be applied to your order.",
    "q2": "Why isn't my coupon code working?",
    "a2": "Some codes have restrictions like minimum purchase amounts, specific products, or first-time customer only. Check the coupon details for any terms. If it still doesn't work, the code may have expired - we update our database frequently to remove invalid codes.",
    "q3": "Do coupons expire?",
    "a3": "Yes, most coupons have expiration dates shown on the coupon card. We display the expiry date when available and remove expired codes from our site.",
    "q4": "How are deals verified?",
    "a4": "Our team manually tests coupon codes and verifies deals before publishing. We also use automated systems to check code validity and update our database in real-time.",
    "q5": "How often are deals updated?",
    "a5": "We update our deals and coupons multiple times daily. Hot deals during sales events are added within minutes of going live.",
    "q6": "Can I submit a deal I found?",
    "a6": "Currently we don't have a public submission form, but you can email us at support@ecommica.com with deal suggestions. We appreciate community contributions!",
    "q7": "Do I need an account to use Ecommica?",
    "a7": "No account is needed! All deals and coupons are freely accessible. Simply browse, find a deal you like, and use it.",
    "q8": "How do I contact support?",
    "a8": "You can reach us at support@ecommica.com. We typically respond within 24 hours on business days.",
    "stillHaveQuestions": "Still have questions?",
    "contactUs": "Contact Us"
  },
  "legal": {
    "lastUpdated": "Last updated: {date}",
    "relatedPolicies": "Related Policies",
    "privacy": {
      "title": "Privacy Policy",
      "description": "How we collect, use, and protect your information"
    },
    "terms": {
      "title": "Terms of Service",
      "description": "Rules and guidelines for using Ecommica"
    },
    "cookies": {
      "title": "Cookie Policy",
      "description": "How we use cookies and similar technologies"
    }
  }
}
```

**Step 2: Update zh.json with Chinese translations**

Add corresponding Chinese translations to `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/messages/zh.json`.

**Step 3: Verify JSON syntax**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && node -e "require('./src/messages/en.json')" && echo "en.json OK"
```

Expected: "en.json OK"

---

## Task 3: Create PageHero Component

**Files:**
- Create: `river-ecommica/src/components/layout/PageHero.tsx`

**Step 1: Create the PageHero component**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/components/layout/PageHero.tsx`:

```tsx
import { type LucideIcon } from 'lucide-react';

interface PageHeroProps {
  icon?: LucideIcon;
  badge?: string;
  title: string;
  subtitle?: string;
  variant?: 'light' | 'dark';
  size?: 'default' | 'compact';
  lastUpdated?: string;
}

export function PageHero({
  icon: Icon,
  badge,
  title,
  subtitle,
  variant = 'light',
  size = 'default',
  lastUpdated,
}: PageHeroProps) {
  const isCompact = size === 'compact';
  const isDark = variant === 'dark';

  return (
    <section
      className={`relative overflow-hidden ${
        isDark
          ? 'bg-gradient-to-br from-slate-900 via-indigo-950/90 to-slate-900 text-white'
          : 'page-header'
      } ${isCompact ? 'py-10 md:py-14' : 'py-12 md:py-16'}`}
    >
      {/* Background decoration */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        {isDark ? (
          <>
            <div className="absolute -top-32 -left-32 w-[500px] h-[500px] bg-indigo-600/20 rounded-full blur-[100px]" />
            <div className="absolute top-1/4 -right-20 w-[400px] h-[400px] bg-violet-600/15 rounded-full blur-[80px]" />
          </>
        ) : (
          <>
            <div className="absolute -top-20 -right-20 w-96 h-96 bg-gradient-to-br from-indigo-200/30 to-violet-200/30 rounded-full blur-3xl" />
            <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-gradient-to-br from-amber-200/20 to-orange-200/20 rounded-full blur-3xl" />
          </>
        )}
      </div>

      <div className="container mx-auto px-4 relative">
        <div className="max-w-3xl">
          {/* Icon and Badge */}
          {(Icon || badge) && (
            <div className="flex items-center gap-3 mb-5">
              {Icon && (
                <div
                  className={`p-3 rounded-2xl ${
                    isDark
                      ? 'bg-white/10 backdrop-blur-sm'
                      : 'bg-gradient-to-br from-indigo-100 to-violet-100'
                  }`}
                >
                  <Icon
                    className={`w-8 h-8 ${isDark ? 'text-white' : 'text-indigo-600'}`}
                  />
                </div>
              )}
              {badge && (
                <span className="badge-featured">
                  {badge}
                </span>
              )}
            </div>
          )}

          {/* Title */}
          <h1
            className={`font-display font-bold tracking-tight mb-4 ${
              isCompact
                ? 'text-3xl md:text-4xl'
                : 'text-4xl md:text-5xl lg:text-6xl'
            } ${isDark ? 'text-white' : 'text-foreground'}`}
          >
            {title}
          </h1>

          {/* Subtitle */}
          {subtitle && (
            <p
              className={`text-lg md:text-xl leading-relaxed ${
                isDark ? 'text-slate-300' : 'text-muted-foreground'
              }`}
            >
              {subtitle}
            </p>
          )}

          {/* Last Updated */}
          {lastUpdated && (
            <p
              className={`mt-4 text-sm ${
                isDark ? 'text-slate-400' : 'text-muted-foreground'
              }`}
            >
              {lastUpdated}
            </p>
          )}
        </div>
      </div>
    </section>
  );
}
```

**Step 2: Verify component compiles**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | head -30
```

Expected: No TypeScript errors for PageHero.tsx

---

## Task 4: Create About Page

**Files:**
- Create: `river-ecommica/src/app/[locale]/about/page.tsx`

**Step 1: Create the About page**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/app/[locale]/about/page.tsx`:

```tsx
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { PageHero } from '@/components/layout/PageHero';
import {
  Heart,
  Users,
  Store,
  Award,
  CheckCircle,
  Shield,
  Sparkles,
  Eye,
  Target,
  Handshake,
} from 'lucide-react';

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string }>;
}): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'about' });

  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function AboutPage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'about' });

  const stats = [
    { value: '50K+', label: t('statsUsers'), icon: Users, color: 'from-blue-500 to-indigo-600' },
    { value: '10K+', label: t('statsStores'), icon: Store, color: 'from-violet-500 to-purple-600' },
    { value: '500+', label: t('statsBrands'), icon: Award, color: 'from-amber-500 to-orange-600' },
    { value: '99%', label: t('statsValid'), icon: CheckCircle, color: 'from-emerald-500 to-teal-600' },
  ];

  const values = [
    {
      icon: Eye,
      title: t('valueTransparency'),
      description: t('valueTransparencyDesc'),
      gradient: 'from-blue-500 to-indigo-600',
      shadow: 'shadow-blue-500/20',
    },
    {
      icon: Target,
      title: t('valueQuality'),
      description: t('valueQualityDesc'),
      gradient: 'from-emerald-500 to-teal-600',
      shadow: 'shadow-emerald-500/20',
    },
    {
      icon: Handshake,
      title: t('valueTrust'),
      description: t('valueTrustDesc'),
      gradient: 'from-violet-500 to-purple-600',
      shadow: 'shadow-violet-500/20',
    },
  ];

  return (
    <main className="min-h-screen bg-background">
      {/* Hero */}
      <PageHero
        icon={Heart}
        title={t('heroTitle')}
        subtitle={t('heroSubtitle')}
        variant="dark"
      />

      {/* Our Story */}
      <section className="py-16 lg:py-24">
        <div className="container mx-auto px-4">
          <div className="grid lg:grid-cols-2 gap-12 items-center">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <div className="p-2 rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 shadow-lg shadow-indigo-500/20">
                  <Sparkles className="w-5 h-5 text-white" />
                </div>
                <span className="text-sm font-semibold text-indigo-600 uppercase tracking-wider">
                  Our Story
                </span>
              </div>
              <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground mb-6">
                {t('storyTitle')}
              </h2>
              <p className="text-lg text-muted-foreground leading-relaxed">
                {t('storyContent')}
              </p>
            </div>
            <div className="relative">
              <div className="absolute inset-0 bg-gradient-to-br from-indigo-500/10 via-violet-500/10 to-amber-500/10 rounded-3xl blur-2xl" />
              <div className="relative bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 rounded-3xl p-8 lg:p-12">
                <div className="grid grid-cols-2 gap-6">
                  {stats.map((stat) => (
                    <div key={stat.label} className="text-center">
                      <div
                        className={`inline-flex p-3 rounded-xl bg-gradient-to-br ${stat.color} shadow-lg mb-3`}
                      >
                        <stat.icon className="w-6 h-6 text-white" />
                      </div>
                      <div className="text-3xl lg:text-4xl font-display font-bold text-white mb-1">
                        {stat.value}
                      </div>
                      <div className="text-sm text-slate-400">{stat.label}</div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Values */}
      <section className="py-16 lg:py-24 bg-gradient-to-b from-slate-50/50 via-slate-100/50 to-slate-50/50 dark:from-slate-900/50 dark:via-slate-800/30 dark:to-slate-900/50">
        <div className="container mx-auto px-4">
          <div className="text-center max-w-2xl mx-auto mb-14">
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-primary/5 border border-primary/10 text-primary text-sm font-medium mb-6">
              <Shield className="w-4 h-4" />
              <span>Our Values</span>
            </div>
            <h2 className="text-3xl lg:text-4xl font-display font-bold text-foreground">
              {t('valuesTitle')}
            </h2>
          </div>

          <div className="grid md:grid-cols-3 gap-6 lg:gap-8">
            {values.map((value, index) => (
              <div
                key={value.title}
                className="group relative p-8 rounded-2xl bg-card border border-border/50 hover:border-border hover:shadow-xl transition-all duration-300"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div
                  className={`inline-flex p-3.5 rounded-xl bg-gradient-to-br ${value.gradient} ${value.shadow} shadow-lg mb-6`}
                >
                  <value.icon className="w-6 h-6 text-white" />
                </div>
                <h3 className="text-xl font-display font-bold text-foreground mb-3">
                  {value.title}
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  {value.description}
                </p>
                <div
                  className={`absolute inset-0 rounded-2xl bg-gradient-to-br ${value.gradient} opacity-0 group-hover:opacity-[0.02] transition-opacity duration-300`}
                />
              </div>
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}
```

**Step 2: Verify page compiles and renders**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | grep -E "(error|About)" | head -10
```

Expected: No errors, About page compiled

---

## Task 5: Create Contact Page

**Files:**
- Create: `river-ecommica/src/app/[locale]/contact/page.tsx`

**Step 1: Create the Contact page**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/app/[locale]/contact/page.tsx`:

```tsx
import { Metadata } from 'next';
import Link from 'next/link';
import { getTranslations } from 'next-intl/server';
import { PageHero } from '@/components/layout/PageHero';
import { Button } from '@/components/ui/button';
import {
  Mail,
  MessageCircle,
  Clock,
  Twitter,
  Facebook,
  Instagram,
  Linkedin,
  ArrowRight,
  HelpCircle,
} from 'lucide-react';

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string }>;
}): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'contact' });

  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function ContactPage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'contact' });

  const socialLinks = [
    { icon: Twitter, label: 'Twitter', href: '#' },
    { icon: Facebook, label: 'Facebook', href: '#' },
    { icon: Instagram, label: 'Instagram', href: '#' },
    { icon: Linkedin, label: 'LinkedIn', href: '#' },
  ];

  return (
    <main className="min-h-screen bg-background">
      {/* Hero */}
      <PageHero
        icon={Mail}
        title={t('heroTitle')}
        subtitle={t('heroSubtitle')}
        variant="light"
        size="compact"
      />

      {/* Contact Cards */}
      <section className="py-16 lg:py-24">
        <div className="container mx-auto px-4">
          <div className="grid md:grid-cols-3 gap-6 lg:gap-8 max-w-5xl mx-auto">
            {/* Email Card */}
            <div className="group relative p-8 rounded-2xl bg-card border border-border/50 hover:border-emerald-500/30 hover:shadow-xl hover:shadow-emerald-500/5 transition-all duration-300 hover:-translate-y-1">
              <div className="inline-flex p-3.5 rounded-xl bg-gradient-to-br from-emerald-500 to-teal-600 shadow-lg shadow-emerald-500/20 mb-6">
                <Mail className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-xl font-display font-bold text-foreground mb-2">
                {t('emailTitle')}
              </h3>
              <p className="text-muted-foreground text-sm mb-4">
                {t('emailDesc')}
              </p>
              <a
                href="mailto:support@ecommica.com"
                className="text-emerald-600 dark:text-emerald-400 font-semibold hover:underline"
              >
                {t('emailAddress')}
              </a>
            </div>

            {/* Social Card */}
            <div className="group relative p-8 rounded-2xl bg-card border border-border/50 hover:border-violet-500/30 hover:shadow-xl hover:shadow-violet-500/5 transition-all duration-300 hover:-translate-y-1">
              <div className="inline-flex p-3.5 rounded-xl bg-gradient-to-br from-violet-500 to-purple-600 shadow-lg shadow-violet-500/20 mb-6">
                <MessageCircle className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-xl font-display font-bold text-foreground mb-2">
                {t('socialTitle')}
              </h3>
              <p className="text-muted-foreground text-sm mb-4">
                {t('socialDesc')}
              </p>
              <div className="flex gap-3">
                {socialLinks.map(({ icon: Icon, label, href }) => (
                  <a
                    key={label}
                    href={href}
                    className="w-10 h-10 rounded-full bg-muted flex items-center justify-center text-muted-foreground hover:text-violet-600 hover:bg-violet-100 dark:hover:bg-violet-900/30 transition-colors"
                    aria-label={label}
                  >
                    <Icon className="w-5 h-5" />
                  </a>
                ))}
              </div>
            </div>

            {/* Info Card */}
            <div className="group relative p-8 rounded-2xl bg-card border border-border/50 hover:border-amber-500/30 hover:shadow-xl hover:shadow-amber-500/5 transition-all duration-300 hover:-translate-y-1">
              <div className="inline-flex p-3.5 rounded-xl bg-gradient-to-br from-amber-500 to-orange-600 shadow-lg shadow-amber-500/20 mb-6">
                <Clock className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-xl font-display font-bold text-foreground mb-2">
                {t('infoTitle')}
              </h3>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li className="flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
                  {t('responseTime')}
                </li>
                <li className="flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-500" />
                  {t('businessHours')}
                </li>
              </ul>
            </div>
          </div>

          {/* FAQ Callout */}
          <div className="mt-16 max-w-2xl mx-auto">
            <div className="p-8 rounded-2xl border-2 border-dashed border-border bg-muted/30 text-center">
              <div className="inline-flex p-3 rounded-xl bg-primary/10 mb-4">
                <HelpCircle className="w-6 h-6 text-primary" />
              </div>
              <h3 className="text-xl font-display font-bold text-foreground mb-2">
                {t('faqCallout')}
              </h3>
              <p className="text-muted-foreground mb-6">
                {t('faqCalloutDesc')}
              </p>
              <Button asChild>
                <Link href={`/${locale}/faq`}>
                  {t('viewFaq')}
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
```

**Step 2: Verify page compiles**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | grep -E "(error|contact)" | head -10
```

Expected: No errors

---

## Task 6: Create FAQ Page

**Files:**
- Create: `river-ecommica/src/app/[locale]/faq/page.tsx`

**Step 1: Create the FAQ page**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/app/[locale]/faq/page.tsx`:

```tsx
import { Metadata } from 'next';
import Link from 'next/link';
import { getTranslations } from 'next-intl/server';
import { PageHero } from '@/components/layout/PageHero';
import { Button } from '@/components/ui/button';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import {
  HelpCircle,
  Tag,
  Gift,
  HeadphonesIcon,
  ArrowRight,
  Mail,
} from 'lucide-react';

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string }>;
}): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'faq' });

  return {
    title: t('meta.title'),
    description: t('meta.description'),
  };
}

export default async function FaqPage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'faq' });

  const faqCategories = [
    {
      id: 'using-coupons',
      title: t('categoryUsing'),
      icon: Tag,
      color: 'from-emerald-500 to-teal-600',
      borderColor: 'border-l-emerald-500',
      questions: [
        { q: t('q1'), a: t('a1') },
        { q: t('q2'), a: t('a2') },
        { q: t('q3'), a: t('a3') },
      ],
    },
    {
      id: 'deals-offers',
      title: t('categoryDeals'),
      icon: Gift,
      color: 'from-violet-500 to-purple-600',
      borderColor: 'border-l-violet-500',
      questions: [
        { q: t('q4'), a: t('a4') },
        { q: t('q5'), a: t('a5') },
        { q: t('q6'), a: t('a6') },
      ],
    },
    {
      id: 'support',
      title: t('categorySupport'),
      icon: HeadphonesIcon,
      color: 'from-amber-500 to-orange-600',
      borderColor: 'border-l-amber-500',
      questions: [
        { q: t('q7'), a: t('a7') },
        { q: t('q8'), a: t('a8') },
      ],
    },
  ];

  return (
    <main className="min-h-screen bg-background">
      {/* Hero */}
      <PageHero
        icon={HelpCircle}
        title={t('heroTitle')}
        subtitle={t('heroSubtitle')}
        variant="light"
      />

      {/* FAQ Sections */}
      <section className="py-16 lg:py-24">
        <div className="container mx-auto px-4">
          <div className="max-w-3xl mx-auto space-y-10">
            {faqCategories.map((category) => (
              <div key={category.id}>
                {/* Category Header */}
                <div className="flex items-center gap-3 mb-6">
                  <div
                    className={`p-2.5 rounded-xl bg-gradient-to-br ${category.color} shadow-lg`}
                  >
                    <category.icon className="w-5 h-5 text-white" />
                  </div>
                  <h2 className="text-xl font-display font-bold text-foreground">
                    {category.title}
                  </h2>
                </div>

                {/* Accordion */}
                <div
                  className={`border-l-4 ${category.borderColor} pl-6 rounded-r-2xl bg-card border border-l-0 border-border/50`}
                >
                  <Accordion type="single" collapsible className="w-full">
                    {category.questions.map((item, index) => (
                      <AccordionItem
                        key={index}
                        value={`${category.id}-${index}`}
                        className="border-b border-border/50 last:border-0"
                      >
                        <AccordionTrigger className="text-left font-semibold text-foreground hover:text-primary py-5 px-4">
                          {item.q}
                        </AccordionTrigger>
                        <AccordionContent className="text-muted-foreground leading-relaxed px-4 pb-5">
                          {item.a}
                        </AccordionContent>
                      </AccordionItem>
                    ))}
                  </Accordion>
                </div>
              </div>
            ))}
          </div>

          {/* Contact CTA */}
          <div className="mt-16 max-w-xl mx-auto">
            <div className="p-8 rounded-2xl bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 text-center">
              <div className="inline-flex p-3 rounded-xl bg-white/10 backdrop-blur-sm mb-4">
                <Mail className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-xl font-display font-bold text-white mb-2">
                {t('stillHaveQuestions')}
              </h3>
              <p className="text-slate-300 mb-6">
                Our team is here to help you.
              </p>
              <Button
                asChild
                className="bg-white text-slate-900 hover:bg-slate-100"
              >
                <Link href={`/${locale}/contact`}>
                  {t('contactUs')}
                  <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
```

**Step 2: Verify page compiles**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | grep -E "(error|faq)" | head -10
```

Expected: No errors

---

## Task 7: Create LegalPageLayout Component

**Files:**
- Create: `river-ecommica/src/components/layout/LegalPageLayout.tsx`

**Step 1: Create the LegalPageLayout component**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/components/layout/LegalPageLayout.tsx`:

```tsx
'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { FileText, Shield, Cookie } from 'lucide-react';

interface Section {
  id: string;
  title: string;
}

interface LegalPageLayoutProps {
  children: React.ReactNode;
  sections: Section[];
  locale: string;
}

export function LegalPageLayout({
  children,
  sections,
  locale,
}: LegalPageLayoutProps) {
  const pathname = usePathname();

  const relatedPages = [
    {
      href: `/${locale}/privacy-policy`,
      label: 'Privacy Policy',
      icon: Shield,
    },
    {
      href: `/${locale}/terms-of-service`,
      label: 'Terms of Service',
      icon: FileText,
    },
    {
      href: `/${locale}/cookie-policy`,
      label: 'Cookie Policy',
      icon: Cookie,
    },
  ];

  return (
    <div className="container mx-auto px-4 py-12 lg:py-16">
      <div className="lg:grid lg:grid-cols-[240px_1fr] lg:gap-12">
        {/* Sidebar */}
        <aside className="hidden lg:block">
          <nav className="sticky top-24 space-y-6">
            {/* Table of Contents */}
            <div>
              <h3 className="text-sm font-semibold text-foreground mb-4 uppercase tracking-wider">
                On This Page
              </h3>
              <ul className="space-y-2">
                {sections.map((section) => (
                  <li key={section.id}>
                    <a
                      href={`#${section.id}`}
                      className="block text-sm text-muted-foreground hover:text-primary transition-colors py-1 border-l-2 border-transparent hover:border-primary pl-3 -ml-px"
                    >
                      {section.title}
                    </a>
                  </li>
                ))}
              </ul>
            </div>

            {/* Related Policies */}
            <div className="pt-6 border-t border-border">
              <h3 className="text-sm font-semibold text-foreground mb-4 uppercase tracking-wider">
                Related Policies
              </h3>
              <ul className="space-y-2">
                {relatedPages
                  .filter((page) => !pathname.includes(page.href.split('/').pop() || ''))
                  .map((page) => (
                    <li key={page.href}>
                      <Link
                        href={page.href}
                        className="flex items-center gap-2 text-sm text-muted-foreground hover:text-primary transition-colors py-1"
                      >
                        <page.icon className="w-4 h-4" />
                        {page.label}
                      </Link>
                    </li>
                  ))}
              </ul>
            </div>
          </nav>
        </aside>

        {/* Mobile TOC */}
        <div className="lg:hidden mb-8 overflow-x-auto">
          <div className="flex gap-2 pb-2">
            {sections.map((section) => (
              <a
                key={section.id}
                href={`#${section.id}`}
                className="flex-shrink-0 px-4 py-2 text-sm font-medium text-muted-foreground bg-muted rounded-full hover:text-primary hover:bg-primary/10 transition-colors"
              >
                {section.title}
              </a>
            ))}
          </div>
        </div>

        {/* Content */}
        <article className="prose prose-slate dark:prose-invert max-w-none prose-headings:font-display prose-headings:scroll-mt-24 prose-h2:text-2xl prose-h2:mt-12 prose-h2:mb-6 prose-p:leading-relaxed prose-li:leading-relaxed">
          {children}
        </article>
      </div>

      {/* Mobile Related Policies */}
      <div className="lg:hidden mt-12 pt-8 border-t border-border">
        <h3 className="text-sm font-semibold text-foreground mb-4 uppercase tracking-wider">
          Related Policies
        </h3>
        <div className="flex flex-wrap gap-3">
          {relatedPages
            .filter((page) => !pathname.includes(page.href.split('/').pop() || ''))
            .map((page) => (
              <Link
                key={page.href}
                href={page.href}
                className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-muted-foreground bg-muted rounded-full hover:text-primary hover:bg-primary/10 transition-colors"
              >
                <page.icon className="w-4 h-4" />
                {page.label}
              </Link>
            ))}
        </div>
      </div>
    </div>
  );
}
```

**Step 2: Verify component compiles**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | grep -E "error" | head -5
```

Expected: No errors

---

## Task 8: Create Privacy Policy Page

**Files:**
- Create: `river-ecommica/src/app/[locale]/privacy-policy/page.tsx`

**Step 1: Create the Privacy Policy page**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/app/[locale]/privacy-policy/page.tsx`:

```tsx
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { PageHero } from '@/components/layout/PageHero';
import { LegalPageLayout } from '@/components/layout/LegalPageLayout';
import { Shield } from 'lucide-react';

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string }>;
}): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });

  return {
    title: t('privacy.title'),
    description: t('privacy.description'),
  };
}

export default async function PrivacyPolicyPage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });

  const sections = [
    { id: 'introduction', title: 'Introduction' },
    { id: 'data-collection', title: 'Data We Collect' },
    { id: 'data-usage', title: 'How We Use Your Data' },
    { id: 'third-parties', title: 'Third-Party Sharing' },
    { id: 'your-rights', title: 'Your Rights' },
    { id: 'contact', title: 'Contact Us' },
  ];

  return (
    <main className="min-h-screen bg-background">
      <PageHero
        icon={Shield}
        title={t('privacy.title')}
        subtitle={t('privacy.description')}
        variant="light"
        size="compact"
        lastUpdated={t('lastUpdated', { date: 'January 2026' })}
      />

      <LegalPageLayout sections={sections} locale={locale}>
        <section id="introduction">
          <h2>
            <span className="text-primary font-mono mr-2">1.</span>
            Introduction
          </h2>
          <p>
            At Ecommica, we respect your privacy and are committed to protecting your personal data.
            This Privacy Policy explains how we collect, use, and safeguard your information when
            you visit our website at deals.ecommica.com.
          </p>
          <p>
            By using our service, you agree to the collection and use of information in accordance
            with this policy. We do not sell your personal information to third parties.
          </p>
        </section>

        <section id="data-collection">
          <h2>
            <span className="text-primary font-mono mr-2">2.</span>
            Data We Collect
          </h2>
          <p>We collect information in the following ways:</p>
          <ul>
            <li>
              <strong>Automatically Collected Data:</strong> When you visit our site, we may collect
              technical information such as your IP address, browser type, device information, and
              pages visited. This is collected through cookies and similar technologies.
            </li>
            <li>
              <strong>Information You Provide:</strong> If you subscribe to our newsletter or contact
              us, we collect your email address and any information you voluntarily provide.
            </li>
            <li>
              <strong>Affiliate Tracking:</strong> When you click on deals or coupons that redirect
              to merchant websites, affiliate tracking cookies may be set to attribute the referral
              to Ecommica. This allows us to earn commissions that keep our service free.
            </li>
          </ul>
        </section>

        <section id="data-usage">
          <h2>
            <span className="text-primary font-mono mr-2">3.</span>
            How We Use Your Data
          </h2>
          <p>We use the collected data for the following purposes:</p>
          <ul>
            <li>To provide and maintain our service</li>
            <li>To improve and personalize your experience</li>
            <li>To send newsletters if you have subscribed</li>
            <li>To analyze usage patterns and optimize our website</li>
            <li>To track affiliate referrals and earn commissions</li>
            <li>To detect and prevent fraud or abuse</li>
          </ul>
        </section>

        <section id="third-parties">
          <h2>
            <span className="text-primary font-mono mr-2">4.</span>
            Third-Party Sharing
          </h2>
          <p>
            We work with affiliate networks and merchant partners to provide deals and coupons.
            When you click on an affiliate link, you may be redirected through tracking services
            that set cookies on your device. These third parties include:
          </p>
          <ul>
            <li>Affiliate networks (e.g., ShareASale, CJ Affiliate, Impact)</li>
            <li>Analytics providers (e.g., Google Analytics)</li>
            <li>Advertising partners for retargeting purposes</li>
          </ul>
          <p>
            We do not sell your personal information. Third-party services have their own privacy
            policies governing the use of your information.
          </p>
        </section>

        <section id="your-rights">
          <h2>
            <span className="text-primary font-mono mr-2">5.</span>
            Your Rights
          </h2>
          <p>Depending on your location, you may have the following rights:</p>
          <ul>
            <li>Access the personal data we hold about you</li>
            <li>Request correction of inaccurate data</li>
            <li>Request deletion of your data</li>
            <li>Opt out of marketing communications</li>
            <li>Disable cookies through your browser settings</li>
          </ul>
          <p>
            To exercise these rights, please contact us at the email address provided below.
          </p>
        </section>

        <section id="contact">
          <h2>
            <span className="text-primary font-mono mr-2">6.</span>
            Contact Us
          </h2>
          <p>
            If you have questions about this Privacy Policy or wish to exercise your rights,
            please contact us at:
          </p>
          <p>
            <strong>Email:</strong> privacy@ecommica.com
          </p>
        </section>
      </LegalPageLayout>
    </main>
  );
}
```

**Step 2: Verify page compiles**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | grep -E "error" | head -5
```

Expected: No errors

---

## Task 9: Create Terms of Service Page

**Files:**
- Create: `river-ecommica/src/app/[locale]/terms-of-service/page.tsx`

**Step 1: Create the Terms of Service page**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/app/[locale]/terms-of-service/page.tsx`:

```tsx
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { PageHero } from '@/components/layout/PageHero';
import { LegalPageLayout } from '@/components/layout/LegalPageLayout';
import { FileText } from 'lucide-react';

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string }>;
}): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });

  return {
    title: t('terms.title'),
    description: t('terms.description'),
  };
}

export default async function TermsOfServicePage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });

  const sections = [
    { id: 'acceptance', title: 'Acceptance of Terms' },
    { id: 'service', title: 'Service Description' },
    { id: 'user-conduct', title: 'User Conduct' },
    { id: 'disclaimers', title: 'Disclaimers' },
    { id: 'intellectual-property', title: 'Intellectual Property' },
    { id: 'termination', title: 'Termination' },
    { id: 'contact', title: 'Contact Us' },
  ];

  return (
    <main className="min-h-screen bg-background">
      <PageHero
        icon={FileText}
        title={t('terms.title')}
        subtitle={t('terms.description')}
        variant="light"
        size="compact"
        lastUpdated={t('lastUpdated', { date: 'January 2026' })}
      />

      <LegalPageLayout sections={sections} locale={locale}>
        <section id="acceptance">
          <h2>
            <span className="text-primary font-mono mr-2">1.</span>
            Acceptance of Terms
          </h2>
          <p>
            By accessing and using Ecommica (deals.ecommica.com), you agree to be bound by these
            Terms of Service. If you do not agree to these terms, please do not use our service.
          </p>
          <p>
            We reserve the right to modify these terms at any time. Continued use of the service
            after changes constitutes acceptance of the modified terms.
          </p>
        </section>

        <section id="service">
          <h2>
            <span className="text-primary font-mono mr-2">2.</span>
            Service Description
          </h2>
          <p>
            Ecommica is a deal and coupon aggregation platform. We provide:
          </p>
          <ul>
            <li>Curated deals and discounts from various retailers</li>
            <li>Coupon codes that can be used at checkout</li>
            <li>Links to merchant websites where purchases can be made</li>
            <li>Blog content with shopping tips and guides</li>
          </ul>
          <p>
            We are an affiliate marketing platform. When you click on deals or coupons and make
            purchases, we may earn a commission from the merchant at no additional cost to you.
          </p>
        </section>

        <section id="user-conduct">
          <h2>
            <span className="text-primary font-mono mr-2">3.</span>
            User Conduct
          </h2>
          <p>When using our service, you agree not to:</p>
          <ul>
            <li>Use the service for any unlawful purpose</li>
            <li>Attempt to gain unauthorized access to our systems</li>
            <li>Scrape, copy, or reproduce our content without permission</li>
            <li>Submit false or misleading information</li>
            <li>Interfere with the proper functioning of the service</li>
            <li>Use automated systems to access the service in a manner that exceeds reasonable use</li>
          </ul>
        </section>

        <section id="disclaimers">
          <h2>
            <span className="text-primary font-mono mr-2">4.</span>
            Disclaimers
          </h2>
          <p>
            <strong>Deal Accuracy:</strong> While we strive to provide accurate and up-to-date
            information, we cannot guarantee that all deals, prices, and coupon codes are current
            or valid. Prices and availability are subject to change by merchants without notice.
          </p>
          <p>
            <strong>Third-Party Websites:</strong> We are not responsible for the content, products,
            services, or practices of third-party merchant websites. Your interactions with merchants
            are solely between you and the merchant.
          </p>
          <p>
            <strong>No Warranty:</strong> The service is provided "as is" without warranties of any
            kind, either express or implied. We do not guarantee uninterrupted or error-free service.
          </p>
        </section>

        <section id="intellectual-property">
          <h2>
            <span className="text-primary font-mono mr-2">5.</span>
            Intellectual Property
          </h2>
          <p>
            All content on Ecommica, including text, graphics, logos, and software, is the property
            of Ecommica or its content suppliers and is protected by intellectual property laws.
          </p>
          <p>
            Merchant logos and brand names are trademarks of their respective owners and are used
            for identification purposes only. Their use does not imply endorsement.
          </p>
        </section>

        <section id="termination">
          <h2>
            <span className="text-primary font-mono mr-2">6.</span>
            Termination
          </h2>
          <p>
            We reserve the right to terminate or suspend access to our service at any time,
            without prior notice, for conduct that we believe violates these Terms of Service
            or is harmful to other users, us, or third parties.
          </p>
        </section>

        <section id="contact">
          <h2>
            <span className="text-primary font-mono mr-2">7.</span>
            Contact Us
          </h2>
          <p>
            If you have questions about these Terms of Service, please contact us at:
          </p>
          <p>
            <strong>Email:</strong> legal@ecommica.com
          </p>
        </section>
      </LegalPageLayout>
    </main>
  );
}
```

**Step 2: Verify page compiles**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | grep -E "error" | head -5
```

Expected: No errors

---

## Task 10: Create Cookie Policy Page

**Files:**
- Create: `river-ecommica/src/app/[locale]/cookie-policy/page.tsx`

**Step 1: Create the Cookie Policy page**

Create file at `/Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica/src/app/[locale]/cookie-policy/page.tsx`:

```tsx
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { PageHero } from '@/components/layout/PageHero';
import { LegalPageLayout } from '@/components/layout/LegalPageLayout';
import { Cookie } from 'lucide-react';

export async function generateMetadata({
  params,
}: {
  params: Promise<{ locale: string }>;
}): Promise<Metadata> {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });

  return {
    title: t('cookies.title'),
    description: t('cookies.description'),
  };
}

export default async function CookiePolicyPage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  const t = await getTranslations({ locale, namespace: 'legal' });

  const sections = [
    { id: 'what-are-cookies', title: 'What Are Cookies' },
    { id: 'types-of-cookies', title: 'Types of Cookies' },
    { id: 'how-we-use', title: 'How We Use Cookies' },
    { id: 'third-party', title: 'Third-Party Cookies' },
    { id: 'managing-cookies', title: 'Managing Cookies' },
    { id: 'contact', title: 'Contact Us' },
  ];

  return (
    <main className="min-h-screen bg-background">
      <PageHero
        icon={Cookie}
        title={t('cookies.title')}
        subtitle={t('cookies.description')}
        variant="light"
        size="compact"
        lastUpdated={t('lastUpdated', { date: 'January 2026' })}
      />

      <LegalPageLayout sections={sections} locale={locale}>
        <section id="what-are-cookies">
          <h2>
            <span className="text-primary font-mono mr-2">1.</span>
            What Are Cookies
          </h2>
          <p>
            Cookies are small text files that are placed on your device when you visit a website.
            They are widely used to make websites work more efficiently and provide information
            to website owners.
          </p>
          <p>
            Similar technologies include web beacons, pixels, and local storage. In this policy,
            we refer to all these technologies collectively as "cookies."
          </p>
        </section>

        <section id="types-of-cookies">
          <h2>
            <span className="text-primary font-mono mr-2">2.</span>
            Types of Cookies We Use
          </h2>
          <p>We use the following categories of cookies:</p>
          <ul>
            <li>
              <strong>Essential Cookies:</strong> Required for the website to function properly.
              These cannot be disabled.
            </li>
            <li>
              <strong>Analytics Cookies:</strong> Help us understand how visitors interact with
              our website by collecting and reporting information anonymously.
            </li>
            <li>
              <strong>Affiliate Cookies:</strong> Track clicks on deals and coupons to attribute
              referrals to our platform. This allows us to earn commissions and keep the service free.
            </li>
            <li>
              <strong>Preference Cookies:</strong> Remember your settings and preferences, such as
              language selection.
            </li>
          </ul>
        </section>

        <section id="how-we-use">
          <h2>
            <span className="text-primary font-mono mr-2">3.</span>
            How We Use Cookies
          </h2>
          <p>We use cookies to:</p>
          <ul>
            <li>Keep track of your preferences and settings</li>
            <li>Analyze website traffic and user behavior</li>
            <li>Track affiliate link clicks for commission purposes</li>
            <li>Improve website performance and user experience</li>
            <li>Remember your language preference</li>
          </ul>
        </section>

        <section id="third-party">
          <h2>
            <span className="text-primary font-mono mr-2">4.</span>
            Third-Party Cookies
          </h2>
          <p>
            Some cookies are placed by third-party services that appear on our pages. We use:
          </p>
          <ul>
            <li>
              <strong>Google Analytics:</strong> To analyze website usage and improve our service.
            </li>
            <li>
              <strong>Affiliate Networks:</strong> ShareASale, CJ Affiliate, Impact, and other
              networks set cookies to track referrals when you click on deals.
            </li>
            <li>
              <strong>Social Media:</strong> If you share content to social platforms, those
              platforms may set their own cookies.
            </li>
          </ul>
          <p>
            These third parties have their own privacy and cookie policies. We encourage you to
            review their policies.
          </p>
        </section>

        <section id="managing-cookies">
          <h2>
            <span className="text-primary font-mono mr-2">5.</span>
            Managing Cookies
          </h2>
          <p>
            You can control and manage cookies in various ways. Most browsers allow you to:
          </p>
          <ul>
            <li>View what cookies are stored and delete them individually</li>
            <li>Block third-party cookies</li>
            <li>Block cookies from specific sites</li>
            <li>Block all cookies</li>
            <li>Delete all cookies when you close your browser</li>
          </ul>
          <p>
            Please note that blocking all cookies may impact your experience on our website and
            limit certain functionality.
          </p>
          <p>
            To opt out of Google Analytics tracking, you can install the{' '}
            <a
              href="https://tools.google.com/dlpage/gaoptout"
              target="_blank"
              rel="noopener noreferrer"
              className="text-primary hover:underline"
            >
              Google Analytics Opt-out Browser Add-on
            </a>
            .
          </p>
        </section>

        <section id="contact">
          <h2>
            <span className="text-primary font-mono mr-2">6.</span>
            Contact Us
          </h2>
          <p>
            If you have questions about our use of cookies, please contact us at:
          </p>
          <p>
            <strong>Email:</strong> privacy@ecommica.com
          </p>
        </section>
      </LegalPageLayout>
    </main>
  );
}
```

**Step 2: Verify page compiles**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build 2>&1 | grep -E "error" | head -5
```

Expected: No errors

---

## Task 11: Final Build Verification and Manual Testing

**Step 1: Run full build**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm build
```

Expected: Build completes successfully with all 6 new pages compiled

**Step 2: Start dev server and verify pages**

Run:
```bash
cd /Users/apple/Projects/shixiaohe/river-ad-workspace/river-ecommica && pnpm dev
```

Manually verify each page:
- http://localhost:3000/en/about
- http://localhost:3000/en/contact
- http://localhost:3000/en/faq
- http://localhost:3000/en/privacy-policy
- http://localhost:3000/en/terms-of-service
- http://localhost:3000/en/cookie-policy

Expected: All pages render correctly with proper styling

**Step 3: Verify footer links work**

Navigate to any page and click footer links to verify navigation works.

---

## Summary

| Task | Description | Files |
|------|-------------|-------|
| 1 | Install Accordion | 1 file |
| 2 | Add i18n messages | 2 files |
| 3 | Create PageHero | 1 file |
| 4 | Create About page | 1 file |
| 5 | Create Contact page | 1 file |
| 6 | Create FAQ page | 1 file |
| 7 | Create LegalPageLayout | 1 file |
| 8 | Create Privacy Policy | 1 file |
| 9 | Create Terms of Service | 1 file |
| 10 | Create Cookie Policy | 1 file |
| 11 | Final verification | - |

**Total: 11 files created/modified**
