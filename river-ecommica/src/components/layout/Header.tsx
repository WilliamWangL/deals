import Link from 'next/link';
import { Button } from "@/components/ui/button"
import { useTranslations } from 'next-intl';

export function Header() {
    const t = useTranslations('Common');
    
    return (
        <header className="border-b sticky top-0 bg-white z-50">
            <div className="container mx-auto px-4 h-16 flex items-center justify-between">
                <Link href="/" className="font-bold text-2xl text-primary">
                    Ecommica
                </Link>
                
                <nav className="hidden md:flex gap-6 items-center">
                    <Link href="/stores" className="text-sm font-medium hover:text-primary">{t('stores')}</Link>
                    <Link href="/deals" className="text-sm font-medium hover:text-primary">{t('deals')}</Link>
                    <Link href="/coupons" className="text-sm font-medium hover:text-primary">{t('coupons')}</Link>
                    <Link href="/blog" className="text-sm font-medium hover:text-primary">{t('blog')}</Link>
                </nav>

                <div className="flex items-center gap-2">
                    <Button variant="outline" size="sm">{t('search')}</Button>
                </div>
            </div>
        </header>
    )
}
