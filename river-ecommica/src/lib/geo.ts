import { Reader, ReaderModel, Country } from '@maxmind/geoip2-node'
import path from 'path'
import fs from 'fs'

let reader: ReaderModel | null = null
let readerError: Error | null = null

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
export async function getCountryByIP(ip: string): Promise<string | null> {
  // 处理本地开发环境的 IP
  if (!ip || ip === '::1' || ip === '127.0.0.1' || ip.startsWith('192.168.') || ip.startsWith('10.')) {
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
 */
export function getClientIP(headers: Headers): string {
  // 优先级：Cloudflare > X-Forwarded-For > X-Real-IP
  const cfIP = headers.get('cf-connecting-ip')
  if (cfIP) return cfIP

  const forwardedFor = headers.get('x-forwarded-for')
  if (forwardedFor) {
    // X-Forwarded-For 可能包含多个 IP，取第一个
    return forwardedFor.split(',')[0].trim()
  }

  const realIP = headers.get('x-real-ip')
  if (realIP) return realIP

  return ''
}
