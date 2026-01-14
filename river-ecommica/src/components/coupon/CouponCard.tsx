'use client';

import { useState } from 'react';
import { Coupon } from '@/types';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

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

  const getDiscountText = () => {
    switch (coupon.discountType) {
      case 1:
        return `${coupon.discountValue}% Off`;
      case 2:
        return `$${coupon.discountValue} Off`;
      case 3:
        return 'Free Shipping';
      default:
        return 'Discount';
    }
  };

  return (
    <Card className="overflow-hidden hover:shadow-lg transition-shadow">
      <CardContent className="p-4">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center flex-shrink-0">
            {coupon.merchantLogo ? (
              <img src={coupon.merchantLogo} alt={coupon.merchantName} className="w-10 h-10 object-contain" />
            ) : (
              <span className="text-lg font-bold text-gray-400">{coupon.merchantName?.charAt(0) || '?'}</span>
            )}
          </div>
          <div className="flex-1 min-w-0">
            <h3 className="font-semibold truncate">{coupon.merchantName}</h3>
            <Badge variant="destructive" className="text-xs">{getDiscountText()}</Badge>
          </div>
        </div>

        <p className="text-sm text-gray-600 mb-3 line-clamp-2">{coupon.description}</p>

        <div className="flex items-center gap-2 mb-3">
          <code className="flex-1 bg-gray-100 px-3 py-2 rounded text-center font-mono text-sm border-2 border-dashed border-gray-300">
            {coupon.code}
          </code>
          <Button size="sm" variant={copied ? "secondary" : "default"} onClick={handleCopy}>
            {copied ? 'Copied!' : 'Copy'}
          </Button>
        </div>

        <div className="flex items-center justify-between text-xs text-gray-500">
          {coupon.verified && <Badge variant="outline" className="text-green-600">Verified</Badge>}
          {coupon.endTime && <span>Expires: {new Date(coupon.endTime).toLocaleDateString()}</span>}
        </div>
      </CardContent>
    </Card>
  );
}
