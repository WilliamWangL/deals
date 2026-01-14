import request from '@/config/axios'

export interface OfferVO {
  id: number | undefined
  merchantId: number
  name: string
  description: string
  commission: number
  trackingUrl: string
  status: number
  creator: string
  createTime: Date
}

// 查询优惠分页列表
export const getOfferPage = (params: PageParam) => {
  return request.get({ url: '/affiliate/offer/page', params })
}

// 查询优惠详情
export const getOffer = (id: number) => {
  return request.get({ url: '/affiliate/offer/get?id=' + id })
}

// 新增优惠
export const createOffer = (data: OfferVO) => {
  return request.post({ url: '/affiliate/offer/create', data })
}

// 修改优惠
export const updateOffer = (data: OfferVO) => {
  return request.put({ url: '/affiliate/offer/update', data })
}

// 删除优惠
export const deleteOffer = (id: number) => {
  return request.delete({ url: '/affiliate/offer/delete?id=' + id })
}
