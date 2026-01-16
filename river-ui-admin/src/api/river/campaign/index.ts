import request from '@/config/axios'

// ==================== 流量来源 ====================

export interface TrafficSourceVO {
  id: number
  code: string
  name: string
  apiCredentials: string
  status: number
  createTime: Date
}

export const TrafficSourceApi = {
  getTrafficSourcePage: async (params: any) => {
    return await request.get({ url: `/campaign/traffic-source/page`, params })
  },
  getTrafficSourceList: async () => {
    return await request.get({ url: `/campaign/traffic-source/list` })
  },
  getTrafficSource: async (id: number) => {
    return await request.get({ url: `/campaign/traffic-source/get?id=` + id })
  },
  createTrafficSource: async (data: TrafficSourceVO) => {
    return await request.post({ url: `/campaign/traffic-source/create`, data })
  },
  updateTrafficSource: async (data: TrafficSourceVO) => {
    return await request.put({ url: `/campaign/traffic-source/update`, data })
  },
  deleteTrafficSource: async (id: number) => {
    return await request.delete({ url: `/campaign/traffic-source/delete?id=` + id })
  }
}

// ==================== Campaign ====================

export interface CampaignVO {
  id: number
  trafficSourceId: number
  name: string
  type: number
  offerIds: string
  landingPageId: number
  budgetDaily: number
  budgetTotal: number
  externalCampaignId: string
  status: number
  createTime: Date
}

export const CampaignApi = {
  getCampaignPage: async (params: any) => {
    return await request.get({ url: `/campaign/campaign/page`, params })
  },
  getCampaign: async (id: number) => {
    return await request.get({ url: `/campaign/campaign/get?id=` + id })
  },
  createCampaign: async (data: CampaignVO) => {
    return await request.post({ url: `/campaign/campaign/create`, data })
  },
  updateCampaign: async (data: CampaignVO) => {
    return await request.put({ url: `/campaign/campaign/update`, data })
  },
  deleteCampaign: async (id: number) => {
    return await request.delete({ url: `/campaign/campaign/delete?id=` + id })
  },
  exportCampaign: async (params) => {
    return await request.download({ url: `/campaign/campaign/export-excel`, params })
  }
}

// ==================== 广告组 ====================

export interface AdGroupVO {
  id: number
  campaignId: number
  name: string
  targeting: string
  bidStrategy: string
  externalAdGroupId: string
  status: number
  createTime: Date
}

export const AdGroupApi = {
  getAdGroupPage: async (params: any) => {
    return await request.get({ url: `/campaign/ad-group/page`, params })
  },
  getAdGroup: async (id: number) => {
    return await request.get({ url: `/campaign/ad-group/get?id=` + id })
  },
  createAdGroup: async (data: AdGroupVO) => {
    return await request.post({ url: `/campaign/ad-group/create`, data })
  },
  updateAdGroup: async (data: AdGroupVO) => {
    return await request.put({ url: `/campaign/ad-group/update`, data })
  },
  deleteAdGroup: async (id: number) => {
    return await request.delete({ url: `/campaign/ad-group/delete?id=` + id })
  }
}

// ==================== 落地页 ====================

export interface LandingPageVO {
  id: number
  name: string
  slug: string
  type: number
  url: string
  offerId: number
  content: string
  status: number
  createTime: Date
}

export const LandingPageApi = {
  getLandingPagePage: async (params: any) => {
    return await request.get({ url: `/campaign/landing-page/page`, params })
  },
  getLandingPageList: async () => {
    return await request.get({ url: `/campaign/landing-page/list` })
  },
  getLandingPage: async (id: number) => {
    return await request.get({ url: `/campaign/landing-page/get?id=` + id })
  },
  createLandingPage: async (data: LandingPageVO) => {
    return await request.post({ url: `/campaign/landing-page/create`, data })
  },
  updateLandingPage: async (data: LandingPageVO) => {
    return await request.put({ url: `/campaign/landing-page/update`, data })
  },
  deleteLandingPage: async (id: number) => {
    return await request.delete({ url: `/campaign/landing-page/delete?id=` + id })
  }
}

// ==================== 成本记录 ====================

export interface CostRecordVO {
  id: number
  campaignId: number
  adGroupId: number
  date: Date
  impressions: number
  clicks: number
  cost: number
  currency: string
  source: number
  createTime: Date
}

export const CostRecordApi = {
  getCostRecordPage: async (params: any) => {
    return await request.get({ url: `/campaign/cost-record/page`, params })
  },
  createCostRecord: async (data: CostRecordVO) => {
    return await request.post({ url: `/campaign/cost-record/create`, data })
  },
  updateCostRecord: async (data: CostRecordVO) => {
    return await request.put({ url: `/campaign/cost-record/update`, data })
  },
  deleteCostRecord: async (id: number) => {
    return await request.delete({ url: `/campaign/cost-record/delete?id=` + id })
  }
}

// ==================== 货币 ====================

export interface CurrencyVO {
  id: number
  code: string
  name: string
  symbol: string
  decimalPlaces: number
  status: number
  createTime: Date
}

export const CurrencyApi = {
  getCurrencyPage: async (params: any) => {
    return await request.get({ url: `/campaign/currency/page`, params })
  },
  getCurrencyList: async () => {
    return await request.get({ url: `/campaign/currency/list` })
  },
  getCurrency: async (id: number) => {
    return await request.get({ url: `/campaign/currency/get?id=` + id })
  },
  createCurrency: async (data: CurrencyVO) => {
    return await request.post({ url: `/campaign/currency/create`, data })
  },
  updateCurrency: async (data: CurrencyVO) => {
    return await request.put({ url: `/campaign/currency/update`, data })
  },
  deleteCurrency: async (id: number) => {
    return await request.delete({ url: `/campaign/currency/delete?id=` + id })
  }
}

// ==================== 汇率 ====================

export interface FxRateVO {
  id: number
  fromCurrency: string
  toCurrency: string
  rate: number
  effectiveDate: Date
  createTime: Date
}

export const FxRateApi = {
  getFxRatePage: async (params: any) => {
    return await request.get({ url: `/campaign/fx-rate/page`, params })
  },
  getFxRate: async (id: number) => {
    return await request.get({ url: `/campaign/fx-rate/get?id=` + id })
  },
  createFxRate: async (data: FxRateVO) => {
    return await request.post({ url: `/campaign/fx-rate/create`, data })
  },
  updateFxRate: async (data: FxRateVO) => {
    return await request.put({ url: `/campaign/fx-rate/update`, data })
  },
  deleteFxRate: async (id: number) => {
    return await request.delete({ url: `/campaign/fx-rate/delete?id=` + id })
  }
}
