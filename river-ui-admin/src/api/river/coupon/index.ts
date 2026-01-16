import request from '@/config/axios'

// ==================== 优惠券 ====================

export interface CouponVO {
  id: number
  merchantId: number
  offerId: number
  code: string
  discountType: number
  discountValue: number
  minPurchase: number
  startTime: Date
  endTime: Date
  terms: string
  source: number
  verified: boolean
  hotScore: number
  status: number
  createTime: Date
}

export const CouponApi = {
  // 查询优惠券分页
  getCouponPage: async (params: any) => {
    return await request.get({ url: `/coupon/coupon/page`, params })
  },

  // 查询优惠券详情
  getCoupon: async (id: number) => {
    return await request.get({ url: `/coupon/coupon/get?id=` + id })
  },

  // 新增优惠券
  createCoupon: async (data: CouponVO) => {
    return await request.post({ url: `/coupon/coupon/create`, data })
  },

  // 修改优惠券
  updateCoupon: async (data: CouponVO) => {
    return await request.put({ url: `/coupon/coupon/update`, data })
  },

  // 删除优惠券
  deleteCoupon: async (id: number) => {
    return await request.delete({ url: `/coupon/coupon/delete?id=` + id })
  },

  // 导出优惠券 Excel
  exportCoupon: async (params) => {
    return await request.download({ url: `/coupon/coupon/export-excel`, params })
  }
}

// ==================== Deal ====================

export interface DealVO {
  id: number
  merchantId: number
  offerId: number
  title: string
  slug: string
  description: string
  originalPrice: number
  dealPrice: number
  discountPercent: number
  startTime: Date
  endTime: Date
  stockLimit: number
  imageUrl: string
  hotScore: number
  featured: boolean
  status: number
  createTime: Date
}

export const DealApi = {
  // 查询 Deal 分页
  getDealPage: async (params: any) => {
    return await request.get({ url: `/coupon/deal/page`, params })
  },

  // 查询 Deal 详情
  getDeal: async (id: number) => {
    return await request.get({ url: `/coupon/deal/get?id=` + id })
  },

  // 新增 Deal
  createDeal: async (data: DealVO) => {
    return await request.post({ url: `/coupon/deal/create`, data })
  },

  // 修改 Deal
  updateDeal: async (data: DealVO) => {
    return await request.put({ url: `/coupon/deal/update`, data })
  },

  // 删除 Deal
  deleteDeal: async (id: number) => {
    return await request.delete({ url: `/coupon/deal/delete?id=` + id })
  },

  // 导出 Deal Excel
  exportDeal: async (params) => {
    return await request.download({ url: `/coupon/deal/export-excel`, params })
  }
}
