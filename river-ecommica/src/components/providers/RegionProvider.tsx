import { headers } from 'next/headers'
import { cookies } from 'next/headers'
import { getClientIP, getCountryByIP } from '@/lib/geo'
import { resolveRegion, REGION_COOKIE_NAME, DEFAULT_REGION } from '@/lib/region-constants'
import { fetchAvailableRegions } from '@/lib/api'

interface RegionData {
  currentRegion: string
  regions: { code: string; name: string }[]
}

export async function getRegionData(searchParams?: { region?: string }): Promise<RegionData> {
  // 获取请求头和 Cookie
  const headersList = await headers()
  const cookieStore = await cookies()

  // 获取客户端 IP 并定位
  const ip = getClientIP(headersList)
  const ipCountry = await getCountryByIP(ip)

  // 获取 Cookie 中的地区
  const cookieRegion = cookieStore.get(REGION_COOKIE_NAME)?.value

  // 解析最终地区
  const currentRegion = resolveRegion(
    searchParams?.region,
    cookieRegion,
    ipCountry
  )

  // 获取可用地区列表
  let regions: { code: string; name: string }[] = []
  try {
    regions = await fetchAvailableRegions()
  } catch (error) {
    console.error('[Region] Failed to fetch regions:', error)
    // 降级为默认 GLOBAL
    regions = [{ code: DEFAULT_REGION, name: 'Global' }]
  }

  return { currentRegion, regions }
}
