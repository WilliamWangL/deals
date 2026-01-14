# Phase 1: river-ecommica 追踪系统实现

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** 实现 ecommica 站点的联盟跳转追踪功能
**Architecture:** Next.js App Router + Edge Runtime，调用 river-server 记录点击
**Tech Stack:** Next.js 15, TypeScript, ulid

## Task 1: 创建 lib/tracking.ts

**Files:** `river-ecommica/lib/tracking.ts`

**Steps:**
1. 安装 ulid 依赖: `pnpm add ulid`
2. 创建 tracking.ts 文件
3. 实现以下函数:
   - `generateClickId()`: 使用 ulid 生成唯一 ID
   - `trackClick(offerId, meta)`: 调用后端 API 记录点击
   - `getTrackingUrl(offerId, clickId)`: 生成联盟跳转 URL

**Verification:** 
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): add tracking utility functions`

## Task 2: 创建 /api/go/[id]/route.ts

**Files:** `river-ecommica/app/api/go/[id]/route.ts`

**Steps:**
1. 创建 API 路由文件
2. 使用 Edge Runtime 以获得低延迟
3. 实现逻辑:
   - 解析 offerId 参数
   - 生成 clickId
   - 调用 trackClick 记录
   - 302 重定向到联盟链接

**Verification:**
```bash
cd river-ecommica && pnpm build
curl -I http://localhost:3000/api/go/test-offer-id
```

**Commit:** `feat(ecommica): add /api/go tracking redirect route`

## Task 3: 更新 DealCard 使用追踪链接

**Files:** `river-ecommica/components/deal-card.tsx`

**Steps:**
1. 查找 DealCard 组件
2. 将 affiliate URL 改为 `/api/go/{offerId}`
3. 确保点击事件正确触发

**Verification:**
```bash
cd river-ecommica && pnpm build
```

**Commit:** `feat(ecommica): update DealCard to use tracking links`

## Verification Checklist
- [ ] pnpm build 成功
- [ ] /api/go/[id] 路由存在
- [ ] lib/tracking.ts 导出所有必要函数
- [ ] DealCard 使用 /api/go 链接
- [ ] 所有文件已提交 Git
