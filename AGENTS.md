# AGENTS.md

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
- **MCP 查询**：可使用 `mcp__postgres__query` 工具直接查询 PostgreSQL

---

## river-server (Java 后端)

### 常用命令

```bash
cd river-server

# 编译项目
mvn clean compile

# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=ClassName

# 运行单个测试方法
mvn test -Dtest=ClassName#methodName

# 跳过测试打包
mvn clean package -DskipTests

# 运行服务器（端口 48080）
mvn spring-boot:run
```

### 测试类规范

- 测试类位于 `src/test/java/` 目录
- 命名规范：`{ServiceName}ImplTest.java`
- 使用 JUnit 5 + Mockito
- 测试基类：`BaseDbUnitTest`、`BaseMockitoUnitTest`、`BaseRedisUnitTest`

```java
// 示例结构
public class ExampleServiceImplTest extends BaseDbUnitTest {

    @InjectMocks
    private ExampleServiceImpl service;

    @Mock
    private ExampleMapper mapper;

    @Test
    void testMethod() {
        // given
        // when
        // then
    }
}
```

### 代码风格

**命名规范：**
- 类名：PascalCase（例：`UserServiceImpl`）
- 方法名/变量名：camelCase（例：`getUserById`）
- 常量：UPPER_SNAKE_CASE（例：`MAX_RETRY_COUNT`）
- 包名：全小写（例：`com.river.module.affiliate`）

**代码组织：**
- 导入顺序：静态导入 → JDK → Spring → Lombok/其他库 → 项目内部
- 禁止使用 `*` 通配符导入
- 每个类必须添加类注释（DocComment）
- 字段注释使用 Javadoc `@param`、`@return`、`@see`

**类型安全（硬性禁止）：**
- 禁止使用 `as any`、`@ts-ignore`、`@ts-expect-error`
- 禁止使用 `catch(e) {}` 空捕获块
- 优先使用具体类型，避免 raw types

**异常处理：**
- 使用 `ServiceException` 抛出业务异常
- 禁止吞掉异常而不处理
- 集成测试使用 `@SpringBootTest` + `@Transactional`

### 模块结构

```
river-module-xxx/
└── src/main/java/com/river/module/xxx/
    ├── api/           # 跨模块 API 接口
    ├── controller/    # REST Controller（admin/、app/）
    ├── convert/       # MapStruct 转换器
    ├── dal/           # 数据访问层
    │   ├── dataobject/   # DO (数据库实体)
    │   └── mysql/        # MyBatis Mapper
    ├── enums/         # 枚举定义
    ├── framework/     # 模块级配置
    └── service/       # 业务逻辑层
```

**API 路径约定：**
- 管理后台：`/admin-api/`（需认证）
- 公开接口：`/app-api/`（需 `@PermitAll`）

---

## river-ui-admin (Vue 3 管理后台)

### 常用命令

```bash
cd river-ui-admin

# 安装依赖
pnpm install

# 本地开发（连接本地后端）
pnpm dev

# 开发服务器
pnpm dev-server

# 类型检查
pnpm ts:check

# 构建
pnpm build:local   # 本地环境
pnpm build:dev     # 开发环境
pnpm build:prod    # 生产环境

# 代码检查
pnpm lint:eslint   # ESLint 检查
pnpm lint:format   # Prettier 格式化
pnpm lint:style    # Stylelint 检查
```

### 代码风格

**TypeScript 配置：**
- `strict: true`
- `noUnusedLocals: true`
- `noUnusedParameters: true`
- 路径别名：`@/*` → `src/*`

**组件结构：**
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

**命名规范：**
- 组件文件：PascalCase（例：`UserDialog.vue`）
- 组合式函数：`use` 前缀（例：`useUserStore.ts`）
- 工具函数：camelCase（例：`formatDate.ts`）
- 常量：UPPER_SNAKE_CASE 或 camelCase

**导入顺序：**
1. Vue/路由相关
2. Element Plus 组件
3. 第三方库
4. `@/` 项目路径
5. `./` 相对路径

**样式规范：**
- 使用 SCSS
- 使用 `<style lang="scss" scoped>`
- 使用 UnoCSS 原子类（可选）
- BEM 命名（可选）

---

## river-ecommica (Next.js 站点)

### 常用命令

```bash
cd river-ecommica

# 本地开发
pnpm dev

# 构建
pnpm build

# 生产运行
pnpm start

# 代码检查
pnpm lint
```

### 代码风格

**TypeScript：**
- 使用 React 19
- 严格模式：`strict: true`
- 路径别名：`@/*` → `src/*`
- JSX：`react-jsx`

**组件约定：**
- 使用 App Router（`src/app/`）
- Server Components 默认
- 使用 `next-intl` 进行国际化
- 使用 Tailwind CSS 4 进行样式

**命名规范：**
- 组件：PascalCase（例：`CouponCard.tsx`）
- 工具函数：camelCase
- hooks：`use` 前缀

**Tailwind CSS：**
- 使用 `@tailwindcss/postcss`
- 通过 `tailwind-merge` 合并类名
- 使用 `clsx` 管理条件类名

**导入顺序：**
1. Node.js 内置
2. React 相关
3. 第三方库
4. 项目内部组件/工具
5. 相对导入

---

## 开发工作流

### 推荐流程

1. **brainstorming** - 编写代码前激活，细化需求
2. **writing-plans** - 设计批准后拆分为小任务
3. **test-driven-development** - RED-GREEN-REFACTOR
4. **systematic-debugging** - 4 阶段根因分析
5. **requesting-code-review** - 完成后审查

### 禁用操作

| 操作 | 原因 |
|------|------|
| 提交代码（未明确要求） | 避免干扰用户 |
| 硬删除数据 | 保留审计追踪 |
| 类型错误抑制 | 破坏类型安全 |
| 删除失败测试 | 掩盖问题 |

---

## 前端 UI/UX 变更

**重要**：如果任务涉及样式、布局、动画、颜色等**视觉变更**，必须委托给 `frontend-ui-ux-engineer` agent 处理，**不要直接修改样式代码**。

**可自行处理**：API 调用、数据处理、业务逻辑、状态管理。
