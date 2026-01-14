'use client';

import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Deal } from "@/types"
import Image from "next/image"
import Link from "next/link"
import { useTranslations } from 'next-intl';
import { Clock } from "lucide-react"
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
      
      if (days > 1) {
        setIsUrgent(false);
        return `Ends in ${days} days`;
      }
      
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      
      setIsUrgent(true);
      if (days === 1) return `Ends in 1d ${hours}h`;
      return `Ends in ${hours}h ${minutes}m`;
    };

    const result = calculateTime();
    if (result) setTimeLeft(result);

    const timer = setInterval(() => {
      const res = calculateTime();
      if (res) setTimeLeft(res);
    }, 60000);

    return () => clearInterval(timer);
  }, [endTime]);

  if (!timeLeft) return null;

  return (
    <div className={cn(
      "flex items-center gap-1.5 text-xs font-medium",
      isUrgent ? "text-red-600 animate-pulse" : "text-gray-500"
    )}>
      <Clock className="w-3.5 h-3.5" />
      <span>{timeLeft}</span>
    </div>
  );
}

export default DealCard;
export function DealCard({ deal }: DealCardProps) {
  const t = useTranslations('Deal');
  const discountHigh = deal.discountPercent >= 50;
  
  return (
    <Card className="group h-full flex flex-col overflow-hidden hover:shadow-xl transition-all duration-300 hover:scale-[1.02] hover:ring-2 hover:ring-primary/20 border-gray-200">
      <div className="relative h-48 w-full bg-gray-100 overflow-hidden">
        {deal.imageUrl ? (
             <Image 
                src={deal.imageUrl} 
                alt={deal.title} 
                fill 
                className="object-cover transition-transform duration-500 group-hover:scale-110" 
                sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw" 
             />
        ) : (
            <div className="w-full h-full flex items-center justify-center text-gray-300 bg-gray-50">No Image</div>
        )}
        
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
        
        {deal.featured && (
          <Badge className="absolute top-2 left-2 bg-yellow-500 hover:bg-yellow-600 text-white border-none shadow-sm z-10">
            Featured
          </Badge>
        )}

        {deal.discountPercent > 0 && (
          <div className={cn(
            "absolute top-0 right-0 px-3 py-1.5 rounded-bl-xl font-bold text-white text-sm shadow-md z-10",
            discountHigh 
              ? "bg-gradient-to-br from-red-500 to-orange-600" 
              : "bg-gradient-to-br from-emerald-500 to-teal-600"
          )}>
            {deal.discountPercent}% OFF
          </div>
        )}
      </div>

      <CardHeader className="p-4 pb-2 space-y-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="relative w-6 h-6 rounded-full overflow-hidden bg-white border border-gray-100 shadow-sm flex-shrink-0">
              {deal.merchantLogo ? (
                <Image 
                  src={deal.merchantLogo} 
                  alt={deal.merchantName} 
                  fill 
                  className="object-contain p-0.5"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center bg-primary/10 text-primary text-[10px] font-bold">
                  {deal.merchantName.charAt(0)}
                </div>
              )}
            </div>
            <span className="text-sm font-medium text-gray-600 truncate max-w-[120px]">
              {deal.merchantName}
            </span>
          </div>
          {deal.endTime && <CountdownTimer endTime={deal.endTime} />}
        </div>
        
        <CardTitle className="text-lg font-bold leading-tight group-hover:text-primary transition-colors">
          <Link href={`/deals/${deal.slug}`} className="line-clamp-2">
            {deal.title}
          </Link>
        </CardTitle>
      </CardHeader>

      <CardContent className="p-4 pt-0 flex-grow">
        <div className="flex items-baseline gap-2 mt-1">
          <span className="text-2xl font-extrabold text-red-600">
            {deal.dealPrice > 0 ? `$${deal.dealPrice}` : `${deal.discountPercent}% OFF`}
          </span>
          {deal.originalPrice > 0 && (
            <span className="text-sm text-gray-400 line-through decoration-gray-400/60">
              ${deal.originalPrice}
            </span>
          )}
        </div>
      </CardContent>

      <CardFooter className="p-4 pt-0 mt-auto">
        <Button className="w-full font-semibold shadow-sm group-hover:shadow-md transition-all" asChild>
           <Link href={`/api/go/${deal.id}`} target="_blank">
             {t('getDeal')}
           </Link>
        </Button>
      </CardFooter>
    </Card>
  )
}
