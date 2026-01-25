'use client';

import { useState, useEffect } from 'react';
import { useRouter } from '@/i18n/routing';
import { Search, ArrowRight } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

interface HeroSearchFormProps {
  placeholder: string;
  buttonText: string;
}

export function HeroSearchForm({ placeholder, buttonText }: HeroSearchFormProps) {
  const router = useRouter();
  const [query, setQuery] = useState('');
  const [isMounted, setIsMounted] = useState(false);
  const [isFocused, setIsFocused] = useState(false);

  // Fix hydration mismatch by only enabling button after mount
  useEffect(() => {
    setIsMounted(true);
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = query.trim();
    if (trimmed) {
      router.push(`/deals?q=${encodeURIComponent(trimmed)}`);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="relative group w-full max-w-2xl mx-auto">
      {/* Glow effect - Primary/Accent gradient */}
      <div className={`
        absolute -inset-1 rounded-2xl blur-xl transition-all duration-500
        bg-gradient-to-r from-primary/30 via-accent/30 to-primary/30
        ${isFocused ? 'opacity-100 scale-[1.02]' : 'opacity-40 group-hover:opacity-60'}
      `} />

      {/* Main container */}
      <div className={`
        relative flex items-center gap-2 p-2 rounded-2xl
        bg-card/80 backdrop-blur-xl border border-white/40
        shadow-2xl shadow-primary/5
        transition-all duration-300
        ${isFocused ? 'ring-2 ring-primary/50 translate-y-[-2px]' : 'hover:translate-y-[-1px]'}
      `}>
        {/* Search icon */}
        <div className="flex items-center justify-center w-12 h-12 rounded-xl bg-primary/10 text-primary">
          <Search className="w-5 h-5" />
        </div>

        {/* Input */}
        <Input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder={placeholder}
          className="flex-1 border-0 bg-transparent text-foreground placeholder:text-muted-foreground focus-visible:ring-0 text-lg h-12 shadow-none font-medium"
        />

        {/* Submit button */}
        <Button
          type="submit"
          size="lg"
          disabled={!isMounted || !query.trim()}
          className="h-12 px-8 btn-primary rounded-xl flex items-center gap-2"
        >
          <span className="hidden sm:inline">{buttonText}</span>
          <ArrowRight className="w-4 h-4" />
        </Button>
      </div>
    </form>
  );
}
