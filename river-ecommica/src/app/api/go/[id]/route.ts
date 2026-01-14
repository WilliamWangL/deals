import { NextRequest, NextResponse } from 'next/server'
import { getTrackingUrl } from '@/lib/tracking'

export const runtime = 'edge'

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  const { id } = await params
  const trackingUrl = getTrackingUrl(id)

  const headers = new Headers()
  headers.set('X-Forwarded-For', request.headers.get('x-forwarded-for') || request.headers.get('x-real-ip') || '')
  headers.set('User-Agent', request.headers.get('user-agent') || '')
  headers.set('Referer', request.headers.get('referer') || '')

  const response = await fetch(trackingUrl, {
    method: 'GET',
    headers,
    redirect: 'manual'
  })

  const location = response.headers.get('location')
  if (location) {
    return NextResponse.redirect(location, 302)
  }

  return NextResponse.json({ error: 'Offer not found' }, { status: 404 })
}
