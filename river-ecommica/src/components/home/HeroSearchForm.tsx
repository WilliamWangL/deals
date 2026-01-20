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
    <form onSubmit={handleSubmit} className="relative group">
      {/* Glow effect - amber themed to match savings */}
      <div className={`
        absolute -inset-1 rounded-2xl blur-lg transition-all duration-500
        bg-gradient-to-r from-amber-500/40 via-orange-500/30 to-amber-500/40
        ${isFocused ? 'opacity-80 -inset-1.5' : 'opacity-40 group-hover:opacity-60'}
      `} />

      {/* Main container */}
      <div className={`
        relative flex items-center gap-2 p-2 rounded-xl
        bg-white/95 backdrop-blur-xl
        shadow-2xl shadow-black/20
        ring-1 transition-all duration-300
        ${isFocused ? 'ring-amber-400/50 ring-2' : 'ring-white/20'}
      `}>
        {/* Search icon */}
        <div className="flex items-center justify-center w-12 h-12 rounded-lg bg-slate-100">
          <Search className="w-5 h-5 text-slate-500" />
        </div>

        {/* Input */}
        <Input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder={placeholder}
          className="flex-1 border-0 bg-transparent text-slate-900 placeholder:text-slate-400 focus-visible:ring-0 text-lg h-12 shadow-none"
        />

        {/* Submit button */}
        <Button
          type="submit"
          size="lg"
          disabled={!isMounted || !query.trim()}
          className="h-12 px-6 bg-slate-900 hover:bg-slate-800 text-white font-semibold rounded-lg transition-all shadow-lg hover:shadow-xl disabled:opacity-40 disabled:cursor-not-allowed flex items-center gap-2"
        >
          <span className="hidden sm:inline">{buttonText}</span>
          <ArrowRight className="w-4 h-4" />
        </Button>
      </div>
    </form>
  );
}
