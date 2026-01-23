# River UI Admin - AI Agent 开发规范

> 本文档继承自 [项目通用规范](../AGENTS.md)。
> 
> 如未特别说明，遵循项目通用规范的所有约定。

## 特有规范

本文档记录管理前端子项目特有的开发规范。

## 技术栈分类

| 分类 | 模式 | 说明 |
|------|------|------|
| 框架 | Vue3 + TypeScript | 前端框架 |
| UI 组件 | Element Plus 类型 | UI 组件库 |
| 状态管理 | Pinia 模式 | 状态管理 |
| 样式 | SCSS + scoped | 样式方案 |

## API 调用模式

### API 模块组织

API 按业务模块组织在 `src/api/` 目录下：

```
src/api/
├── river/        # River 业务模块 API
│   ├── affiliate/
│   ├── campaign/
│   ├── coupon/
│   ├── blog/
│   ├── stats/
│   └── tracking/
├── system/       # 系统模块 API
└── infra/        # 基础设施 API
```

### 调用方式

- 使用 TypeScript 类型定义请求和响应
- 遵循项目已有的 API 封装模式
- 路径别名：`@/*` → `src/*`

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

## 禁止重复造轮子清单

> **禁止重复造轮子**：使用模式描述，而非列出具体组件名。

### 基础组件（src/components/）

| 功能模式 | 组件模式 | 说明 |
|----------|----------|------|
| 按钮 | XButton 类型 | 基础按钮组件 |
| 表格 | Table 类型 | 增强表格（分页、选择、搜索） |
| 上传 | UploadFile 类型 | 文件/图片上传 |
| 选择器 | SelectForm 类型 | 用户/部门选择器 |
| 图标 | Icon 类型 | 图标选择与展示 |
| 编辑器 | Editor 类型 | 富文本编辑器 |
| 图表 | Echart 类型 | ECharts 封装 |
| JSON | JsonEditor 类型 | JSON 编辑器 |
| 表单设计 | FormCreate 类型 | 动态表单设计器 |
| 流程设计 | bpmnProcessDesigner 类型 | BPMN 流程设计器 |
| 装饰组件 | Card/ContentWrap 类型 | 布局装饰组件 |

### Hooks（src/hooks/web/）

| 功能模式 | Hook 模式 | 说明 |
|----------|-----------|------|
| 表格操作 | useTable 模式 | 表格 CRUD 操作 |
| 表单操作 | useForm 模式 | 表单处理 |
| CRUD 模式 | useCrudSchemas 模式 | CRUD 模式生成 |
| 标签页 | useTagsView 模式 | 标签页管理 |
| 缓存 | useCache 模式 | 缓存操作 |
| 水印 | useWatermark 模式 | 水印功能 |
| 引导 | useGuide 模式 | 新手引导 |
| 验证 | useValidator 模式 | 表单验证 |

### 工具函数（src/utils/）

| 功能模式 | 工具模式 | 说明 |
|----------|----------|------|
| 字典 | dict.ts 模式 | 字典数据处理 |
| 树形 | tree.ts 模式 | 树结构操作 |
| 加密 | encrypt.ts 模式 | 加密解密 |
| 路由 | routerHelper.ts 模式 | 路由处理 |
| 权限 | permission.ts 模式 | 权限检查 |
| 下载 | download.ts 模式 | 文件下载 |
| 格式化 | formatTime.ts 模式 | 时间格式化 |
| 校验 | formRules.ts 模式 | 表单校验规则 |
| 日期 | dateUtil.ts 模式 | 日期操作 |

## 重要注意事项

1. **遵循现有模式**：创建新代码前先查看类似模块
2. **未明确要求不得 commit**：不要主动创建 Git 提交
3. **类型安全**：禁止使用 `as any` 等类型错误抑制
