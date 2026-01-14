'use client';

import { useState, useEffect } from 'react';
import { useTranslations } from 'next-intl';
import { Link } from '@/i18n/routing';
import { NAV_LINKS } from '@/config/navigation';
import { LanguageSwitcher } from '@/components/layout/LanguageSwitcher';
import { Button } from '@/components/ui/button';
import { Sheet, SheetContent, SheetTrigger, SheetHeader, SheetTitle } from '@/components/ui/sheet';
import { Menu, Search, X, ShoppingBag } from 'lucide-react';
import { cn } from '@/lib/utils';

export function Header() {
    const t = useTranslations('Common');
    const [isScrolled, setIsScrolled] = useState(false);
    const [isSearchOpen, setIsSearchOpen] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 10);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    return (
        <header
            className={cn(
                "sticky top-0 z-50 w-full transition-all duration-300",
                isScrolled 
                    ? "bg-background/80 backdrop-blur-xl border-b shadow-sm" 
                    : "bg-background/50 backdrop-blur-md border-b border-transparent"
            )}
        >
            <div className="container mx-auto px-4 h-16 flex items-center justify-between">
                <div className="flex items-center md:hidden">
                    <Sheet>
                        <SheetTrigger asChild>
                            <Button variant="ghost" size="icon" className="mr-2 -ml-2 hover:bg-muted/50">
                                <Menu className="h-5 w-5" />
                                <span className="sr-only">Menu</span>
                            </Button>
                        </SheetTrigger>
                        <SheetContent side="left" className="w-[300px] sm:w-[400px]">
                            <SheetHeader className="text-left border-b pb-4 mb-4">
                                <SheetTitle className="font-bold text-xl flex items-center gap-2">
                                    <ShoppingBag className="h-5 w-5 text-primary" />
                                    Ecommica
                                </SheetTitle>
                            </SheetHeader>
                            <nav className="flex flex-col gap-2">
                                {NAV_LINKS.map((link) => (
                                    <Link
                                        key={link.href}
                                        href={link.href}
                                        className="text-lg font-medium hover:text-primary transition-colors py-3 px-2 rounded-md hover:bg-muted/50 block"
                                    >
                                        {t(link.label)}
                                    </Link>
                                ))}
                            </nav>
                        </SheetContent>
                    </Sheet>
                    
                    <Link href="/" className="font-bold text-xl flex items-center gap-2">
                        <ShoppingBag className="h-5 w-5 text-primary" />
                        Ecommica
                    </Link>
                </div>

                <Link href="/" className="hidden md:flex items-center gap-2 font-bold text-2xl tracking-tight group">
                    <div className="bg-primary/10 p-1.5 rounded-lg group-hover:bg-primary/20 transition-colors">
                        <ShoppingBag className="h-6 w-6 text-primary" />
                    </div>
                    <span>Ecommica</span>
                </Link>

                <nav className="hidden md:flex items-center gap-8">
                    {NAV_LINKS.map((link) => (
                        <Link
                            key={link.href}
                            href={link.href}
                            className="group relative text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
                        >
                            {t(link.label)}
                            <span className="absolute -bottom-1 left-0 w-0 h-0.5 bg-primary transition-all duration-300 ease-out group-hover:w-full" />
                        </Link>
                    ))}
                </nav>

                <div className="flex items-center gap-2">
                    <Button 
                        variant="ghost" 
                        size="icon" 
                        onClick={() => setIsSearchOpen(!isSearchOpen)}
                        className={cn(
                            "text-muted-foreground hover:text-foreground hover:bg-muted/50 transition-all",
                            isSearchOpen && "bg-muted text-foreground"
                        )}
                    >
                        {isSearchOpen ? <X className="h-5 w-5" /> : <Search className="h-5 w-5" />}
                        <span className="sr-only">{isSearchOpen ? 'Close search' : t('search')}</span>
                    </Button>

                    <LanguageSwitcher />
                </div>
            </div>

            <div 
                className={cn(
                    "overflow-hidden transition-all duration-300 ease-in-out bg-background border-b absolute w-full",
                    isSearchOpen ? "max-h-24 opacity-100 shadow-md" : "max-h-0 opacity-0"
                )}
            >
                <div className="container mx-auto px-4 py-4 flex items-center justify-center">
                    <div className="relative w-full max-w-2xl transform transition-all duration-500 delay-75">
                        <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                        <input
                            type="text"
                            placeholder={t('searchPlaceholder')}
                            className="w-full h-12 pl-12 pr-4 rounded-full border border-input bg-muted/30 focus:bg-background focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all shadow-sm"
                            autoFocus={isSearchOpen}
                        />
                    </div>
                </div>
            </div>
        </header>
    );
}
