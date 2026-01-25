import { Reader, ReaderModel, Country } from '@maxmind/geoip2-node'
import path from 'path'
import fs from 'fs'

let reader: ReaderModel | null = null
let readerError: Error | null = null

/**
 * Trusted proxy CIDR ranges.
 * Only IPs from these ranges should be trusted for X-Forwarded-For header processing.
 * Cloudflare IP ranges as of 2024.
 * See: https://www.cloudflare.com/ips/
 */
const TRUSTED_PROXY_CIDRS = [
  '103.21.244.0/22',
  '103.22.200.0/22',
  '103.31.4.0/22',
  '104.16.0.0/13',
  '104.24.0.0/14',
  '108.162.192.0/18',
  '131.0.72.0/22',
  '141.101.64.0/18',
  '162.158.0.0/15',
  '172.64.0.0/13',
  '173.245.48.0/20',
  '188.114.96.0/20',
  '190.93.240.0/20',
  '197.234.240.0/22',
  '198.41.128.0/17',
  '2400:cb00::/32',
  '2606:4700::/32',
  '2803:f800::/32',
  '2405:b500::/32',
  '2405:8100::/32',
  '2a06:98c0::/29',
  '2c0f:f248::/32',
] as const

/**
 * Check if an IP is within a CIDR range
 */
function isInCidrRange(ip: string, cidr: string): boolean {
  const [range, bits] = cidr.split('/')
  const mask = parseInt(bits, 10)

  const ipParts = ip.includes(':')
    ? ip.split(':').map(p => parseInt(p, 16))
    : ip.split('.').map(Number)
  const rangeParts = range.includes(':')
    ? range.split(':').map(p => parseInt(p, 16))
    : range.split('.').map(Number)

  const numParts = Math.max(ipParts.length, rangeParts.length)
  let ipNum = 0n
  let rangeNum = 0n
  let bitShift = numParts === 4 ? 24 : 120

  for (let i = 0; i < numParts; i++) {
    const ipVal = ipParts[i] || 0
    const rangeVal = rangeParts[i] || 0
    ipNum = (BigInt(ipNum) << BigInt(bitShift)) + BigInt(ipVal)
    rangeNum = (BigInt(rangeNum) << BigInt(bitShift)) + BigInt(rangeVal)
    bitShift -= 8
  }

  const maskBits = numParts === 4 ? 32 : 128
  const maskNum = ~((1n << BigInt(maskBits - mask)) - 1n)

  return (ipNum & maskNum) === (rangeNum & maskNum)
}

/**
 * Check if IP is from a trusted proxy
 */
function isTrustedProxy(ip: string): boolean {
  return TRUSTED_PROXY_CIDRS.some(cidr => isInCidrRange(ip, cidr))
}

/**
 * 初始化 GeoIP Reader（单例模式）
 */
async function initGeoReader(): Promise<ReaderModel | null> {
  if (reader) return reader
  if (readerError) return null

  const dbPath = path.join(process.cwd(), 'data', 'GeoLite2-Country.mmdb')

  // 检查文件是否存在
  if (!fs.existsSync(dbPath)) {
    console.warn('[GeoIP] GeoLite2-Country.mmdb not found at:', dbPath)
    readerError = new Error('Database file not found')
    return null
  }

  try {
    reader = await Reader.open(dbPath)
    console.log('[GeoIP] Database loaded successfully')
    return reader
  } catch (error) {
    console.error('[GeoIP] Failed to load database:', error)
    readerError = error as Error
    return null
  }
}

/**
 * 根据 IP 获取国家代码
 */
function isLocalIP(ip: string): boolean {
  return (
    ip === '::1' ||
    ip === '127.0.0.1' ||
    ip.startsWith('192.168.') ||
    ip.startsWith('10.') ||
    ip.startsWith('::ffff:127.') ||
    ip.startsWith('::ffff:192.168.') ||
    ip.startsWith('::ffff:10.')
  )
}

export async function getCountryByIP(ip: string): Promise<string | null> {
  // 处理本地开发环境的 IP
  if (!ip || isLocalIP(ip)) {
    return null
  }

  const r = await initGeoReader()
  if (!r) return null

  try {
    const response: Country = r.country(ip)
    return response.country?.isoCode || null
  } catch (error) {
    // IP 不在数据库中（如保留 IP）
    console.debug('[GeoIP] Failed to lookup IP:', ip, error)
    return null
  }
}

/**
 * 从请求头中获取客户端 IP
 *
 * Security: This function validates IP headers against trusted proxy ranges
 * to prevent IP spoofing attacks. Only CF-Connecting-IP from trusted Cloudflare
 * IPs, or X-Forwarded-For/X-Real-IP from trusted proxies are accepted.
 *
 * @param headers - HTTP request headers
 * @param remoteIP - The actual remote IP address of the connection (optional, for validation)
 * @returns The client's real IP address, or empty string if cannot be determined securely
 */
export function getClientIP(headers: Headers, remoteIP?: string): string {
  // Priority 1: Cloudflare connecting IP - only trust if request comes from Cloudflare
  const cfIP = headers.get('cf-connecting-ip')
  if (cfIP) {
    // Validate that the remote connection is from a trusted proxy
    if (remoteIP && isTrustedProxy(remoteIP)) {
      return cfIP.trim()
    }
    // If we can't verify the source, fall back to trusting CF only in development
    if (process.env.NODE_ENV === 'development') {
      console.debug('[GeoIP] CF IP received but cannot verify proxy, trusting in dev mode')
      return cfIP.trim()
    }
    // In production without verification, reject the header
    console.warn('[GeoIP] CF IP received from untrusted source, rejecting')
    return ''
  }

  // Priority 2: X-Forwarded-For - parse from right to left, ignoring trusted proxies
  const forwardedFor = headers.get('x-forwarded-for')
  if (forwardedFor) {
    const ips = forwardedFor.split(',').map(ip => ip.trim())

    // Walk from right to left, find first non-trusted IP
    for (let i = ips.length - 1; i >= 0; i--) {
      const ip = ips[i]
      // If this IP is NOT a trusted proxy, it's the client IP
      if (!isTrustedProxy(ip)) {
        return ip
      }
    }

    // All IPs are trusted proxies - use the leftmost or verify with remote IP
    if (remoteIP && isTrustedProxy(remoteIP)) {
      return ips[0] // Return first (original client) when behind trusted proxy
    }

    // No trusted proxy validation available
    if (process.env.NODE_ENV === 'development' && ips.length > 0) {
      console.debug('[GeoIP] X-Forwarded-For received without proxy validation, trusting in dev mode')
      return ips[0]
    }

    console.warn('[GeoIP] X-Forwarded-For received but no trusted proxy verification')
    return ''
  }

  // Priority 3: X-Real-IP - only trust if request comes from trusted proxy
  const realIP = headers.get('x-real-ip')
  if (realIP) {
    if (remoteIP && isTrustedProxy(remoteIP)) {
      return realIP.trim()
    }
    if (process.env.NODE_ENV === 'development') {
      console.debug('[GeoIP] X-Real-IP received but cannot verify proxy, trusting in dev mode')
      return realIP.trim()
    }
    console.warn('[GeoIP] X-Real-IP received from untrusted source, rejecting')
    return ''
  }

  return ''
}
