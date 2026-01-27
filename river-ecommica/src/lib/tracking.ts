import { ulid } from 'ulid'

export function generateClickId(): string {
  return ulid()
}

/**
 * 获取追踪链接路径
 * 直接走 nginx 代理到后端，不经过 Next.js 代理层
 */
export function getTrackingLink(trackingLinkId?: string, fallbackUrl: string = '#'): string {
  if (trackingLinkId) {
    return `/go/${trackingLinkId}`
  }
  return fallbackUrl
}
