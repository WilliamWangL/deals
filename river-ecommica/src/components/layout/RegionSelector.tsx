'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { setCookie } from 'cookies-next'
import { Globe, ChevronDown, Check } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { cn } from '@/lib/utils'
import { REGION_COOKIE_NAME, REGION_COOKIE_MAX_AGE } from '@/lib/region-constants'

interface Region {
  code: string
  name: string
  count: number
}

interface RegionSelectorProps {
  currentRegion: string
  regions: Region[]
}

export function RegionSelector({ currentRegion, regions }: RegionSelectorProps) {
  const router = useRouter()
  const [isOpen, setIsOpen] = useState(false)

  const currentRegionData = regions.find(r => r.code === currentRegion) || regions[0] || { code: 'GLOBAL', name: 'Global', count: 0 }

  const handleSelect = (code: string) => {
    setCookie(REGION_COOKIE_NAME, code, { maxAge: REGION_COOKIE_MAX_AGE })
    setIsOpen(false)
    router.refresh()
  }

  return (
    <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="sm"
          className="h-9 px-2 gap-1.5 text-muted-foreground hover:text-foreground"
        >
          <Globe className="h-4 w-4" />
          <span className="hidden sm:inline text-sm">
            {currentRegionData?.name || 'Global'}
          </span>
          <ChevronDown className="h-3 w-3" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-48 max-h-64 overflow-y-auto">
        {regions.map((region) => (
          <DropdownMenuItem
            key={region.code}
            onClick={() => handleSelect(region.code)}
            className={cn(
              "flex items-center justify-between cursor-pointer",
              currentRegion === region.code && "bg-muted"
            )}
          >
            <span>{region.name}</span>
            <div className="flex items-center gap-2">
              <span className="text-xs text-muted-foreground">{region.count}</span>
              {currentRegion === region.code && (
                <Check className="h-4 w-4 text-primary" />
              )}
            </div>
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
