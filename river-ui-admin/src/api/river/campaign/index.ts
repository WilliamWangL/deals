import request from '@/config/axios'

export interface CampaignVO {
  id: number | undefined
  name: string
  type: number
  status: number
  budget: number
  startTime: Date
  endTime: Date
  creator: string
  createTime: Date
}

// 查询活动分页列表
export const getCampaignPage = (params: PageParam) => {
  return request.get({ url: '/campaign/campaign/page', params })
}

// 查询活动详情
export const getCampaign = (id: number) => {
  return request.get({ url: '/campaign/campaign/get?id=' + id })
}

// 新增活动
export const createCampaign = (data: CampaignVO) => {
  return request.post({ url: '/campaign/campaign/create', data })
}

// 修改活动
export const updateCampaign = (data: CampaignVO) => {
  return request.put({ url: '/campaign/campaign/update', data })
}

// 删除活动
export const deleteCampaign = (id: number) => {
  return request.delete({ url: '/campaign/campaign/delete?id=' + id })
}
