# AGENTS.md - river-ui-admin

本文件为 river-ui-admin（Vue 3 管理后台）开发指南，供 AI Agent 使用。

## 常用命令

```bash
cd river-ui-admin

# 安装依赖（强制使用 pnpm）
pnpm install

# 本地开发（连接本地后端 env.local）
pnpm dev

# 开发服务器（连接开发服务器）
pnpm dev-server

# 类型检查
pnpm ts:check

# 构建
pnpm build:local   # 本地环境
pnpm build:dev     # 开发环境
pnpm build:prod    # 生产环境

# 代码检查与修复
pnpm lint:eslint   # ESLint 检查
pnpm lint:format   # Prettier 格式化
pnpm lint:style    # Stylelint 检查

# 清理
pnpm clean              # 删除 node_modules
pnpm clean:cache        # 删除缓存
```

## TypeScript 配置

```json
{
  "compilerOptions": {
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "paths": {
      "@/*": ["src/*"]
    }
  }
}
```

## 代码风格

### Vue 组件结构

```vue
<script setup lang="ts">
// 1. 导入（第三方 → 项目内部）
// 2. 类型定义
// 3. Props/Emits
// 4. Ref/Reactive
// 5. Computed
// 6. Watch
// 7. 生命周期
// 8. 方法
</script>

<template>
<!-- 模板内容 -->
</template>

<style lang="scss" scoped>
/* 样式 */
</style>
```

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件文件 | PascalCase | `UserDialog.vue` |
| 组合式函数 | use 前缀 | `useUserStore.ts` |
| 工具函数 | camelCase | `formatDate.ts` |
| 常量 | UPPER_SNAKE_CASE 或 camelCase | - |

### 导入顺序

1. Vue/路由相关（vue, vue-router）
2. Element Plus 组件
3. 第三方库
4. `@/` 项目路径
5. `./` 相对路径

### 样式规范

- 使用 SCSS
- 使用 `<style lang="scss" scoped>`
- 使用 UnoCSS 原子类（可选）
- 缩进大小：2 空格
- 最大行长度：100 字符

### 类型安全（硬性禁止）

```typescript
// 禁止
as any
@ts-ignore
@ts-expect-error
catch(e) {}  // 空捕获块
```

## 项目结构

```
src/
├── api/              # API 调用模块（按业务模块组织）
│   ├── river/        # River 业务模块 API
│   │   ├── affiliate/
│   │   ├── campaign/
│   │   ├── coupon/
│   │   ├── blog/
│   │   ├── stats/
│   │   └── tracking/
│   ├── system/       # 系统模块 API
│   └── infra/        # 基础设施 API
├── views/            # 页面组件
│   └── river/        # River 业务模块页面
├── components/       # 公共组件
├── router/           # 路由配置
├── store/            # Pinia 状态管理
├── utils/            # 工具函数
└── types/            # TypeScript 类型定义
```

## 编辑器配置

```editorconfig
charset = utf-8
end_of_line = lf
insert_final_newline = true
indent_style = space
indent_size = 2
max_line_length = 100
```

## 重要注意事项

1. **遵循现有模式**：创建新代码前先查看类似模块
2. **未明确要求不得 commit**：不要主动创建 Git 提交
3. **类型安全**：禁止使用 `as any` 等类型错误抑制
4. **前端 UI/UX 变更**：样式、布局、动画等视觉变更委托给 `frontend-ui-ux-engineer` agent 处理
