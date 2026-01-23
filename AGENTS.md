# AGENTS.md

> **Parent Document**: This is the root configuration. Subprojects inherit these universal conventions.

本文件为 River 广告平台 Monorepo 的开发指南，供 AI Agent 使用。

## 项目结构

```
river-ad-workspace/
├── river-server/       # Java 17 + Spring Boot 3.5 后端 API 服务
├── river-ui-admin/     # Vue 3 + Element Plus + TypeScript 管理后台
└── river-ecommica/     # Next.js 16 + React 19 + Tailwind CSS 4 优惠聚合站点
```

## 通用规范

### Git 使用
- **禁止主动提交代码**：除非用户明确要求，否则不要创建 Git 提交
- **禁止硬删除**：始终使用软删除（`deleted` 字段）
- **遵循现有模式**：创建新代码前先查看类似模块

### 数据库
- **多租户**：所有业务表必须包含 `tenant_id` 字段
- **表前缀**：新模块使用 `river_`，框架使用 `system_`/`infra_`

---

## 子项目索引

> 子项目 AGENTS.md 通过相对路径引用：

| 子项目 | AGENTS.md 路径 | 技术栈分类 |
|--------|----------------|-----------|
| 后端服务 | `river-server/AGENTS.md` | Java 后端 |
| 管理后台 | `river-ui-admin/AGENTS.md` | Vue3 前端 |
| 前端站点 | `river-ecommica/AGENTS.md` | Next.js 前端 |

---

## 开发工作流

### 禁用操作

| 操作 | 原因 |
|------|------|
| 提交代码（未明确要求） | 避免干扰用户 |
| 硬删除数据 | 保留审计追踪 |
| 类型错误抑制 | 破坏类型安全 |
| 删除失败测试 | 掩盖问题 |

---

## 前端 UI/UX 变更

**重要**：如果任务涉及样式、布局、动画、颜色等**视觉变更**，必须使用 `frontend-design` skill 处理。

**可自行处理**：API 调用、数据处理、业务逻辑、状态管理。
