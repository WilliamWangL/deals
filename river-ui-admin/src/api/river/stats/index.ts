import request from '@/config/axios'

export interface DashboardSummaryVO {
  totalClicks: number
  totalConversions: number
  totalRevenue: number
  totalCost: number
  totalProfit: number
  avgEpc: number
  avgCr: number
  avgRoi: number
}

export interface DashboardTrendVO {
  date: string
  clicks: number
  conversions: number
  revenue: number
  cost: number
  profit: number
}

export const getDashboardSummary = () => {
  return request.get<DashboardSummaryVO>({ url: '/stats/dashboard/summary' })
}

export const getDashboardTrend = (params: { startDate?: string; endDate?: string }) => {
  return request.get<DashboardTrendVO[]>({ url: '/stats/dashboard/trend', params })
}
