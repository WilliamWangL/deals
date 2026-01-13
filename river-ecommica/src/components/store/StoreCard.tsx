import { Card, CardContent } from "@/components/ui/card"
import { Store } from "@/types"
import Link from "next/link"
import Image from "next/image"

export function StoreCard({ store }: { store: Store }) {
  return (
    <Link href={`/stores/${store.slug}`}>
      <Card className="hover:shadow-md transition-shadow cursor-pointer h-full">
        <CardContent className="p-4 flex flex-col items-center text-center">
          <div className="relative w-20 h-20 mb-3 bg-gray-50 rounded-full flex items-center justify-center overflow-hidden border">
             {store.logoUrl ? (
               <Image src={store.logoUrl} alt={store.name} width={80} height={80} className="object-contain p-2" />
             ) : (
               <span className="text-xl font-bold text-gray-400">{store.name[0]}</span>
             )}
          </div>
          <h3 className="font-bold text-lg mb-1">{store.name}</h3>
          <p className="text-sm text-gray-500">{store.dealCount} Deals • {store.couponCount} Coupons</p>
        </CardContent>
      </Card>
    </Link>
  )
}
