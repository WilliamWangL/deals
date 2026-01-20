import { cn } from '@/lib/utils';

interface PageHeroProps {
  title: string;
  subtitle?: string;
  /** 'dark' uses deep gradient background, 'light' uses softer tones */
  variant?: 'dark' | 'light';
  /** 'default' for main pages, 'compact' for legal/info pages */
  size?: 'default' | 'compact';
  /** Optional date to display (e.g., "Last updated: Jan 2026") */
  date?: string;
  children?: React.ReactNode;
}

export function PageHero({
  title,
  subtitle,
  variant = 'dark',
  size = 'default',
  date,
  children
}: PageHeroProps) {
  const isDark = variant === 'dark';
  const isCompact = size === 'compact';

  return (
    <section
      className={cn(
        'relative overflow-hidden',
        isDark
          ? 'bg-gradient-to-b from-slate-950 via-indigo-950/90 to-slate-900'
          : 'bg-gradient-to-b from-slate-900 via-slate-900/95 to-slate-950'
      )}
    >
      {/* Animated Background Mesh */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        {/* Primary glow */}
        <div
          className={cn(
            'absolute rounded-full blur-[120px]',
            isDark
              ? '-top-32 -left-32 w-[600px] h-[600px] bg-indigo-600/30 animate-pulse-glow'
              : '-top-20 -left-20 w-[400px] h-[400px] bg-indigo-600/20'
          )}
        />
        {/* Secondary glow */}
        <div
          className={cn(
            'absolute rounded-full blur-[100px]',
            isDark
              ? 'top-1/4 -right-20 w-[500px] h-[500px] bg-violet-600/25'
              : 'top-0 right-0 w-[300px] h-[300px] bg-violet-600/15'
          )}
        />
        {/* Accent glow - only on dark variant */}
        {isDark && (
          <div className="absolute bottom-0 left-1/3 w-[400px] h-[400px] bg-amber-500/15 rounded-full blur-[80px]" />
        )}

        {/* Subtle grid overlay */}
        <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)] bg-[size:60px_60px]" />

        {/* Gradient fade at bottom */}
        <div
          className={cn(
            'absolute inset-x-0 bottom-0 bg-gradient-to-t to-transparent',
            isDark ? 'h-40 from-slate-900' : 'h-20 from-slate-950'
          )}
        />
      </div>

      {/* Content */}
      <div
        className={cn(
          'container relative mx-auto px-4',
          isCompact ? 'pt-12 pb-16 lg:pt-16 lg:pb-20' : 'pt-16 pb-20 lg:pt-24 lg:pb-28'
        )}
      >
        <div className="mx-auto max-w-4xl flex flex-col items-center text-center">
          {/* Date badge - for legal pages */}
          {date && (
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
              <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/[0.07] backdrop-blur-xl border border-white/[0.08] text-sm font-medium mb-6 shadow-lg shadow-black/10">
                <span className="text-white/70">{date}</span>
              </div>
            </div>
          )}

          {/* Title */}
          <h1
            className={cn(
              'font-display font-bold tracking-tight text-white animate-in fade-in slide-in-from-bottom-6 duration-700',
              isCompact
                ? 'text-3xl md:text-4xl lg:text-5xl mb-4'
                : 'text-4xl md:text-5xl lg:text-6xl mb-6'
            )}
          >
            {title}
          </h1>

          {/* Subtitle */}
          {subtitle && (
            <p
              className={cn(
                'text-slate-300/90 max-w-2xl leading-relaxed animate-in fade-in slide-in-from-bottom-6 duration-700 delay-150',
                isCompact ? 'text-base md:text-lg' : 'text-lg md:text-xl'
              )}
            >
              {subtitle}
            </p>
          )}

          {/* Additional content (e.g., badges, CTAs) */}
          {children && (
            <div className="mt-8 animate-in fade-in slide-in-from-bottom-4 duration-700 delay-300">
              {children}
            </div>
          )}
        </div>
      </div>

      {/* Smooth curve transition - only on default size */}
      {!isCompact && (
        <div className="absolute bottom-0 left-0 right-0">
          <svg viewBox="0 0 1440 60" fill="none" className="w-full h-auto" preserveAspectRatio="none">
            <path d="M0 60V30C360 10 720 0 1080 10C1260 18 1380 26 1440 30V60H0Z" className="fill-background" />
          </svg>
        </div>
      )}
    </section>
  );
}
