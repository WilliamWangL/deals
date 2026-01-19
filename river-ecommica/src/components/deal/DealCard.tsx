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
        text = `Ends in ${days} days`;
      } else {
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        
        urgent = true;
        if (days === 1) text = `Ends in 1d ${hours}h`;
        else text = `Ends in ${hours}h ${minutes}m`;
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
      "flex items-center gap-1.5 text-xs font-semibold px-2 py-1 rounded-full backdrop-blur-sm",
      isUrgent 
        ? "text-red-600 bg-red-50 dark:bg-red-900/20 animate-pulse border border-red-100 dark:border-red-900/30" 
        : "text-amber-600 bg-amber-50 dark:bg-amber-900/20 border border-amber-100 dark:border-amber-900/30"
    )}>
      <Clock className="w-3.5 h-3.5" />
      <span>{timeLeft}</span>
    </div>
  );
}

export function DealCard({ deal }: DealCardProps) {
  const t = useTranslations('Deal');
  const locale = useLocale();
  const discountHigh = deal.discountPercent >= 50;
  
  return (
    <Card className="group relative h-full flex flex-col overflow-visible bg-white dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800 transition-all duration-500 hover:shadow-2xl hover:shadow-primary/5 hover:-translate-y-1.5 rounded-xl">
      <div className="relative h-52 w-full overflow-hidden rounded-t-xl">
        {deal.imageUrl ? (
          <Image 
            src={deal.imageUrl} 
            alt={deal.title} 
            fill 
            className="object-cover transition-transform duration-700 group-hover:scale-110" 
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw" 
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-gray-300 bg-gray-50 dark:bg-zinc-800">
             <Store className="w-12 h-12 opacity-20" />
          </div>
        )}
        
        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent opacity-60 group-hover:opacity-80 transition-opacity duration-300" />
        
        {deal.featured && (
          <div className="absolute top-3 left-3 z-10 flex items-center gap-1.5 px-3 py-1 bg-gradient-to-r from-amber-400 to-orange-500 text-white text-xs font-bold rounded-full shadow-lg shadow-orange-500/30 ring-1 ring-white/20 animate-in fade-in zoom-in duration-300">
            <Sparkles className="w-3.5 h-3.5 fill-current" />
            <span>FEATURED</span>
          </div>
        )}

        {deal.exclusive && (
          <div className="absolute top-3 left-3 z-10 flex items-center gap-1.5 px-3 py-1 bg-gradient-to-r from-purple-600 to-violet-600 text-white text-xs font-bold rounded-full shadow-lg shadow-purple-500/30 ring-1 ring-white/20 animate-in fade-in zoom-in duration-300">
            <Crown className="w-3.5 h-3.5 fill-current" />
            <span>EXCLUSIVE</span>
          </div>
        )}

        {deal.discountPercent > 0 && (
          <div className={cn(
            "absolute top-3 right-3 z-10 flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold text-white shadow-lg backdrop-blur-md ring-1 ring-white/20",
            discountHigh 
              ? "bg-gradient-to-r from-red-600 to-pink-600 shadow-red-500/30" 
              : "bg-gradient-to-r from-emerald-500 to-teal-500 shadow-emerald-500/30"
          )}>
            <Tag className="w-3.5 h-3.5 fill-white/20" />
            <span>{deal.discountPercent}% OFF</span>
          </div>
        )}
      </div>

      <CardContent className="flex flex-col flex-grow p-5 space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="relative w-8 h-8 rounded-full overflow-hidden bg-white border border-gray-100 shadow-sm flex-shrink-0 group-hover:ring-2 ring-primary/20 transition-all">
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
            <span className="text-sm font-semibold text-gray-600 dark:text-gray-300 truncate max-w-[100px]">
              {deal.merchant.name}
            </span>
          </div>
          {deal.endTime && <CountdownTimer endTime={deal.endTime} />}
        </div>
        
        <h3 className="text-lg font-bold leading-snug text-zinc-900 dark:text-zinc-100 group-hover:text-primary transition-colors line-clamp-2 min-h-[3.5rem]">
          <Link href={`/${locale}/deals/${deal.slug}`} className="hover:underline decoration-2 decoration-primary/30 underline-offset-4">
            {deal.title}
          </Link>
        </h3>

        <div className="mt-auto pt-2 flex items-baseline gap-3">
          <span className="text-3xl font-black text-transparent bg-clip-text bg-gradient-to-br from-zinc-900 to-zinc-600 dark:from-white dark:to-zinc-400 group-hover:from-primary group-hover:to-primary/70 transition-all">
            {deal.dealPrice > 0 ? `$${deal.dealPrice}` : `${deal.discountPercent}% OFF`}
          </span>
          {deal.originalPrice > 0 && (
            <span className="text-sm font-medium text-gray-400 line-through decoration-gray-400/50">
              ${deal.originalPrice}
            </span>
          )}
        </div>
      </CardContent>

      <CardFooter className="p-5 pt-0">
        <Button
          className="w-full h-11 bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 font-bold rounded-lg shadow-md hover:shadow-lg hover:scale-[1.02] active:scale-[0.98] transition-all duration-300 group/btn"
          asChild
        >
           <Link href={deal.gotoUrl} target="_blank" rel="noopener" className="flex items-center justify-center gap-2">
             <span>{t('getDeal')}</span>
             <ExternalLink className="w-4 h-4 transition-transform group-hover/btn:translate-x-1" />
           </Link>
        </Button>
      </CardFooter>
    </Card>
  )
}

export default DealCard;
