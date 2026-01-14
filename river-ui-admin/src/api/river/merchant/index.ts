import request from '@/config/axios'

export interface MerchantVO {
  id: number | undefined
  name: string
  slug: string
  domain: string
  logo: string
  description: string
  status: number
  creator: string
  createTime: Date
}

// 查询商家分页列表
export const getMerchantPage = (params: PageParam) => {
  return request.get({ url: '/affiliate/merchant/page', params })
}

// 查询商家详情
export const getMerchant = (id: number) => {
  return request.get({ url: '/affiliate/merchant/get?id=' + id })
}

// 新增商家
export const createMerchant = (data: MerchantVO) => {
  return request.post({ url: '/affiliate/merchant/create', data })
}

// 修改商家
export const updateMerchant = (data: MerchantVO) => {
  return request.put({ url: '/affiliate/merchant/update', data })
}

// 删除商家
export const deleteMerchant = (id: number) => {
  return request.delete({ url: '/affiliate/merchant/delete?id=' + id })
}
