import request from '@/config/axios'

// ==================== 作者 ====================

export interface AuthorVO {
  id: number
  name: string
  slug: string
  avatarUrl: string
  bio: string
  status: number
  createTime: Date
}

export const AuthorApi = {
  getAuthorPage: async (params: any) => {
    return await request.get({ url: `/blog/author/page`, params })
  },
  getAuthorList: async () => {
    return await request.get({ url: `/blog/author/list` })
  },
  getAuthor: async (id: number) => {
    return await request.get({ url: `/blog/author/get?id=` + id })
  },
  createAuthor: async (data: AuthorVO) => {
    return await request.post({ url: `/blog/author/create`, data })
  },
  updateAuthor: async (data: AuthorVO) => {
    return await request.put({ url: `/blog/author/update`, data })
  },
  deleteAuthor: async (id: number) => {
    return await request.delete({ url: `/blog/author/delete?id=` + id })
  }
}

// ==================== 标签 ====================

export interface TagVO {
  id: number
  name: string
  slug: string
  postCount: number
  status: number
  createTime: Date
}

export const TagApi = {
  getTagPage: async (params: any) => {
    return await request.get({ url: `/blog/tag/page`, params })
  },
  getTagList: async () => {
    return await request.get({ url: `/blog/tag/list` })
  },
  getTag: async (id: number) => {
    return await request.get({ url: `/blog/tag/get?id=` + id })
  },
  createTag: async (data: TagVO) => {
    return await request.post({ url: `/blog/tag/create`, data })
  },
  updateTag: async (data: TagVO) => {
    return await request.put({ url: `/blog/tag/update`, data })
  },
  deleteTag: async (id: number) => {
    return await request.delete({ url: `/blog/tag/delete?id=` + id })
  }
}

// ==================== 文章 ====================

export interface PostVO {
  id: number
  authorId: number
  title: string
  slug: string
  content: string
  excerpt: string
  coverImage: string
  type: number
  status: number
  publishedAt: Date
  metaTitle: string
  metaDescription: string
  canonicalUrl: string
  viewCount: number
  featured: boolean
  createTime: Date
}

export const PostApi = {
  getPostPage: async (params: any) => {
    return await request.get({ url: `/blog/post/page`, params })
  },
  getPost: async (id: number) => {
    return await request.get({ url: `/blog/post/get?id=` + id })
  },
  createPost: async (data: PostVO) => {
    return await request.post({ url: `/blog/post/create`, data })
  },
  updatePost: async (data: PostVO) => {
    return await request.put({ url: `/blog/post/update`, data })
  },
  deletePost: async (id: number) => {
    return await request.delete({ url: `/blog/post/delete?id=` + id })
  },
  exportPost: async (params) => {
    return await request.download({ url: `/blog/post/export-excel`, params })
  }
}
