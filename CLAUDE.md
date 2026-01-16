# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

River 广告平台是一个广告联盟管理系统 monorepo，包含：

- **river-server**: Java 17 + Spring Boot 3.5 + PostgreSQL 17 后端 API
- **river-ui-admin**: Vue 3 + Element Plus + TypeScript 管理后台
- **river-ecommica**: Next.js 16 优惠聚合站点 (deals.ecommica.com)

## 常用命令

### 后端 (river-server)

```bash
# 编译整个项目
cd river-server && mvn clean compile

# 运行单个模块测试
cd river-server/river-module-{module}/river-module-{module}-biz
mvn test -Dtest=TestClassName

# 运行服务器（默认端口 48080）
cd river-server/river-server && mvn spring-boot:run

# 跳过测试打包
cd river-server && mvn clean package -DskipTests
```

### 管理后台 (river-ui-admin)

```bash
cd river-ui-admin

# 安装依赖（使用 pnpm）
pnpm install

# 本地开发
pnpm dev          # 连接本地后端 (env.local)
pnpm dev-server   # 连接开发服务器

# 类型检查
pnpm ts:check

# 构建
pnpm build:local  # 本地环境
pnpm build:dev    # 开发环境
pnpm build:test   # 测试环境
pnpm build:prod   # 生产环境

# 代码检查和格式化
pnpm lint:eslint
pnpm lint:format
pnpm lint:style
```

### Next.js 站点 (river-ecommica)

```bash
cd river-ecommica

# 本地开发
pnpm dev

# 构建
pnpm build

# 生产运行
pnpm start
```

## 项目架构

### 后端架构

三层架构，基于模块化设计：

```
river-server/
├── river-framework/           # 技术组件（基础框架）
│   ├── river-common/          # 公共工具类
│   ├── river-spring-boot-starter-mybatis/   # MyBatis Plus 扩展
│   ├── river-spring-boot-starter-redis/     # Redis/Redisson 封装
│   ├── river-spring-boot-starter-web/       # Web MVC 全局异常、日志
│   ├── river-spring-boot-starter-security/  # 安全认证
│   ├── river-spring-boot-starter-biz-tenant/     # 多租户
│   └── ...
├── river-server/              # 主启动模块
├── river-module-system/       # 系统模块（用户、角色、权限等）
├── river-module-infra/        # 基础设施模块（文件、任务、配置等）
├── river-module-affiliate/    # 联盟营销模块
├── river-module-tracking/     # 跟踪追踪模块
├── river-module-coupon/       # 优惠券模块
├── river-module-blog/         # 博客模块
├── river-module-campaign/     # 营销活动模块
└── river-module-stats/        # 统计模块
```

**模块结构**（以 river-module-affiliate 为例）：

```
river-module-affiliate/
├── river-module-affiliate-api/     # API 模块（接口定义、DTO、VO）
│   └── src/main/java/
│       └── com/river/module/affiliate/
│           ├── controller/         # Feign 客户端接口
│           ├── enums/              # 枚举
│           └── vo/                 # 值对象
└── river-module-affiliate-biz/     # 业务实现模块
    └── src/main/java/
        └── com/river/module/affiliate/
            ├── controller/         # REST Controller
            ├── convert/            # MapStruct 转换器
            ├── dal/                # 数据访问层
            │   ├── dataobject/     # DO (数据库实体)
            │   └── mysql/          # MyBatis Mapper
            ├── framework/          # 模块级配置
            └── service/            # 业务逻辑层
```

**重要约定**：
- 组件扫描路径：`${river.info.base-package}.server` 和 `${river.info.base-package}.module`
- 数据库表前缀：新模块使用 `river_`，框架使用 `system_`/`infra_`
- API 路径：`/admin-api/`（管理后台，需认证）、`/app-api/`（公开接口）
- 多租户：业务表必须包含 `tenant_id` 字段
- 软删除：使用 `deleted` 字段（0=未删除，1=已删除），禁止硬删除

### 前端架构 (river-ui-admin)

```
src/
├── api/              # API 调用模块（按业务模块组织）
│   └── river/        # River 业务模块 API
│       ├── affiliate/
│       ├── campaign/
│       ├── coupon/
│       ├── blog/
│       └── stats/
├── views/            # 页面组件
│   └── river/        # River 业务模块页面
├── components/       # 公共组件
├── router/           # 路由配置
├── store/            # Pinia 状态管理
├── utils/            # 工具函数
└── types/            # TypeScript 类型定义
```

### Next.js 站点 (river-ecommica)

```
src/
├── app/              # App Router
├── components/       # React 组件
├── lib/              # 工具库
├── i18n/             # 国际化
├── messages/         # 翻译文件
└── config/           # 配置
```

## 数据库

- **类型**: PostgreSQL 17
- **连接**: `localhost:5432/river`
- **默认表结构约定**:

```sql
CREATE TABLE river_example (
    id int8 NOT NULL,
    name varchar(200) NOT NULL,
    status int2 NOT NULL DEFAULT 0,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
```

## 开发工作流 (Superpowers)

本项目已安装 [Superpowers](https://github.com/obra/superpowers) 插件来规范化开发流程。

### 可用 Slash 命令

```bash
/superpowers:brainstorm    # 交互式设计细化 - 用于新功能开始前
/superpowers:write-plan    # 创建实施计划
/superpowers:execute-plan  # 批量执行计划任务
```

### Superpowers 工作流

1. **brainstorming** - 编写代码前激活，通过问题细化需求
2. **writing-plans** - 设计批准后，将工作拆分为 2-5 分钟的小任务
3. **test-driven-development** - RED-GREEN-REFACTOR：先写测试→看失败→写最小代码→通过
4. **systematic-debugging** - 4 阶段根因分析，禁止盲目尝试
5. **requesting-code-review** - 任务完成后审查代码

### 使用 Skill 工具

Claude Code 会自动检测并加载相关 skill。你也可以显式调用：

```
使用 Skill 工具加载 superpowers:test-driven-development
```

## 重要注意事项

1. **遵循现有模式** - 创建新代码前先查看类似模块
2. **未明确要求不得 commit** - 不要主动创建 Git 提交
3. **Superpowers 已安装** - 通过 Claude Code 插件系统安装，使用 Skill 工具加载

## 模块间依赖

- API 模块（`*-api`）可被其他模块引用
- BIZ 模块（`*-biz`）包含具体实现，通过 Spring 自动装配发现
- 前端 API 路径与后端 Controller 路径保持一致
