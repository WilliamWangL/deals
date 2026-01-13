# 管理后台规范 - Vue 3 + Element Plus

River 广告平台管理后台前端。

## 构建命令

```bash
# 安装依赖
pnpm install

# 开发服务器
pnpm dev

# 生产环境构建
pnpm build:prod

# 代码检查与格式化
pnpm lint:eslint
pnpm lint:format
pnpm lint:style

# 类型检查
pnpm ts:check
```

## 代码风格 (Prettier)

- 行宽：100
- 缩进：2 空格
- 无分号
- 单引号
- 无尾随逗号

## 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `DealList.vue` |
| Composables | camelCase + use 前缀 | `useDeal.ts` |
| API 文件 | camelCase | `deal.ts` |
| 类型文件 | camelCase | `deal.d.ts` |

## 组件结构

```vue
<template>
  <!-- 模板 -->
</template>

<script setup lang="ts">
// imports 导入
// props/emits 属性/事件
// composables 组合式函数
// reactive state 响应式状态
// computed 计算属性
// methods 方法
// lifecycle 生命周期
</script>

<style scoped lang="scss">
/* 样式 */
</style>
```

## 错误处理

- 使用 try-catch 配合 `ElMessage` 反馈用户
- 在拦截器中处理 API 错误

## 重要约束

- 禁止使用 `as any` 或 `@ts-ignore`
- 严格遵循 Element Plus 组件用法
- API 调用统一放在 `api/` 目录
