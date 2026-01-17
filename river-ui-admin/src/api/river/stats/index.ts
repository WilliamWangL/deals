import request from '@/config/axios'

// ==================== 类型定义 ====================

/** 维度类型枚举 */
export enum DimensionType {
  CAMPAIGN = 1,
  SOURCE = 2,
  OFFER = 3,
  LANDING_PAGE = 4,
  MERCHANT = 5,
  CATEGORY = 6
}

/** 分页请求参数 */
export interface DailyStatsPageReqVO {
  pageNo: number
  pageSize: number
  dimensionType?: number
  dimensionId?: number
  startDate?: string
  endDate?: string
}

/** 查询参数 */
export interface DailyStatsQueryVO {
  dimensionType?: number
  dimensionId?: number
  startDate?: string
  endDate?: string
}

/** 日统计响应 */
export interface DailyStatsRespVO {
  id: number
  date: string
  dimensionType: number
  dimensionId: number
  dimensionName: string
  clicks: number
  conversions: number
  revenue: number
  cost: number
  profit: number
  epc: number
  cr: number
  roi: number
  createTime: string
}

/** 汇总响应 */
export interface DailyStatsSummaryRespVO {
  totalClicks: number
  totalConversions: number
  totalRevenue: number
  totalCost: number
  totalProfit: number
  avgEpc: number
  avgCr: number
  avgRoi: number
}

/** 趋势响应 */
export interface DailyStatsTrendRespVO {
  date: string
  clicks: number
  conversions: number
  revenue: number
  cost: number
  profit: number
}

// ==================== API 接口 ====================

export const DailyStatsApi = {
  /** 分页查询日统计 */
  getPage: async (params: DailyStatsPageReqVO) => {
    return await request.post({ url: `/stats/daily/page`, data: params })
  },

  /** 获取汇总数据 */
  getSummary: async (params: DailyStatsQueryVO) => {
    return await request.get<DailyStatsSummaryRespVO>({ url: `/stats/daily/summary`, params })
  },

  /** 获取趋势数据 */
  getTrend: async (params: DailyStatsQueryVO) => {
    return await request.get<DailyStatsTrendRespVO[]>({ url: `/stats/daily/trend`, params })
  },

  /** 导出 Excel */
  exportExcel: async (params: DailyStatsQueryVO) => {
    return await request.download({ url: `/stats/daily/export-excel`, params })
  }
}
