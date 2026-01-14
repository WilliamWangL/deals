import { ulid } from 'ulid'

export function generateClickId(): string {
  return ulid()
}

export function getTrackingUrl(offerId: string): string {
  const apiBase = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:48080'
  return `${apiBase}/api/go/${offerId}`
}
