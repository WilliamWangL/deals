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
          ? 'bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950'
          : 'bg-gradient-to-b from-background via-muted/50 to-background'
      )}
    >
      {/* Animated Background Mesh */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        {/* Primary glow */}
        <div
          className={cn(
            'absolute rounded-full blur-[120px]',
            isDark
              ? '-top-32 -left-32 w-[600px] h-[600px] bg-primary/10 animate-pulse-glow'
              : '-top-20 -left-20 w-[400px] h-[400px] bg-primary/5'
          )}
        />
        {/* Secondary glow */}
        <div
          className={cn(
            'absolute rounded-full blur-[100px]',
            isDark
              ? 'top-1/4 -right-20 w-[500px] h-[500px] bg-accent/10'
              : 'top-0 right-0 w-[300px] h-[300px] bg-accent/5'
          )}
        />
        {/* Accent glow - only on dark variant */}
        {isDark && (
          <div className="absolute bottom-0 left-1/3 w-[400px] h-[400px] bg-primary/5 rounded-full blur-[80px]" />
        )}

        {/* Subtle grid overlay */}
        <div className={cn(
            "absolute inset-0 bg-[size:60px_60px]",
            isDark 
                ? "bg-[linear-gradient(rgba(255,255,255,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.02)_1px,transparent_1px)]"
                : "bg-[linear-gradient(rgba(0,0,0,0.02)_1px,transparent_1px),linear-gradient(90deg,rgba(0,0,0,0.02)_1px,transparent_1px)]"
        )} />

        {/* Gradient fade at bottom */}
        <div
          className={cn(
            'absolute inset-x-0 bottom-0 bg-gradient-to-t to-transparent',
            isDark ? 'h-40 from-slate-950' : 'h-20 from-background'
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
              <div className={cn(
                  "inline-flex items-center gap-2 px-4 py-2 rounded-full backdrop-blur-xl border text-sm font-medium mb-6 shadow-lg",
                  isDark 
                    ? "bg-white/[0.07] border-white/[0.08] text-white/70 shadow-black/10"
                    : "bg-white/50 border-black/[0.05] text-muted-foreground shadow-black/5"
              )}>
                <span className={isDark ? "text-white/70" : "text-muted-foreground"}>{date}</span>
              </div>
            </div>
          )}

          {/* Title */}
          <h1
            className={cn(
              'font-display font-bold tracking-tight animate-in fade-in slide-in-from-bottom-6 duration-700',
              isDark ? 'text-white' : 'text-foreground',
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
                'max-w-2xl leading-relaxed animate-in fade-in slide-in-from-bottom-6 duration-700 delay-150',
                isDark ? 'text-slate-300/90' : 'text-muted-foreground',
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
            <path d="M0 60V30C360 10 720 0 1080 10C1260 18 1380 26 1440 30V60H0Z" className={isDark ? "fill-slate-950" : "fill-background"} />
          </svg>
        </div>
      )}
    </section>
  );
}
