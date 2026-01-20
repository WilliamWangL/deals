'use client';

import { useState, useEffect } from 'react';
import { useRouter } from '@/i18n/routing';
import { Search } from 'lucide-react';
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
      <div className="absolute -inset-1 bg-gradient-to-r from-cyan-500 via-blue-500 to-indigo-500 rounded-xl blur opacity-30 group-hover:opacity-60 transition duration-500"></div>
      <div className="relative flex items-center shadow-2xl rounded-xl overflow-hidden bg-white/95 backdrop-blur-xl p-2 ring-1 ring-white/20">
        <Search className="ml-4 h-6 w-6 text-slate-400" />
        <Input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder={placeholder}
          className="border-0 bg-transparent text-slate-900 placeholder:text-slate-400 focus-visible:ring-0 text-lg h-14 pl-4 shadow-none flex-grow"
        />
        <Button
          type="submit"
          size="lg"
          disabled={!isMounted || !query.trim()}
          className="h-14 px-8 bg-slate-900 hover:bg-slate-800 text-white font-bold transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] rounded-lg text-base disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {buttonText}
        </Button>
      </div>
    </form>
  );
}
