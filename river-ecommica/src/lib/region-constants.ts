// Region constants - safe to import from both client and server components

export const REGION_COOKIE_NAME = 'region'
export const REGION_COOKIE_MAX_AGE = 30 * 24 * 60 * 60 // 30 days
export const DEFAULT_REGION = 'GLOBAL'

// 后端使用的全球地区代码映射
export const GLOBAL_REGION_CODE = 'GLOBAL'

/**
 * 解析地区优先级
 * URL 参数 > Cookie > IP 定位 > 默认全球
 */
export function resolveRegion(
  urlParam: string | null | undefined,
  cookieValue: string | null | undefined,
  ipCountry: string | null | undefined
): string {
  if (urlParam && urlParam !== '') {
    return urlParam.toUpperCase()
  }
  if (cookieValue && cookieValue !== '') {
    return cookieValue.toUpperCase()
  }
  if (ipCountry && ipCountry !== '') {
    return ipCountry.toUpperCase()
  }
  return DEFAULT_REGION
}
