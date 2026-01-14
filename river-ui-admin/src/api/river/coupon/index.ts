import request from '@/config/axios'

export interface CouponVO {
  id: number | undefined
  merchantId: number
  code: string
  description: string
  discountType: number
  discountValue: number
  status: number
  creator: string
  createTime: Date
}

// 查询优惠券分页列表
export const getCouponPage = (params: PageParam) => {
  return request.get({ url: '/coupon/coupon/page', params })
}

// 查询优惠券详情
export const getCoupon = (id: number) => {
  return request.get({ url: '/coupon/coupon/get?id=' + id })
}

// 新增优惠券
export const createCoupon = (data: CouponVO) => {
  return request.post({ url: '/coupon/coupon/create', data })
}

// 修改优惠券
export const updateCoupon = (data: CouponVO) => {
  return request.put({ url: '/coupon/coupon/update', data })
}

// 删除优惠券
export const deleteCoupon = (id: number) => {
  return request.delete({ url: '/coupon/coupon/delete?id=' + id })
}
