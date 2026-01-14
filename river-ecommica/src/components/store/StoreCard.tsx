import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Store } from "@/types"
import Link from "next/link"
import Image from "next/image"
import { Star, ArrowRight, Tag, TicketPercent } from "lucide-react"

export function StoreCard({ store, locale = 'en' }: { store: Store; locale?: string }) {
  const rating = store.rating || 0;

  return (
    <Link href={`/${locale}/stores/${store.slug}`} className="group block h-full">
      <Card className="h-full transition-all duration-500 hover:shadow-[0_8px_30px_rgb(0,0,0,0.04)] hover:-translate-y-1.5 hover:border-primary/20 overflow-hidden relative bg-white border-slate-200/60">
        
        <div className="absolute inset-0 bg-gradient-to-b from-slate-50/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none" />

        <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-all duration-300 transform translate-x-2 group-hover:translate-x-0 z-10">
          <div className="bg-primary/10 p-1.5 rounded-full backdrop-blur-sm">
            <ArrowRight className="w-4 h-4 text-primary" />
          </div>
        </div>

        <CardContent className="p-6 flex flex-col items-center text-center h-full relative z-0">
          
          <div className="relative w-24 h-24 mb-5 bg-white rounded-2xl flex items-center justify-center p-4 border border-slate-100 shadow-sm group-hover:shadow-xl group-hover:shadow-primary/5 transition-all duration-500 group-hover:-translate-y-1">
             {store.logoUrl ? (
               <Image 
                 src={store.logoUrl} 
                 alt={store.name} 
                 width={80} 
                 height={80} 
                 className="object-contain w-full h-full transition-transform duration-500 group-hover:scale-110" 
               />
             ) : (
               <span className="text-3xl font-bold text-slate-300 group-hover:text-primary/50 transition-colors">{store.name[0]}</span>
             )}
          </div>
          
          <h3 className="font-bold text-xl mb-2 text-slate-900 group-hover:text-primary transition-colors line-clamp-1 tracking-tight">
            {store.name}
          </h3>
          
          <div className="flex items-center gap-2 mb-4 bg-slate-50/80 px-3 py-1 rounded-full border border-slate-100">
             <div className="flex gap-0.5">
               {[1, 2, 3, 4, 5].map((star) => (
                 <Star
                   key={star}
                   className={`w-3.5 h-3.5 ${
                     star <= Math.round(rating) 
                       ? "fill-amber-400 text-amber-400" 
                       : "fill-slate-200 text-slate-200"
                   }`}
                 />
               ))}
             </div>
             <span className="text-sm font-bold text-slate-700">{rating.toFixed(1)}</span>
          </div>
          
          {store.regions && store.regions.length > 0 ? (
            <div className="flex flex-wrap gap-1.5 justify-center mb-6 min-h-[1.5rem]">
              {store.regions.slice(0, 3).map((region) => (
                <Badge 
                  key={region} 
                  variant="secondary" 
                  className="bg-white hover:bg-slate-50 text-slate-600 border border-slate-200/60 text-[10px] uppercase tracking-wider px-2 py-0.5 shadow-sm font-medium"
                >
                  {region}
                </Badge>
              ))}
            </div>
          ) : (
             <div className="mb-6 min-h-[1.5rem]" />
          )}
          
          <div className="mt-auto w-full pt-4 border-t border-slate-100 grid grid-cols-2 gap-4">
             <div className="flex flex-col items-center group/stat">
               <span className="text-xs text-slate-400 uppercase tracking-wider font-medium mb-0.5 flex items-center gap-1">
                  <Tag className="w-3 h-3" /> Deals
               </span>
               <span className="font-bold text-slate-900 text-lg group-hover/stat:text-primary transition-colors">{store.dealCount}</span>
             </div>
             <div className="flex flex-col items-center border-l border-slate-100 group/stat">
               <span className="text-xs text-slate-400 uppercase tracking-wider font-medium mb-0.5 flex items-center gap-1">
                  <TicketPercent className="w-3 h-3" /> Coupons
               </span>
               <span className="font-bold text-slate-900 text-lg group-hover/stat:text-primary transition-colors">{store.couponCount}</span>
             </div>
          </div>
          
        </CardContent>
      </Card>
    </Link>
  )
}

export default StoreCard;
