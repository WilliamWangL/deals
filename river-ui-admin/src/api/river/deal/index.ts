import request from '@/config/axios'

export interface DealVO {
  id: number | undefined
  merchantId: number
  title: string
  description: string
  originalPrice: number
  dealPrice: number
  status: number
  creator: string
  createTime: Date
}

// 查询特惠分页列表
export const getDealPage = (params: PageParam) => {
  return request.get({ url: '/coupon/deal/page', params })
}

// 查询特惠详情
export const getDeal = (id: number) => {
  return request.get({ url: '/coupon/deal/get?id=' + id })
}

// 新增特惠
export const createDeal = (data: DealVO) => {
  return request.post({ url: '/coupon/deal/create', data })
}

// 修改特惠
export const updateDeal = (data: DealVO) => {
  return request.put({ url: '/coupon/deal/update', data })
}

// 删除特惠
export const deleteDeal = (id: number) => {
  return request.delete({ url: '/coupon/deal/delete?id=' + id })
}
