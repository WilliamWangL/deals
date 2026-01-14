# Phase 4: river-ui-admin Dashboard 真实数据接入

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** 将 Dashboard 硬编码 Mock 数据替换为调用 river-server 真实统计 API
**Architecture:** 调用 /admin-api/river/stats/summary 获取统计数据
**Tech Stack:** Vue 3, TypeScript, axios

## Task 1: 分析现有 Dashboard

查找 Dashboard 组件并分析 Mock 数据结构

## Task 2: 查找后端统计 API

确认 /admin-api/river/stats/* 端点和响应格式

## Task 3: 创建 stats API 层

**Files:** `src/api/river/stats/index.ts`

## Task 4: 更新 Dashboard 调用真实 API

**Files:** `src/views/river/dashboard/index.vue`

## Verification Checklist
- [ ] pnpm build 成功
- [ ] Dashboard 调用真实 API
- [ ] 已提交 Git
