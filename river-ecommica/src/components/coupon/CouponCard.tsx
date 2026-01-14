'use client';

import { useState } from 'react';
import { Coupon } from '@/types';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Check, Copy, Clock, Scissors, ShoppingBag } from 'lucide-react';
import { cn } from '@/lib/utils';

interface CouponCardProps {
  coupon: Coupon;
}

export default function CouponCard({ coupon }: CouponCardProps) {
  const [copied, setCopied] = useState(false);

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
          lightBg: 'bg-rose-50',
          border: 'border-rose-200'
        };
      }
      return {
        main: `${discountValue}%`,
        sub: 'OFF',
        color: 'text-blue-600',
        bg: 'bg-blue-600',
        lightBg: 'bg-blue-50',
        border: 'border-blue-200'
      };
    }
    
    if (discountType === 2) {
      return {
        main: `$${discountValue}`,
        sub: 'OFF',
        color: 'text-emerald-600',
        bg: 'bg-emerald-600',
        lightBg: 'bg-emerald-50',
        border: 'border-emerald-200'
      };
    }
    
    if (discountType === 3) {
      return {
        main: 'FREE',
        sub: 'SHIPPING',
        color: 'text-purple-600',
        bg: 'bg-purple-600',
        lightBg: 'bg-purple-50',
        border: 'border-purple-200'
      };
    }

    return {
      main: 'DEAL',
      sub: '',
      color: 'text-slate-600',
      bg: 'bg-slate-600',
      lightBg: 'bg-slate-50',
      border: 'border-slate-200'
    };
  };

  const config = getDiscountConfig();

  const getExpiryStatus = () => {
    if (!coupon.endTime) return null;
    const end = new Date(coupon.endTime);
    const now = new Date();
    const diffTime = end.getTime() - now.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < 0) return { text: 'Expired', className: 'text-gray-400', iconClass: 'text-gray-400' };
    if (diffDays <= 3) return { text: 'Expires Soon!', className: 'text-red-600 font-bold', iconClass: 'text-red-600' };
    if (diffDays <= 7) return { text: `${diffDays} days left`, className: 'text-orange-500 font-medium', iconClass: 'text-orange-500' };
    
    return { 
      text: `Expires: ${end.toLocaleDateString()}`, 
      className: 'text-gray-500',
      iconClass: 'text-gray-400' 
    };
  };

  const expiry = getExpiryStatus();

  return (
    <Card className={cn(
      "group relative overflow-hidden transition-all duration-300 hover:shadow-lg hover:-translate-y-1 border-l-0 h-full",
      "before:absolute before:left-0 before:top-0 before:bottom-0 before:w-1.5 before:z-10",
      config.bg.replace('bg-', 'before:bg-')
    )}>
      <CardContent className="p-0 flex h-full items-stretch">
        <div className={cn(
          "w-24 sm:w-28 flex flex-col items-center justify-center p-2 border-r-2 border-dashed relative shrink-0",
          config.lightBg, config.border
        )}>
          <div className={cn(
            "absolute -right-[11px] top-1/2 -translate-y-1/2 w-5 h-5 bg-white rounded-full border border-gray-200 z-20 flex items-center justify-center",
            "group-hover:rotate-12 transition-transform duration-300"
          )}>
            <Scissors className="w-3 h-3 text-gray-400" />
          </div>

          <div className="text-center space-y-1 z-10">
            <span className={cn("block text-xl sm:text-2xl font-black leading-none break-all", config.color)}>
              {config.main}
            </span>
            {config.sub && (
              <span className="block text-[10px] sm:text-xs font-bold text-gray-500 uppercase tracking-wider">
                {config.sub}
              </span>
            )}
          </div>
        </div>

        <div className="flex-1 p-3 sm:p-4 min-w-0 flex flex-col justify-between gap-3 bg-white">
          <div>
            <div className="flex items-start justify-between gap-2 mb-2">
              <div className="flex items-center gap-2 min-w-0">
                <div className="w-8 h-8 rounded-full bg-gray-50 flex items-center justify-center overflow-hidden border border-gray-100 shrink-0">
                  {coupon.merchantLogo ? (
                    <img src={coupon.merchantLogo} alt={coupon.merchantName} className="w-6 h-6 object-contain" />
                  ) : (
                    <ShoppingBag className="w-4 h-4 text-gray-400" />
                  )}
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="font-bold text-sm text-gray-900 leading-tight truncate" title={coupon.merchantName}>
                    {coupon.merchantName}
                  </h3>
                  {coupon.verified && (
                    <div className="flex items-center gap-1 text-[10px] text-green-600 font-medium">
                      <Check className="w-3 h-3" /> Verified
                    </div>
                  )}
                </div>
              </div>
            </div>

            <p className="text-xs sm:text-sm text-gray-600 line-clamp-2 mb-2">
              {coupon.description}
            </p>

            {coupon.minPurchase && (
               <div className="text-[10px] text-gray-500 mb-1">
                 Min. purchase: <span className="font-medium text-gray-700">${coupon.minPurchase}</span>
               </div>
            )}
          </div>

          <div className="mt-auto space-y-2">
            <div className="relative group/code">
              <div className={cn(
                "flex items-center justify-between bg-gray-50 border border-dashed border-gray-300 rounded-lg p-1 pr-1 pl-3 transition-colors",
                "group-hover/code:border-gray-400 group-hover/code:bg-gray-100/50"
              )}>
                <code className="font-mono font-bold text-gray-800 text-xs sm:text-sm tracking-wide truncate mr-2">
                  {coupon.code}
                </code>
                <Button 
                  size="sm" 
                  variant={copied ? "default" : "secondary"}
                  className={cn(
                    "h-7 text-xs px-2 sm:px-3 transition-all duration-300 shrink-0 active:scale-95",
                    copied ? "bg-green-600 hover:bg-green-700 text-white" : ""
                  )}
                  onClick={handleCopy}
                >
                  {copied ? (
                    <span className="flex items-center gap-1.5">
                      <Check className="w-3.5 h-3.5" /> Copied
                    </span>
                  ) : (
                    <span className="flex items-center gap-1.5">
                      <Copy className="w-3.5 h-3.5" /> Copy
                    </span>
                  )}
                </Button>
              </div>
            </div>

            {expiry && (
              <div className="flex items-center justify-end text-[10px] sm:text-xs">
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
