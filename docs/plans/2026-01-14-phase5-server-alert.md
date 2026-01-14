# Phase 5: river-server Alert 告警逻辑实现

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** 实现 AlertDailyCheckJob 和 AlertHourlyCheckJob 的告警检测逻辑
**Architecture:** 定时任务检测异常指标，触发消息通知
**Tech Stack:** Java 17, Spring Boot 3.5, XXL-Job

## Task 1: 分析现有告警骨架

查找 Alert Job 类和相关配置

## Task 2: 设计告警规则

- 每日检查: 点击量异常下降、转化率异常
- 每小时检查: 实时异常检测

## Task 3: 实现告警逻辑

完善 AlertDailyCheckJob 和 AlertHourlyCheckJob

## Task 4: 集成消息通知

使用现有的消息服务发送告警

## Verification Checklist
- [ ] mvn compile 成功
- [ ] 告警逻辑完整实现
- [ ] 已提交 Git
