# Phase 3: river-ui-admin River 模块 CRUD 页面

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** 创建 River 广告平台的管理页面 (Merchant/Offer/Campaign/Coupon/Deal/Blog)
**Architecture:** 遵循 yudao-ui-admin Vue 3 + Element Plus 模式
**Tech Stack:** Vue 3, Element Plus, TypeScript

## 需要创建的页面

| 模块 | 路径 | 后端 API 前缀 |
|------|------|--------------|
| 商家管理 | /river/merchant | /admin-api/affiliate/merchant |
| Offer 管理 | /river/offer | /admin-api/affiliate/offer |
| Campaign 管理 | /river/campaign | /admin-api/campaign/campaign |
| 优惠券管理 | /river/coupon | /admin-api/coupon/coupon |
| Deal 管理 | /river/deal | /admin-api/coupon/deal |
| 博客管理 | /river/blog | /admin-api/blog/post |

## Task 1: 分析现有 CRUD 模式

查找现有模块的 CRUD 实现模式:
1. 目录结构
2. API 层定义
3. 页面组件结构
4. 表单组件

## Task 2-7: 创建各模块 CRUD

每个模块需要:
1. API 定义 (src/api/river/{module}/index.ts)
2. 列表页面 (src/views/river/{module}/index.vue)
3. 表单组件 (src/views/river/{module}/{Module}Form.vue)
4. 路由配置

## Verification Checklist
- [ ] pnpm build 成功
- [ ] 所有 6 个模块页面创建
- [ ] API 层与后端匹配
- [ ] 已提交 Git
