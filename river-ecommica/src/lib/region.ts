// Server-side region utilities - only use in Server Components
import { cookies, headers } from 'next/headers'
import { getClientIP, getCountryByIP } from '@/lib/geo'

// Re-export constants for convenience
export { REGION_COOKIE_NAME, REGION_COOKIE_MAX_AGE, DEFAULT_REGION, resolveRegion } from '@/lib/region-constants'

/**
 * 在服务端获取当前地区（用于 Server Components/Pages）
 */
export async function getCurrentRegion(searchParams?: { region?: string }): Promise<string> {
  const { resolveRegion, REGION_COOKIE_NAME } = await import('@/lib/region-constants')
  const headersList = await headers()
  const cookieStore = await cookies()

  const ip = getClientIP(headersList)
  const ipCountry = await getCountryByIP(ip)
  const cookieRegion = cookieStore.get(REGION_COOKIE_NAME)?.value

  return resolveRegion(searchParams?.region, cookieRegion, ipCountry)
}
