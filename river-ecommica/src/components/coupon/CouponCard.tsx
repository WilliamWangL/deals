'use client';

import { useState } from 'react';
import { Coupon } from '@/types';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Check, Copy, Clock, Scissors, ShoppingBag, ExternalLink } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useTranslations } from 'next-intl';

interface CouponCardProps {
  coupon: Coupon;
}

export default function CouponCard({ coupon }: CouponCardProps) {
  const [copied, setCopied] = useState(false);
  const [isHovering, setIsHovering] = useState(false);
  const t = useTranslations('Deal');

  const handleCopy = async () => {
    await navigator.clipboard.writeText(coupon.code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const getDiscountConfig = () => {
    const { discountType, discountValue } = coupon;

    if (discountType === 1) {
      if (discountValue >= 50) {
        return {
          main: `${discountValue}%`,
          sub: 'OFF',
          color: 'text-rose-600',
          bg: 'bg-rose-600',
          lightBg: 'bg-gradient-to-br from-rose-50 to-pink-50',
          border: 'border-rose-200/60',
          gradient: 'from-rose-500 to-pink-500'
        };
      }
      return {
        main: `${discountValue}%`,
        sub: 'OFF',
        color: 'text-primary',
        bg: 'bg-primary',
        lightBg: 'bg-gradient-to-br from-indigo-50 to-violet-50',
        border: 'border-primary/20',
        gradient: 'from-indigo-500 to-violet-500'
      };
    }

    if (discountType === 2) {
      return {
        main: `$${discountValue}`,
        sub: 'OFF',
        color: 'text-emerald-600',
        bg: 'bg-emerald-600',
        lightBg: 'bg-gradient-to-br from-emerald-50 to-teal-50',
        border: 'border-emerald-200/60',
        gradient: 'from-emerald-500 to-teal-500'
      };
    }

    if (discountType === 3) {
      return {
        main: 'FREE',
        sub: 'SHIPPING',
        color: 'text-violet-600',
        bg: 'bg-violet-600',
        lightBg: 'bg-gradient-to-br from-violet-50 to-purple-50',
        border: 'border-violet-200/60',
        gradient: 'from-violet-500 to-purple-500'
      };
    }

    return {
      main: 'DEAL',
      sub: '',
      color: 'text-foreground',
      bg: 'bg-muted-foreground',
      lightBg: 'bg-gradient-to-br from-slate-50 to-zinc-50',
      border: 'border-border/60',
      gradient: 'from-slate-500 to-zinc-500'
    };
  };

  const config = getDiscountConfig();

  const getExpiryStatus = () => {
    if (!coupon.endTime) return null;
    const end = new Date(coupon.endTime);
    const now = new Date();
    const diffTime = end.getTime() - now.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < 0) return { text: 'Expired', className: 'text-muted-foreground', iconClass: 'text-muted-foreground' };
    if (diffDays <= 3) return { text: 'Expires Soon!', className: 'text-rose-600 font-bold animate-pulse', iconClass: 'text-rose-600' };
    if (diffDays <= 7) return { text: `${diffDays} days left`, className: 'text-amber-600 font-medium', iconClass: 'text-amber-500' };

    return {
      text: `Expires: ${end.toLocaleDateString()}`,
      className: 'text-muted-foreground',
      iconClass: 'text-muted-foreground'
    };
  };

  const expiry = getExpiryStatus();

  return (
    <Card
      className={cn(
        "group relative overflow-hidden transition-all duration-300 hover:shadow-xl hover:shadow-primary/[0.08] hover:-translate-y-1 border-l-0 h-full bg-card rounded-2xl",
        "before:absolute before:left-0 before:top-0 before:bottom-0 before:w-1.5 before:z-10 before:rounded-l-2xl",
        config.bg.replace('bg-', 'before:bg-')
      )}
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
    >
      <CardContent className="p-0 flex h-full items-stretch">
        <div className={cn(
          "w-24 sm:w-28 flex flex-col items-center justify-center p-2 border-r-2 border-dashed relative shrink-0 overflow-hidden",
          config.lightBg, config.border
        )}>
          <div className={cn(
            "absolute inset-0 bg-gradient-to-br opacity-30 transition-opacity duration-300",
            config.gradient,
            isHovering && "opacity-50"
          )} />

          <div className={cn(
            "absolute -right-[11px] top-1/2 -translate-y-1/2 w-5 h-5 bg-card rounded-full border border-border/60 z-20 flex items-center justify-center transition-transform duration-300",
            isHovering && "rotate-12"
          )}>
            <Scissors className="w-3 h-3 text-muted-foreground" />
          </div>

          <div className="text-center space-y-1 z-10">
            <span className={cn("block text-xl sm:text-2xl font-display font-bold leading-none break-all", config.color)}>
              {config.main}
            </span>
            {config.sub && (
              <span className="block text-[10px] sm:text-xs font-bold text-muted-foreground uppercase tracking-wider">
                {config.sub}
              </span>
            )}
          </div>
        </div>

        <div className="flex-1 p-3 sm:p-4 min-w-0 flex flex-col justify-between gap-3 bg-card">
          <div>
            <div className="flex items-start justify-between gap-2 mb-2">
              <div className="flex items-center gap-2 min-w-0">
                <a
                  href={coupon.gotoUrl}
                  target="_blank"
                  rel="noopener"
                  className="w-8 h-8 rounded-full bg-muted/50 flex items-center justify-center overflow-hidden border border-border/60 shrink-0 hover:ring-2 hover:ring-primary/30 transition-all"
                >
                  {coupon.merchant.logoUrl ? (
                    <img src={coupon.merchant.logoUrl} alt={coupon.merchant.name} className="w-6 h-6 object-contain" />
                  ) : (
                    <ShoppingBag className="w-4 h-4 text-muted-foreground" />
                  )}
                </a>
                <div className="min-w-0 flex-1">
                  <a
                    href={coupon.gotoUrl}
                    target="_blank"
                    rel="noopener"
                    className="font-bold text-sm text-foreground leading-tight truncate block hover:text-primary transition-colors"
                    title={coupon.merchant.name}
                  >
                    {coupon.merchant.name}
                  </a>
                  {coupon.verified && (
                    <div className="flex items-center gap-1 text-[10px] text-emerald-600 font-medium">
                      <Check className="w-3 h-3" /> Verified
                    </div>
                  )}
                </div>
              </div>
            </div>

            <p className="text-xs sm:text-sm text-muted-foreground line-clamp-2 mb-2">
              {coupon.description}
            </p>

            {coupon.minPurchase && (
               <div className="text-[10px] text-muted-foreground mb-1">
                 Min. purchase: <span className="font-medium text-foreground">${coupon.minPurchase}</span>
               </div>
            )}
          </div>

          <div className="mt-auto space-y-2">
            <div className="relative group/code">
              <div className={cn(
                "flex items-center justify-between bg-muted/50 border border-dashed border-border/80 rounded-xl p-1 pr-1 pl-3 transition-all duration-300",
                "group-hover/code:border-primary/30 group-hover/code:bg-primary/5",
                copied && "border-emerald-400 bg-emerald-50"
              )}>
                <code className="font-mono font-bold text-foreground text-xs sm:text-sm tracking-wide truncate mr-2">
                  {coupon.code}
                </code>
                <Button
                  size="sm"
                  variant={copied ? "default" : "secondary"}
                  className={cn(
                    "h-7 text-xs px-2 sm:px-3 transition-all duration-300 shrink-0 active:scale-95 rounded-lg",
                    copied
                      ? "bg-emerald-600 hover:bg-emerald-700 text-white shadow-sm"
                      : "hover:bg-card hover:shadow-md"
                  )}
                  onClick={handleCopy}
                >
                  {copied ? (
                    <span className="flex items-center gap-1.5">
                      <Check className="w-3.5 h-3.5" /> Copied!
                    </span>
                  ) : (
                    <span className="flex items-center gap-1.5">
                      <Copy className="w-3.5 h-3.5" /> Copy
                    </span>
                  )}
                </Button>
              </div>
              {copied && (
                <div className="absolute -top-8 left-1/2 -translate-x-1/2 bg-emerald-600 text-white text-xs font-medium px-2 py-1 rounded-lg shadow-lg animate-in fade-in slide-in-from-bottom-2">
                  Copied to clipboard!
                </div>
              )}
            </div>

            {expiry && (
              <div className="flex items-center justify-between text-[10px] sm:text-xs">
                 <a
                   href={coupon.gotoUrl}
                   target="_blank"
                   rel="noopener"
                   className="flex items-center gap-1 text-primary hover:text-primary/80 transition-colors font-medium"
                 >
                   <ExternalLink className="w-3 h-3" />
                   {t('getCoupon')}
                 </a>
                 <div className={cn("flex items-center gap-1", expiry.className)}>
                   <Clock className={cn("w-3 h-3", expiry.iconClass)} />
                   {expiry.text}
                 </div>
              </div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
