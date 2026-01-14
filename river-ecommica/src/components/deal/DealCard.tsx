import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Deal } from "@/types"
import Image from "next/image"
import Link from "next/link"
import { useTranslations } from 'next-intl';

interface DealCardProps {
  deal: Deal;
}

export default DealCard;
export function DealCard({ deal }: DealCardProps) {
  const t = useTranslations('Deal');
  
  return (
    <Card className="h-full flex flex-col overflow-hidden hover:shadow-lg transition-shadow">
      <div className="relative h-48 w-full bg-gray-100">
        {deal.imageUrl ? (
             <Image 
                src={deal.imageUrl} 
                alt={deal.title}
                fill
                className="object-cover"
                sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
            />
        ) : (
            <div className="w-full h-full flex items-center justify-center text-gray-300">No Image</div>
        )}
        {deal.featured && (
          <Badge className="absolute top-2 left-2 bg-red-500">Featured</Badge>
        )}
      </div>
      <CardHeader className="p-4 pb-2">
        <div className="flex items-center gap-2 text-sm text-gray-500 mb-2">
           <span className="font-medium">{deal.merchantName}</span>
        </div>
        <CardTitle className="text-lg line-clamp-2 leading-tight">
          <Link href={`/deals/${deal.slug}`} className="hover:text-primary">
            {deal.title}
          </Link>
        </CardTitle>
      </CardHeader>
      <CardContent className="p-4 pt-0 flex-grow">
        <div className="flex items-baseline gap-2 mt-2">
          <span className="text-xl font-bold text-red-600">
            {deal.dealPrice > 0 ? `$${deal.dealPrice}` : `${deal.discountPercent}% OFF`}
          </span>
          {deal.originalPrice > 0 && (
            <span className="text-sm text-gray-400 line-through">${deal.originalPrice}</span>
          )}
        </div>
      </CardContent>
      <CardFooter className="p-4 pt-0">
        <Button className="w-full" asChild>
           <Link href={`/api/go/${deal.id}`} target="_blank">
             {t('getDeal')}
           </Link>
        </Button>
      </CardFooter>
    </Card>
  )
}
