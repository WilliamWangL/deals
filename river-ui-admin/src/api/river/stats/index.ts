import request from '@/config/axios'

// ==================== 日报统计 ====================

export interface DailyStatsVO {
  id: number
  date: Date
  dimensionType: number
  dimensionId: number
  clicks: number
  conversions: number
  revenue: number
  cost: number
  profit: number
  epc: number
  cr: number
  roi: number
}

export const DailyStatsApi = {
  getDailyStatsPage: async (params: any) => {
    return await request.get({ url: `/stats/daily/page`, params })
  },
  getDailyStats: async (id: number) => {
    return await request.get({ url: `/stats/daily/get?id=` + id })
  },
  exportDailyStats: async (params) => {
    return await request.download({ url: `/stats/daily/export-excel`, params })
  }
}

// ==================== 小时统计 ====================

export interface HourlyStatsVO {
  id: number
  hour: Date
  dimensionType: number
  dimensionId: number
  clicks: number
  conversions: number
  revenue: number
  cost: number
}

export const HourlyStatsApi = {
  getHourlyStatsPage: async (params: any) => {
    return await request.get({ url: `/stats/hourly/page`, params })
  },
  getHourlyStats: async (id: number) => {
    return await request.get({ url: `/stats/hourly/get?id=` + id })
  },
  exportHourlyStats: async (params) => {
    return await request.download({ url: `/stats/hourly/export-excel`, params })
  }
}
