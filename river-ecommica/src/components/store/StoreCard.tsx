import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Store } from "@/types"
import Link from "next/link"
import Image from "next/image"
import { Star, ArrowRight } from "lucide-react"

export function StoreCard({ store }: { store: Store }) {
  const rating = store.rating || 0;

  return (
    <Link href={`/stores/${store.slug}`} className="group block h-full">
      <Card className="h-full transition-all duration-300 hover:shadow-xl hover:-translate-y-1 hover:border-primary/30 overflow-hidden relative bg-white border-slate-200">
        
        <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-all duration-300 transform translate-x-2 group-hover:translate-x-0 z-10">
          <ArrowRight className="w-5 h-5 text-primary" />
        </div>

        <CardContent className="p-5 flex flex-col items-center text-center h-full">
          
          <div className="relative w-20 h-20 mb-4 bg-white rounded-full flex items-center justify-center overflow-hidden border border-slate-100 shadow-sm group-hover:shadow-md transition-all duration-300 group-hover:scale-105">
             {store.logoUrl ? (
               <Image 
                 src={store.logoUrl} 
                 alt={store.name} 
                 width={80} 
                 height={80} 
                 className="object-contain p-2" 
               />
             ) : (
               <span className="text-2xl font-bold text-slate-300">{store.name[0]}</span>
             )}
          </div>
          
          <h3 className="font-bold text-lg mb-2 text-slate-900 group-hover:text-primary transition-colors line-clamp-1">
            {store.name}
          </h3>
          
          <div className="flex items-center gap-1.5 mb-3">
             <div className="flex">
               {[1, 2, 3, 4, 5].map((star) => (
                 <Star
                   key={star}
                   className={`w-3.5 h-3.5 ${
                     star <= Math.round(rating) 
                       ? "fill-yellow-400 text-yellow-400" 
                       : "fill-gray-100 text-gray-200"
                   }`}
                 />
               ))}
             </div>
             <span className="text-sm font-semibold text-slate-600">{rating.toFixed(1)}</span>
          </div>
          
          {store.regions && store.regions.length > 0 ? (
            <div className="flex flex-wrap gap-1.5 justify-center mb-4 min-h-[1.5rem]">
              {store.regions.slice(0, 3).map((region) => (
                <Badge 
                  key={region} 
                  variant="secondary" 
                  className="bg-slate-50 text-slate-600 border border-slate-200 hover:bg-slate-100 text-xs px-2 py-0.5 font-normal shadow-sm"
                >
                  {region}
                </Badge>
              ))}
            </div>
          ) : (
             <div className="mb-4 min-h-[1.5rem]" />
          )}
          
          <div className="mt-auto w-full pt-4 border-t border-slate-100 flex justify-between items-center text-sm text-slate-500">
             <div className="flex items-center gap-1">
               <span className="font-bold text-slate-700">{store.dealCount}</span> Deals
             </div>
             <div className="w-1 h-1 rounded-full bg-slate-300" />
             <div className="flex items-center gap-1">
               <span className="font-bold text-slate-700">{store.couponCount}</span> Coupons
             </div>
          </div>
          
        </CardContent>
      </Card>
    </Link>
  )
}

export default StoreCard;
