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
      <Card className="h-full transition-all duration-300 hover:shadow-xl hover:shadow-primary/[0.08] hover:-translate-y-1 hover:border-primary/20 overflow-hidden relative bg-card border-border/50 rounded-2xl">

        {/* Hover arrow indicator */}
        <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-all duration-300 transform translate-x-2 group-hover:translate-x-0 z-10">
          <div className="bg-primary/10 p-2 rounded-full backdrop-blur-sm">
            <ArrowRight className="w-4 h-4 text-primary" />
          </div>
        </div>

        <CardContent className="p-6 flex flex-col items-center text-center h-full">

          {/* Logo */}
          <div className="relative w-20 h-20 mb-5 bg-gradient-to-br from-slate-50 to-slate-100 rounded-2xl flex items-center justify-center p-3 border border-border/50 group-hover:shadow-lg group-hover:shadow-primary/[0.08] transition-all duration-300 group-hover:-translate-y-1">
            {store.logoUrl ? (
              <Image
                src={store.logoUrl}
                alt={store.name}
                width={64}
                height={64}
                className="object-contain w-full h-full transition-transform duration-300 group-hover:scale-105"
              />
            ) : (
              <span className="text-2xl font-display font-bold text-muted-foreground/50 group-hover:text-primary/50 transition-colors">
                {store.name[0]}
              </span>
            )}
          </div>

          {/* Store Name */}
          <h3 className="font-display font-bold text-lg mb-2 text-foreground group-hover:text-primary transition-colors line-clamp-1">
            {store.name}
          </h3>

          {/* Rating */}
          <div className="flex items-center gap-2 mb-4 px-3 py-1.5 rounded-full bg-muted/50 border border-border/50">
            <div className="flex gap-0.5">
              {[1, 2, 3, 4, 5].map((star) => (
                <Star
                  key={star}
                  className={`w-3.5 h-3.5 ${
                    star <= Math.round(rating)
                      ? "fill-amber-400 text-amber-400"
                      : "fill-muted text-muted"
                  }`}
                />
              ))}
            </div>
            <span className="text-sm font-semibold text-foreground">{rating.toFixed(1)}</span>
          </div>

          {/* Regions */}
          {store.regions && store.regions.length > 0 ? (
            <div className="flex flex-wrap gap-1.5 justify-center mb-5 min-h-[1.5rem]">
              {store.regions.slice(0, 3).map((region) => (
                <Badge
                  key={region}
                  variant="secondary"
                  className="bg-card text-muted-foreground border border-border/60 text-[10px] uppercase tracking-wider px-2 py-0.5 font-medium hover:bg-muted"
                >
                  {region}
                </Badge>
              ))}
            </div>
          ) : (
            <div className="mb-5 min-h-[1.5rem]" />
          )}

          {/* Stats */}
          <div className="mt-auto w-full pt-4 border-t border-border/50 grid grid-cols-2 gap-4">
            <div className="flex flex-col items-center">
              <span className="text-xs text-muted-foreground uppercase tracking-wider font-medium mb-1 flex items-center gap-1">
                <Tag className="w-3 h-3" /> Deals
              </span>
              <span className="font-display font-bold text-foreground text-lg group-hover:text-primary transition-colors">
                {store.dealCount || 0}
              </span>
            </div>
            <div className="flex flex-col items-center border-l border-border/50">
              <span className="text-xs text-muted-foreground uppercase tracking-wider font-medium mb-1 flex items-center gap-1">
                <TicketPercent className="w-3 h-3" /> Coupons
              </span>
              <span className="font-display font-bold text-foreground text-lg group-hover:text-primary transition-colors">
                {store.couponCount || 0}
              </span>
            </div>
          </div>

        </CardContent>
      </Card>
    </Link>
  )
}

export default StoreCard;
