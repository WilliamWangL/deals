import request from '@/config/axios'

export interface PostVO {
  id: number | undefined
  title: string
  slug: string
  content: string
  type: number
  status: number
  publishedAt: Date
  creator: string
  createTime: Date
}

// 查询文章分页列表
export const getPostPage = (params: PageParam) => {
  return request.get({ url: '/blog/post/page', params })
}

// 查询文章详情
export const getPost = (id: number) => {
  return request.get({ url: '/blog/post/get?id=' + id })
}

// 新增文章
export const createPost = (data: PostVO) => {
  return request.post({ url: '/blog/post/create', data })
}

// 修改文章
export const updatePost = (data: PostVO) => {
  return request.put({ url: '/blog/post/update', data })
}

// 删除文章
export const deletePost = (id: number) => {
  return request.delete({ url: '/blog/post/delete?id=' + id })
}
