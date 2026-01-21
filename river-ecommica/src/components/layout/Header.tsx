'use client';

import { useState, useEffect, useRef } from 'react';
import { useTranslations } from 'next-intl';
import { Link, useRouter } from '@/i18n/routing';
import { NAV_LINKS } from '@/config/navigation';
import { LanguageSwitcher } from '@/components/layout/LanguageSwitcher';
import { RegionSelector } from '@/components/layout/RegionSelector';
import { Button } from '@/components/ui/button';
import { Sheet, SheetContent, SheetTrigger, SheetHeader, SheetTitle, SheetClose } from '@/components/ui/sheet';
import { Menu, Search, X, ShoppingBag, ChevronRight, Flame, Store, Ticket, BookOpen, LayoutGrid } from 'lucide-react';
import { cn } from '@/lib/utils';

const NAV_ICONS: Record<string, React.ElementType> = {
  '/deals': Flame,
  '/stores': Store,
  '/coupons': Ticket,
  '/blog': BookOpen,
};

interface HeaderProps {
  currentRegion?: string
  regions?: { code: string; name: string }[]
}

export function Header({ currentRegion = 'GLOBAL', regions = [] }: HeaderProps) {
    const t = useTranslations('Common');
    const router = useRouter();
    const searchInputRef = useRef<HTMLInputElement>(null);
    const mobileSearchInputRef = useRef<HTMLInputElement>(null);
    const [isScrolled, setIsScrolled] = useState(false);
    const [isSearchOpen, setIsSearchOpen] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [mobileSearchQuery, setMobileSearchQuery] = useState('');

    const handleSearch = () => {
        const query = searchQuery.trim();
        if (query) {
            router.push(`/deals?q=${encodeURIComponent(query)}`);
            setIsSearchOpen(false);
            setSearchQuery('');
        }
    };

    const handleMobileSearch = () => {
        const query = mobileSearchQuery.trim();
        if (query) {
            router.push(`/deals?q=${encodeURIComponent(query)}`);
            setIsMobileMenuOpen(false);
            setMobileSearchQuery('');
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSearch();
        } else if (e.key === 'Escape') {
            setIsSearchOpen(false);
            setSearchQuery('');
        }
    };

    const handleMobileKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleMobileSearch();
        }
    };

    useEffect(() => {
        if (isSearchOpen && searchInputRef.current) {
            searchInputRef.current.focus();
        }
    }, [isSearchOpen]);

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
                    ? "bg-background/80 backdrop-blur-xl border-b border-border/50 shadow-sm"
                    : "bg-background/50 backdrop-blur-md border-b border-transparent"
            )}
        >
            <div className="container mx-auto px-4 h-14 sm:h-16 flex items-center justify-between">
                {/* Mobile: Menu + Logo */}
                <div className="flex items-center md:hidden">
                    <Sheet open={isMobileMenuOpen} onOpenChange={setIsMobileMenuOpen}>
                        <SheetTrigger asChild>
                            <Button variant="ghost" size="icon" className="mr-2 -ml-2 hover:bg-muted/50 h-9 w-9">
                                <Menu className="h-5 w-5" />
                                <span className="sr-only">Menu</span>
                            </Button>
                        </SheetTrigger>
                        <SheetContent side="left" className="w-[85vw] max-w-[320px] p-0">
                            <SheetHeader className="p-4 pb-3 border-b bg-gradient-to-r from-primary/5 to-transparent">
                                <SheetTitle className="font-bold text-lg flex items-center gap-2">
                                    <div className="bg-primary/10 p-1.5 rounded-lg">
                                        <ShoppingBag className="h-5 w-5 text-primary" />
                                    </div>
                                    Ecommica
                                </SheetTitle>
                            </SheetHeader>

                            {/* Mobile Search */}
                            <div className="p-4 border-b">
                                <div className="relative">
                                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                    <input
                                        ref={mobileSearchInputRef}
                                        type="text"
                                        value={mobileSearchQuery}
                                        onChange={(e) => setMobileSearchQuery(e.target.value)}
                                        onKeyDown={handleMobileKeyDown}
                                        placeholder={t('searchPlaceholder')}
                                        className="w-full h-10 pl-9 pr-4 rounded-lg border border-input bg-muted/30 focus:bg-background focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all text-sm"
                                    />
                                </div>
                            </div>

                            {/* Mobile Navigation */}
                            <nav className="flex flex-col p-2">
                                {NAV_LINKS.map((link) => {
                                    const Icon = NAV_ICONS[link.href] || LayoutGrid;
                                    return (
                                        <SheetClose asChild key={link.href}>
                                            <Link
                                                href={link.href}
                                                className="flex items-center gap-3 text-base font-medium text-foreground/80 hover:text-foreground hover:bg-muted/50 transition-colors py-3 px-3 rounded-lg group"
                                            >
                                                <div className="w-9 h-9 rounded-lg bg-muted/50 group-hover:bg-primary/10 flex items-center justify-center transition-colors">
                                                    <Icon className="h-4 w-4 text-muted-foreground group-hover:text-primary transition-colors" />
                                                </div>
                                                <span className="flex-1">{t(link.label)}</span>
                                                <ChevronRight className="h-4 w-4 text-muted-foreground/50 group-hover:text-muted-foreground transition-colors" />
                                            </Link>
                                        </SheetClose>
                                    );
                                })}
                            </nav>

                            {/* Mobile Footer */}
                            <div className="absolute bottom-0 left-0 right-0 p-4 border-t bg-muted/30">
                                <div className="flex items-center justify-between">
                                    <span className="text-xs text-muted-foreground">Language</span>
                                    <LanguageSwitcher />
                                </div>
                            </div>
                        </SheetContent>
                    </Sheet>

                    <Link href="/" className="font-bold text-lg flex items-center gap-1.5">
                        <ShoppingBag className="h-5 w-5 text-primary" />
                        <span>Ecommica</span>
                    </Link>
                </div>

                {/* Desktop: Logo */}
                <Link href="/" className="hidden md:flex items-center gap-2 font-bold text-2xl tracking-tight group">
                    <div className="bg-primary/10 p-1.5 rounded-lg group-hover:bg-primary/20 transition-colors">
                        <ShoppingBag className="h-6 w-6 text-primary" />
                    </div>
                    <span>Ecommica</span>
                </Link>

                {/* Desktop: Navigation */}
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

                {/* Right Actions */}
                <div className="flex items-center gap-1 sm:gap-2">
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => setIsSearchOpen(!isSearchOpen)}
                        className={cn(
                            "text-muted-foreground hover:text-foreground hover:bg-muted/50 transition-all h-9 w-9",
                            isSearchOpen && "bg-muted text-foreground"
                        )}
                    >
                        {isSearchOpen ? <X className="h-5 w-5" /> : <Search className="h-5 w-5" />}
                        <span className="sr-only">{isSearchOpen ? 'Close search' : t('search')}</span>
                    </Button>

                    {regions.length > 0 && (
                      <RegionSelector currentRegion={currentRegion} regions={regions} />
                    )}

                    <div className="hidden sm:block">
                        <LanguageSwitcher />
                    </div>
                </div>
            </div>

            {/* Desktop Search Dropdown */}
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
                            ref={searchInputRef}
                            type="text"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            onKeyDown={handleKeyDown}
                            placeholder={t('searchPlaceholder')}
                            className="w-full h-12 pl-12 pr-24 rounded-full border border-input bg-muted/30 focus:bg-background focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all shadow-sm"
                        />
                        <Button
                            onClick={handleSearch}
                            disabled={!searchQuery.trim()}
                            className="absolute right-2 top-1/2 -translate-y-1/2 rounded-full h-8 px-4"
                        >
                            {t('search')}
                        </Button>
                    </div>
                </div>
            </div>
        </header>
    );
}
