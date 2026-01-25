'use client';

import { useState } from 'react';
import { Coupon } from '@/types';
import { Button } from '@/components/ui/button';
import { Check, Copy, Clock, Sparkles, ExternalLink, BadgeCheck } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useTranslations } from 'next-intl';
import { getTrackingLink } from '@/lib/tracking';

interface CouponCardProps {
  coupon: Coupon;
}

export default function CouponCard({ coupon }: CouponCardProps) {
  const [copied, setCopied] = useState(false);
  const [isRevealed, setIsRevealed] = useState(false);
  const t = useTranslations('Deal');

  const handleCopy = async () => {
    await navigator.clipboard.writeText(coupon.code);
    setCopied(true);
    setIsRevealed(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const getDiscountDisplay = () => {
    const { discountType, discountValue } = coupon;

    if (discountType === 1) {
      return { value: discountValue, suffix: '%', label: 'OFF' };
    }
    if (discountType === 2) {
      return { prefix: '$', value: discountValue, label: 'OFF' };
    }
    if (discountType === 3) {
      return { value: 'FREE', label: 'SHIPPING' };
    }
    return { value: 'DEAL', label: '' };
  };

  const discount = getDiscountDisplay();

  const getExpiryInfo = () => {
    if (!coupon.endTime) return null;
    const end = new Date(coupon.endTime);
    const now = new Date();
    const diffDays = Math.ceil((end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));

    if (diffDays < 0) return { text: 'Expired', urgent: false, expired: true };
    if (diffDays <= 3) return { text: `${diffDays}d left`, urgent: true, expired: false };
    if (diffDays <= 7) return { text: `${diffDays} days`, urgent: false, expired: false };
    return { text: end.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }), urgent: false, expired: false };
  };

  const expiry = getExpiryInfo();

  // Mask code until revealed
  const displayCode = isRevealed ? coupon.code : coupon.code.slice(0, 3) + '••••••';

  return (
    <article
      className="group relative card-interactive h-full flex flex-col overflow-hidden"
    >
      {/* Subtle gradient overlay on hover */}
      <div className="absolute inset-0 bg-gradient-to-br from-primary/[0.02] via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none" />

      <div className="relative p-5 flex flex-col flex-1">
        {/* Header: Merchant + Discount Badge */}
        <div className="flex items-start justify-between gap-3 mb-4">
          {/* Merchant Info */}
          <div className="flex items-center gap-3 min-w-0 flex-1">
            <a
              href={getTrackingLink(coupon.trackingLinkId, coupon.gotoUrl)}
              target="_blank"
              rel="noopener"
              className="relative shrink-0"
            >
              <div className="w-11 h-11 rounded-xl bg-white border border-border flex items-center justify-center overflow-hidden transition-transform duration-300 group-hover:scale-105 shadow-sm">
                {coupon.merchant.logoUrl ? (
                  <img
                    src={coupon.merchant.logoUrl}
                    alt={coupon.merchant.name}
                    className="w-8 h-8 object-contain"
                  />
                ) : (
                  <span className="text-base font-bold text-muted-foreground">
                    {coupon.merchant.name.charAt(0)}
                  </span>
                )}
              </div>
            </a>
            <div className="min-w-0 flex-1">
              <a
                href={getTrackingLink(coupon.trackingLinkId, coupon.gotoUrl)}
                target="_blank"
                rel="noopener"
                className="font-semibold text-sm text-foreground truncate block hover:text-primary transition-colors"
              >
                {coupon.merchant.name}
              </a>
              <div className="flex items-center gap-2 mt-0.5">
                {coupon.verified && (
                  <span className="badge-savings px-1.5 py-0.5 text-[10px]">
                    <BadgeCheck className="w-3 h-3" />
                    Verified
                  </span>
                )}
                {expiry && !expiry.expired && (
                  <span className={cn(
                    "inline-flex items-center gap-1 text-[11px]",
                    expiry.urgent ? "text-amber-600 font-semibold" : "text-muted-foreground"
                  )}>
                    <Clock className="w-3 h-3" />
                    {expiry.text}
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Discount Badge */}
          <div className="shrink-0 text-right">
            <div className="inline-flex flex-col items-end">
              <span className="text-2xl font-bold tracking-tight font-display text-gradient-savings leading-none">
                {discount.prefix}{discount.value}{discount.suffix}
              </span>
              {discount.label && (
                <span className="text-[10px] font-bold text-muted-foreground uppercase tracking-widest mt-0.5">
                  {discount.label}
                </span>
              )}
            </div>
          </div>
        </div>

        {/* Description */}
        {coupon.description && (
          <p className="text-sm text-muted-foreground line-clamp-2 mb-4 leading-relaxed">
            {coupon.title || coupon.description}
          </p>
        )}

        {/* Minimum Purchase */}
        {coupon.minPurchase && (
          <div className="text-xs text-muted-foreground mb-4">
            Min. order: <span className="font-medium text-foreground">${coupon.minPurchase}</span>
          </div>
        )}

        <div className="mt-auto">
          {/* Code Section */}
          <div className="relative">
            {/* Decorative dashed line */}
            <div className="absolute -left-5 -right-5 top-0 border-t border-dashed border-border/60" />

            <div className="pt-4">
              <div className="flex items-center gap-2">
                {/* Code Display */}
                <div className={cn(
                  "flex-1 relative overflow-hidden rounded-xl transition-all duration-300",
                  "bg-muted/30 border border-border",
                  copied && "border-emerald-500/30 bg-emerald-50/30"
                )}>
                  <div className="flex items-center justify-between px-4 py-3">
                    <code className={cn(
                      "font-mono font-bold text-sm tracking-wider transition-all duration-300",
                      isRevealed ? "text-foreground" : "text-muted-foreground"
                    )}>
                      {displayCode}
                    </code>
                    {!isRevealed && (
                      <button
                        onClick={() => setIsRevealed(true)}
                        className="text-[11px] text-primary font-medium hover:underline"
                      >
                        Reveal
                      </button>
                    )}
                  </div>
                </div>

                {/* Copy Button */}
                <Button
                  onClick={handleCopy}
                  className={cn(
                    "h-12 px-5 rounded-xl font-semibold text-sm transition-all duration-300",
                    "shadow-sm hover:shadow-md active:scale-[0.98]",
                    copied
                      ? "bg-emerald-500 hover:bg-emerald-600 text-white"
                      : "bg-foreground hover:bg-foreground/90 text-background"
                  )}
                >
                  {copied ? (
                    <span className="flex items-center gap-2">
                      <Check className="w-4 h-4" />
                      Copied
                    </span>
                  ) : (
                    <span className="flex items-center gap-2">
                      <Copy className="w-4 h-4" />
                      Copy
                    </span>
                  )}
                </Button>
              </div>
            </div>
          </div>

          {/* Footer Link */}
          <div className="mt-4 flex items-center justify-between">
            <a
              href={getTrackingLink(coupon.trackingLinkId, coupon.gotoUrl)}
              target="_blank"
              rel="noopener"
              className="inline-flex items-center gap-1.5 text-xs font-medium text-primary hover:text-primary/80 transition-colors group/link hover-underline"
            >
              <ExternalLink className="w-3.5 h-3.5 transition-transform group-hover/link:translate-x-0.5" />
              {t('getCoupon')}
            </a>

            {expiry?.expired && (
              <span className="text-[11px] text-muted-foreground">
                Expired
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Sparkle effect on copy */}
      {copied && (
        <div className="absolute top-4 right-4 animate-ping">
          <Sparkles className="w-4 h-4 text-emerald-400" />
        </div>
      )}
    </article>
  );
}
