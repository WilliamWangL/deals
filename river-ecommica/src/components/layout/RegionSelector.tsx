'use client'

import { useState, useRef, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { setCookie } from 'cookies-next'
import { Globe, Check, Sparkles } from 'lucide-react'
import { cn } from '@/lib/utils'
import { REGION_COOKIE_NAME, REGION_COOKIE_MAX_AGE } from '@/lib/region-constants'

interface Region {
  code: string
  name: string
}

interface RegionSelectorProps {
  currentRegion: string
  regions: Region[]
}

/**
 * Convert ISO 3166-1 alpha-2 country code to flag emoji
 * Each letter is converted to a Regional Indicator Symbol (🇦-🇿)
 * by adding 0x1F1E6 - 0x41 (offset from 'A' to regional indicator 'A')
 */
function countryCodeToFlag(code: string): string {
  // Special cases
  if (code === '00' || code === 'GLOBAL') return '🌍'
  if (code.length !== 2) return '🌏'

  const upperCode = code.toUpperCase()
  const offset = 0x1F1E6 - 0x41 // Regional Indicator Symbol A minus ASCII A

  try {
    const firstChar = upperCode.charCodeAt(0)
    const secondChar = upperCode.charCodeAt(1)

    // Validate it's A-Z
    if (firstChar < 0x41 || firstChar > 0x5A || secondChar < 0x41 || secondChar > 0x5A) {
      return '🌏'
    }

    return String.fromCodePoint(firstChar + offset) + String.fromCodePoint(secondChar + offset)
  } catch {
    return '🌏'
  }
}

// Gradient themes for each region - premium feel
const REGION_THEMES: Record<string, { gradient: string; glow: string; accent: string }> = {
  '00': {
    gradient: 'from-indigo-500 via-purple-500 to-pink-500',
    glow: 'shadow-indigo-500/30',
    accent: 'bg-indigo-500'
  },
  GLOBAL: {
    gradient: 'from-indigo-500 via-purple-500 to-pink-500',
    glow: 'shadow-indigo-500/30',
    accent: 'bg-indigo-500'
  },
  US: {
    gradient: 'from-blue-600 via-red-500 to-blue-600',
    glow: 'shadow-blue-500/30',
    accent: 'bg-blue-600'
  },
  GB: {
    gradient: 'from-red-600 via-blue-700 to-red-600',
    glow: 'shadow-red-500/30',
    accent: 'bg-red-600'
  },
  DE: {
    gradient: 'from-yellow-500 via-red-600 to-black',
    glow: 'shadow-yellow-500/30',
    accent: 'bg-yellow-500'
  },
  FR: {
    gradient: 'from-blue-600 via-white to-red-600',
    glow: 'shadow-blue-500/30',
    accent: 'bg-blue-600'
  },
  ES: {
    gradient: 'from-red-600 via-yellow-500 to-red-600',
    glow: 'shadow-yellow-500/30',
    accent: 'bg-yellow-500'
  },
  IT: {
    gradient: 'from-green-600 via-white to-red-600',
    glow: 'shadow-green-500/30',
    accent: 'bg-green-600'
  },
  RU: {
    gradient: 'from-white via-blue-600 to-red-600',
    glow: 'shadow-blue-500/30',
    accent: 'bg-blue-600'
  },
  CN: {
    gradient: 'from-red-600 via-yellow-500 to-red-600',
    glow: 'shadow-red-500/30',
    accent: 'bg-red-600'
  },
  JP: {
    gradient: 'from-white via-red-500 to-white',
    glow: 'shadow-red-500/30',
    accent: 'bg-red-500'
  },
  KR: {
    gradient: 'from-blue-600 via-red-500 to-blue-600',
    glow: 'shadow-red-500/30',
    accent: 'bg-red-500'
  },
  AU: {
    gradient: 'from-blue-700 via-yellow-400 to-blue-700',
    glow: 'shadow-blue-500/30',
    accent: 'bg-blue-700'
  },
  CA: {
    gradient: 'from-red-600 via-white to-red-600',
    glow: 'shadow-red-500/30',
    accent: 'bg-red-600'
  },
  BR: {
    gradient: 'from-green-600 via-yellow-400 to-blue-600',
    glow: 'shadow-green-500/30',
    accent: 'bg-green-600'
  },
  IN: {
    gradient: 'from-orange-500 via-white to-green-600',
    glow: 'shadow-orange-500/30',
    accent: 'bg-orange-500'
  },
  PL: {
    gradient: 'from-white via-red-600 to-red-600',
    glow: 'shadow-red-500/30',
    accent: 'bg-red-600'
  },
  UA: {
    gradient: 'from-blue-500 via-yellow-400 to-blue-500',
    glow: 'shadow-blue-500/30',
    accent: 'bg-blue-500'
  },
}

const defaultTheme = {
  gradient: 'from-slate-500 via-slate-400 to-slate-500',
  glow: 'shadow-slate-500/30',
  accent: 'bg-slate-500'
}

export function RegionSelector({ currentRegion, regions }: RegionSelectorProps) {
  const router = useRouter()
  const [isOpen, setIsOpen] = useState(false)
  const [hoveredRegion, setHoveredRegion] = useState<string | null>(null)
  const menuRef = useRef<HTMLDivElement>(null)
  const triggerRef = useRef<HTMLButtonElement>(null)

  const currentRegionData = regions.find(r => r.code === currentRegion) || regions[0] || { code: 'GLOBAL', name: 'Global' }
  const currentTheme = REGION_THEMES[currentRegionData.code] || defaultTheme

  const handleSelect = (code: string) => {
    if (code === currentRegion) {
      setIsOpen(false)
      return
    }
    setCookie(REGION_COOKIE_NAME, code, { maxAge: REGION_COOKIE_MAX_AGE })
    setIsOpen(false)
    // Full page reload to ensure all data refreshes with new region
    window.location.reload()
  }

  const getFlag = (code: string) => countryCodeToFlag(code)
  const getTheme = (code: string) => REGION_THEMES[code] || defaultTheme

  // Close on outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        menuRef.current &&
        !menuRef.current.contains(event.target as Node) &&
        triggerRef.current &&
        !triggerRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false)
      }
    }

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside)
    }
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [isOpen])

  // Close on escape
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setIsOpen(false)
    }
    if (isOpen) {
      document.addEventListener('keydown', handleEscape)
    }
    return () => document.removeEventListener('keydown', handleEscape)
  }, [isOpen])

  return (
    <div className="relative">
      {/* Trigger Button - Premium Globe Design */}
      <button
        ref={triggerRef}
        onClick={() => setIsOpen(!isOpen)}
        className={cn(
          "group relative flex items-center gap-2.5 h-10 px-3 rounded-xl",
          "bg-gradient-to-r from-slate-100/80 to-slate-50/80 dark:from-slate-800/80 dark:to-slate-900/80",
          "border border-slate-200/60 dark:border-slate-700/60",
          "hover:border-slate-300 dark:hover:border-slate-600",
          "hover:shadow-lg hover:shadow-slate-200/50 dark:hover:shadow-slate-900/50",
          "transition-all duration-300 ease-out",
          isOpen && "border-primary/50 shadow-lg shadow-primary/10"
        )}
      >
        {/* Animated Globe Container */}
        <div className="relative">
          {/* Glow ring on hover/active */}
          <div className={cn(
            "absolute inset-0 rounded-full transition-all duration-500",
            "bg-gradient-to-r opacity-0 blur-md scale-150",
            currentTheme.gradient,
            (isOpen || hoveredRegion) && "opacity-40"
          )} />

          {/* Globe icon with gradient overlay */}
          <div className={cn(
            "relative w-7 h-7 flex items-center justify-center rounded-full",
            "bg-gradient-to-br from-slate-100 to-white dark:from-slate-700 dark:to-slate-800",
            "shadow-inner transition-transform duration-300",
            "group-hover:scale-110"
          )}>
            <Globe className={cn(
              "w-4 h-4 transition-all duration-500",
              "text-slate-500 dark:text-slate-400",
              "group-hover:text-primary",
              isOpen && "text-primary animate-spin-slow"
            )} />

            {/* Current region indicator dot */}
            <span className={cn(
              "absolute -bottom-0.5 -right-0.5 text-[10px]",
              "drop-shadow-sm transition-transform duration-200",
              "group-hover:scale-125"
            )}>
              {getFlag(currentRegionData.code)}
            </span>
          </div>
        </div>

        {/* Region name - desktop only */}
        <div className="hidden sm:flex flex-col items-start">
          <span className="text-[10px] uppercase tracking-wider text-slate-400 dark:text-slate-500 font-medium leading-none">
            Region
          </span>
          <span className="text-sm font-semibold text-slate-700 dark:text-slate-200 leading-tight">
            {currentRegionData.name}
          </span>
        </div>

        {/* Chevron with rotation */}
        <svg
          className={cn(
            "w-3.5 h-3.5 text-slate-400 transition-transform duration-300 hidden sm:block",
            isOpen && "rotate-180"
          )}
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {/* Dropdown Menu - Card Grid Layout */}
      <div
        ref={menuRef}
        className={cn(
          "absolute right-0 top-full mt-2 z-50",
          "w-[340px] sm:w-[380px] p-4",
          "bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl",
          "border border-slate-200/80 dark:border-slate-700/80",
          "rounded-2xl shadow-2xl shadow-slate-900/10 dark:shadow-black/30",
          "transition-all duration-300 ease-out origin-top-right",
          isOpen
            ? "opacity-100 scale-100 translate-y-0"
            : "opacity-0 scale-95 -translate-y-2 pointer-events-none"
        )}
      >
        {/* Header */}
        <div className="flex items-center gap-3 mb-4 pb-3 border-b border-slate-100 dark:border-slate-800">
          <div className={cn(
            "w-10 h-10 rounded-xl flex items-center justify-center",
            "bg-gradient-to-br from-primary/20 to-primary/5"
          )}>
            <Sparkles className="w-5 h-5 text-primary" />
          </div>
          <div>
            <h3 className="text-sm font-bold text-slate-800 dark:text-slate-100">
              Choose Your Region
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Discover deals tailored for you
            </p>
          </div>
        </div>

        {/* Region Grid */}
        <div className="grid grid-cols-2 gap-2 max-h-[320px] overflow-y-auto scrollbar-hide pr-1">
          {regions.map((region, index) => {
            const theme = getTheme(region.code)
            const isSelected = currentRegion === region.code
            const isHovered = hoveredRegion === region.code

            return (
              <button
                key={region.code}
                onClick={() => handleSelect(region.code)}
                onMouseEnter={() => setHoveredRegion(region.code)}
                onMouseLeave={() => setHoveredRegion(null)}
                className={cn(
                  "group relative flex items-center gap-3 p-3 rounded-xl",
                  "transition-all duration-300 ease-out",
                  "hover:scale-[1.02] active:scale-[0.98]",
                  isSelected
                    ? "bg-gradient-to-r from-primary/10 to-primary/5 border-2 border-primary/30"
                    : "bg-slate-50/80 dark:bg-slate-800/50 border-2 border-transparent hover:border-slate-200 dark:hover:border-slate-700"
                )}
                style={{
                  animationDelay: `${index * 30}ms`
                }}
              >
                {/* Gradient bar indicator */}
                <div className={cn(
                  "absolute left-0 top-2 bottom-2 w-1 rounded-full",
                  "bg-gradient-to-b transition-all duration-300",
                  theme.gradient,
                  isSelected ? "opacity-100" : "opacity-0 group-hover:opacity-60"
                )} />

                {/* Flag container */}
                <div className={cn(
                  "relative flex-shrink-0 w-10 h-10 rounded-lg flex items-center justify-center",
                  "bg-white dark:bg-slate-700 shadow-sm",
                  "transition-all duration-300",
                  "group-hover:shadow-md",
                  isSelected && cn("shadow-lg", theme.glow)
                )}>
                  <span className={cn(
                    "text-2xl transition-transform duration-300",
                    "group-hover:scale-110",
                    isHovered && "animate-bounce-subtle"
                  )}>
                    {getFlag(region.code)}
                  </span>
                </div>

                {/* Region info */}
                <div className="flex-1 min-w-0 text-left">
                  <span className={cn(
                    "block text-sm font-semibold truncate transition-colors duration-200",
                    isSelected
                      ? "text-primary"
                      : "text-slate-700 dark:text-slate-200 group-hover:text-slate-900 dark:group-hover:text-white"
                  )}>
                    {region.name}
                  </span>
                  <span className="text-[10px] uppercase tracking-wider text-slate-400 dark:text-slate-500">
                    {region.code}
                  </span>
                </div>

                {/* Selected indicator */}
                {isSelected && (
                  <div className={cn(
                    "flex-shrink-0 w-5 h-5 rounded-full flex items-center justify-center",
                    "bg-primary text-white"
                  )}>
                    <Check className="w-3 h-3" strokeWidth={3} />
                  </div>
                )}
              </button>
            )
          })}
        </div>

        {/* Footer hint */}
        <div className="mt-3 pt-3 border-t border-slate-100 dark:border-slate-800">
          <p className="text-[10px] text-center text-slate-400 dark:text-slate-500">
            Your selection will be saved for future visits
          </p>
        </div>
      </div>
    </div>
  )
}
