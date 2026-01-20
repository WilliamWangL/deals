// 分页配置常量
export const PAGINATION = {
  // 每页显示数量
  PAGE_SIZE: {
    STORE: 12,
    DEAL: 12,
    COUPON: 12,
    BLOG: 9,
    DEFAULT: 12,
  },
  // 分页组件显示的页码数量
  PAGE_RANGE: 5,
  // 默认页码
  DEFAULT_PAGE: 1,
} as const;

// 分页参数类型
export interface PaginationParams {
  pageNo?: number;
  pageSize?: number;
}

// 分页结果类型
export interface PaginationResult<T> {
  total: number;
  list: T[];
}
