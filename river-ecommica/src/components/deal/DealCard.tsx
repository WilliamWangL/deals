'use client';

import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Deal } from "@/types"
import Image from "next/image"
import Link from "next/link"
import { useTranslations, useLocale } from 'next-intl';
import { Clock, Sparkles, Tag, Store, Crown, ExternalLink } from "lucide-react"
import { useEffect, useState } from "react"
import { cn } from "@/lib/utils"

interface DealCardProps {
  deal: Deal;
}

function CountdownTimer({ endTime }: { endTime: string }) {
  const [timeLeft, setTimeLeft] = useState<string>("");
  const [isUrgent, setIsUrgent] = useState(false);

  useEffect(() => {
    const calculateTime = () => {
      const end = new Date(endTime).getTime();
      if (isNaN(end)) return null;
      const now = new Date().getTime();
      const diff = end - now;

      if (diff <= 0) return null;

      const days = Math.floor(diff / (1000 * 60 * 60 * 24));

      let urgent = false;
      let text = '';

      if (days > 1) {
        text = `${days}d left`;
      } else {
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

        urgent = true;
        if (days === 1) text = `1d ${hours}h`;
        else text = `${hours}h ${minutes}m`;
      }
      return { text, urgent };
    };

    const update = () => {
      const result = calculateTime();
      if (result) {
        setTimeLeft(result.text);
        setIsUrgent(result.urgent);
      }
    };

    requestAnimationFrame(update);
    const timer = setInterval(update, 60000);
    return () => clearInterval(timer);
  }, [endTime]);

  if (!timeLeft) return null;

  return (
    <div className={cn(
      "flex items-center gap-1.5 text-xs font-semibold px-2.5 py-1 rounded-full backdrop-blur-sm",
      isUrgent
        ? "text-rose-600 bg-rose-50 border border-rose-100 animate-pulse"
        : "text-amber-600 bg-amber-50 border border-amber-100"
    )}>
      <Clock className="w-3 h-3" />
      <span>{timeLeft}</span>
    </div>
  );
}

export function DealCard({ deal }: DealCardProps) {
  const t = useTranslations('Deal');
  const locale = useLocale();
  const discountHigh = deal.discountPercent >= 50;

  return (
    <Card className="group relative h-full flex flex-col overflow-hidden bg-card border-border/50 transition-all duration-300 hover:shadow-xl hover:shadow-primary/[0.08] hover:-translate-y-1 hover:border-primary/20 rounded-2xl">
      {/* Image Container */}
      <div className="relative h-48 w-full overflow-hidden bg-gradient-to-br from-slate-50 via-slate-100 to-slate-50">
        {deal.imageUrl ? (
          <Image
            src={deal.imageUrl}
            alt={deal.title}
            fill
            className="object-contain p-8 transition-transform duration-500 group-hover:scale-105"
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <Store className="w-12 h-12 text-muted-foreground/20" />
          </div>
        )}

        {/* Overlay gradient */}
        <div className="absolute inset-0 bg-gradient-to-t from-black/40 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />

        {/* Badges */}
        <div className="absolute top-3 left-3 z-10 flex flex-col gap-2">
          {deal.featured && (
            <div className="badge-featured">
              <Sparkles className="w-3 h-3" />
              FEATURED
            </div>
          )}
          {deal.exclusive && (
            <div className="badge-exclusive">
              <Crown className="w-3 h-3" />
              EXCLUSIVE
            </div>
          )}
        </div>

        {/* Discount Badge */}
        {deal.discountPercent > 0 && (
          <div className={cn(
            "absolute top-3 right-3 z-10",
            discountHigh ? "badge-deal" : "badge-savings"
          )}>
            <Tag className="w-3 h-3" />
            {deal.discountPercent}% OFF
          </div>
        )}
      </div>

      {/* Content */}
      <CardContent className="flex flex-col flex-grow p-5 space-y-4">
        {/* Merchant & Timer Row */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="relative w-8 h-8 rounded-full overflow-hidden bg-muted border border-border flex-shrink-0 group-hover:ring-2 ring-primary/20 transition-all">
              {deal.merchant.logoUrl ? (
                <Image
                  src={deal.merchant.logoUrl}
                  alt={deal.merchant.name}
                  fill
                  className="object-contain p-1"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center bg-primary/5 text-primary text-xs font-bold">
                  {deal.merchant.name.charAt(0)}
                </div>
              )}
            </div>
            <span className="text-sm font-medium text-muted-foreground truncate max-w-[100px]">
              {deal.merchant.name}
            </span>
          </div>
          {deal.endTime && <CountdownTimer endTime={deal.endTime} />}
        </div>

        {/* Title */}
        <h3 className="text-base font-semibold leading-snug text-foreground group-hover:text-primary transition-colors line-clamp-2 min-h-[2.75rem]">
          <Link
            href={`/${locale}/deals/${deal.slug}`}
            className="hover:underline decoration-2 decoration-primary/30 underline-offset-4"
          >
            {deal.title}
          </Link>
        </h3>

        {/* Price */}
        <div className="mt-auto pt-2 flex items-baseline gap-3">
          <span className={cn(
            "text-2xl font-bold font-display",
            deal.dealPrice > 0 ? "text-foreground" : "text-gradient-savings"
          )}>
            {deal.dealPrice > 0 ? `$${deal.dealPrice}` : `${deal.discountPercent}% OFF`}
          </span>
          {deal.originalPrice > 0 && (
            <span className="text-sm font-medium text-muted-foreground line-through">
              ${deal.originalPrice}
            </span>
          )}
        </div>
      </CardContent>

      {/* Footer */}
      <CardFooter className="p-5 pt-0">
        <Button
          className="w-full h-11 bg-primary text-primary-foreground font-semibold rounded-xl shadow-md hover:shadow-lg hover:bg-primary/90 active:scale-[0.98] transition-all duration-200 group/btn"
          asChild
        >
          <Link href={deal.gotoUrl} target="_blank" rel="noopener" className="flex items-center justify-center gap-2">
            <span>{t('getDeal')}</span>
            <ExternalLink className="w-4 h-4 transition-transform group-hover/btn:translate-x-0.5" />
          </Link>
        </Button>
      </CardFooter>
    </Card>
  )
}

export default DealCard;
