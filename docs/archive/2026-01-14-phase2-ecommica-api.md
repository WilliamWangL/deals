# Phase 2: river-ecommica Mock 数据替换为真实 API

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** 将 ecommica 的 Mock 数据替换为调用 river-server 真实 API
**Architecture:** Next.js Server Components 调用 river-server /app-api/*
**Tech Stack:** Next.js 16, TypeScript, fetch API

## Task 1: 分析现有 API 层

**Files:** `river-ecommica/src/lib/api.ts`

**Steps:**
1. 读取现有 api.ts，了解 Mock 数据结构
2. 识别需要替换的函数

**Verification:** 确认 Mock 函数列表

## Task 2: 查找 river-server App API 端点

**Files:** river-server/src/**/controller/*AppController.java

**Steps:**
1. 找到所有 /app-api/ 端点
2. 确认请求/响应格式

## Task 3: 重写 lib/api.ts

**Files:** `river-ecommica/src/lib/api.ts`

**Steps:**
1. 定义 API_BASE 环境变量
2. 实现真实 API 调用函数:
   - getStores() → /app-api/affiliate/merchant/list
   - getDeals() → /app-api/deal/list
   - getCoupons() → /app-api/coupon/list  
   - getBlogPosts() → /app-api/blog/post/list
3. 保持接口兼容

**Verification:**
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): replace mock data with real API calls`

## Verification Checklist
- [ ] pnpm build 成功
- [ ] lib/api.ts 调用真实 API
- [ ] 类型定义与后端响应匹配
- [ ] 已提交 Git
