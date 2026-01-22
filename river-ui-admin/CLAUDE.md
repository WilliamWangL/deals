# CLAUDE.md - River UI Admin

管理后台，基于 Vue 3 + Element Plus + TypeScript + Vite 5。

## 常用命令

```bash
pnpm install      # 安装依赖
pnpm dev          # 本地开发（连接本地后端）
pnpm dev-server   # 连接开发服务器
pnpm ts:check     # 类型检查
pnpm build:local  # 本地环境构建
pnpm build:prod   # 生产环境构建
pnpm lint:eslint  # 代码检查
```

## 目录结构

```
src/
├── api/              # API 调用模块
│   ├── river/        # River 业务模块 API
│   ├── system/       # 系统模块 API
│   └── infra/        # 基础设施 API
├── views/            # 页面组件
│   └── river/        # River 业务模块页面
├── components/       # 公共组件
├── router/           # 路由配置
├── store/            # Pinia 状态管理
├── utils/            # 工具函数
├── types/            # TypeScript 类型定义
├── styles/           # 全局样式
├── hooks/            # 组合式函数
├── layout/           # 布局组件
└── locales/          # 国际化文件
```

## 开发规范

### 组件命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 页面组件 | `{Entity}Index.vue` / `{Entity}Form.vue` | `MerchantIndex.vue`, `MerchantForm.vue` |
| 公共组件 | `{Feature}Component.vue` | `ImageUpload.vue`, `DictSelect.vue` |
| 布局组件 | `{Layout}Layout.vue` | `DefaultLayout.vue` |
| 弹窗组件 | `{Entity}Dialog.vue` | `MerchantDialog.vue` |

### API 模块结构

```
src/api/river/{module}/
├── index.ts          # 主要 CRUD 接口
└── types.ts          # TypeScript 类型定义（可选）
```

**API 函数命名**：

| 操作 | 命名规则 | 示例 |
|------|----------|------|
| 创建 | `create{Entity}` | `createMerchant` |
| 更新 | `update{Entity}` | `updateMerchant` |
| 删除 | `delete{Entity}` | `deleteMerchant` |
| 查询单个 | `get{Entity}` | `getMerchant` |
| 查询列表 | `get{Entity}List` | `getMerchantList` |
| 分页查询 | `get{Entity}Page` | `getMerchantPage` |

### API 调用模式

```typescript
import { getMerchantPage, createMerchant } from '@/api/river/affiliate'

// 分页查询
const { data } = await getMerchantPage({ pageNo: 1, pageSize: 10 })

// 创建
await createMerchant(formData)
```

### 状态管理

使用 Pinia store：

```typescript
import { useUserStore } from '@/store/modules/user'
const userStore = useUserStore()
```

### 样式规范

- 使用 UnoCSS + Tailwind CSS
- SCSS 变量定义在 `src/styles/variables.scss`
- 组件样式使用 `<style scoped>`

## 配置文件

| 文件 | 说明 |
|------|------|
| `.env.local` | 本地开发环境 |
| `.env.dev` | 开发服务器环境 |
| `.env.prod` | 生产环境 |

## 重要注意事项

1. **遵循现有模式** — 创建新页面/组件前先查看类似模块
2. **类型安全** — 充分利用 TypeScript，使用 `types` 目录定义类型
3. **API 路径一致** — 前端 API 路径与后端 Controller 路径保持一致
4. **组件复用** — 优先使用 `components/` 中的公共组件
5. **未明确要求不得 commit** — 不要主动创建 Git 提交
