import { NextRequest, NextResponse } from 'next/server'
import { getTrackingUrl } from '@/lib/tracking'

export const runtime = 'edge'

const TENANT_ID = process.env.NEXT_PUBLIC_TENANT_ID || '1'
const REDIRECT_VALIDATION_ENABLED = process.env.NEXT_PUBLIC_TRACKING_REDIRECT_VALIDATE !== 'false'
const REDIRECT_DOMAIN_CONFIG = process.env.NEXT_PUBLIC_TRACKING_REDIRECT_DOMAINS || ''
const REDIRECT_ALLOW_HTTP = process.env.NEXT_PUBLIC_TRACKING_REDIRECT_ALLOW_HTTP === 'true'

// 默认允许重定向的域名白名单（联盟网络域名）
const DEFAULT_ALLOWED_REDIRECT_DOMAINS = [
  'admitad.com',
  'ad.admitad.com',
  'click.admitad.com',
  'goto.target.my.com',
  'ad.doubleclick.net',
  'click.linksynergy.com',
  'www.anrdoezrs.net',
  'www.dpbolvw.net',
  'www.jdoqocy.com',
  'www.kqzyfj.com',
  'www.tkqlhce.com',
  // 添加更多联盟域名
]

const ALLOWED_REDIRECT_DOMAINS = REDIRECT_DOMAIN_CONFIG
  .split(',')
  .map(domain => domain.trim())
  .filter(Boolean)

const EFFECTIVE_ALLOWED_DOMAINS = ALLOWED_REDIRECT_DOMAINS.length
  ? ALLOWED_REDIRECT_DOMAINS
  : DEFAULT_ALLOWED_REDIRECT_DOMAINS

/**
 * 验证重定向 URL 是否安全（防止开放重定向攻击）
 */
function isValidRedirectUrl(url: string): boolean {
  if (!REDIRECT_VALIDATION_ENABLED) {
    return true
  }

  try {
    const parsed = new URL(url)
    // 只允许 https（可选允许 http）
    if (parsed.protocol !== 'https:' && !(REDIRECT_ALLOW_HTTP && parsed.protocol === 'http:')) {
      return false
    }
    // 检查域名是否在白名单中
    const hostname = parsed.hostname.toLowerCase()
    return EFFECTIVE_ALLOWED_DOMAINS.some(domain => 
      hostname === domain || hostname.endsWith('.' + domain)
    )
  } catch {
    return false
  }
}

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  const { id } = await params
  
  // 验证 ID 格式（防止注入）
  if (!id || !/^[a-zA-Z0-9_-]+$/.test(id)) {
    return NextResponse.json({ error: 'Invalid offer ID' }, { status: 400 })
  }

  const trackingUrl = getTrackingUrl(id)

  const headers = new Headers()
  headers.set('tenant-id', TENANT_ID)
  headers.set('X-Forwarded-For', request.headers.get('x-forwarded-for') || request.headers.get('x-real-ip') || '')
  headers.set('User-Agent', request.headers.get('user-agent') || '')
  headers.set('Referer', request.headers.get('referer') || '')

  try {
    const response = await fetch(trackingUrl, {
      method: 'GET',
      headers,
      redirect: 'manual'
    })

    const location = response.headers.get('location')
    
    if (location) {
      // 验证重定向 URL 安全性
      if (!isValidRedirectUrl(location)) {
        console.error(`[Tracking] Blocked unsafe redirect: ${location}`)
        return NextResponse.json({ error: 'Invalid redirect destination' }, { status: 400 })
      }
      return NextResponse.redirect(location, 302)
    }

    // 后端返回非重定向响应
    if (!response.ok) {
      return NextResponse.json({ error: 'Offer not found' }, { status: 404 })
    }

    return NextResponse.json({ error: 'Offer not found' }, { status: 404 })
  } catch (error) {
    // 后端不可达或网络错误
    console.error(`[Tracking] Failed to reach tracking server:`, error)
    return NextResponse.json(
      { error: 'Tracking service temporarily unavailable' },
      { status: 502 }
    )
  }
}
