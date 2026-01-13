# AGENTS.md - River 广告平台

本文档为 AI 编程助手在此仓库工作时提供指导规范。

## 项目概述

River 广告平台是一个广告联盟管理系统，包含：
- **river-server**: Java 17 + Spring Boot 3.5 后端 API
- **river-ui-admin**: Vue 3 + Element Plus 管理后台
- **river-ecommica**: Next.js 15 优惠聚合站点 (deals.ecommica.com)

## 通用规范

### 数据库
- **类型**：PostgreSQL 17 (Supabase)
- **表前缀**：新模块使用 `river_`，框架使用 `system_`/`infra_`
- **多租户**：所有业务表必须包含 `tenant_id` 字段
- **软删除**：使用 `deleted` 字段，禁止硬删除

### SQL 规范
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

### API 路径约定
- 管理后台：`/admin-api/`（需要认证）
- 公开接口：`/app-api/`（站点调用）

## 重要注意事项

1. **禁止使用 `as any` 或 `@ts-ignore`**（TypeScript）
2. **始终使用 Jakarta 校验** 验证输入（Java）
3. **使用 MapStruct** 进行对象转换，禁止手动映射（Java）
4. **遵循现有模式** - 创建新代码前先查看类似模块
5. **未明确要求不得 commit**

## 子项目规范

各子项目有独立的 AGENTS.md，包含技术栈特定规范：
- [river-server/AGENTS.md](river-server/AGENTS.md) - 后端规范
- [river-ui-admin/AGENTS.md](river-ui-admin/AGENTS.md) - 管理后台规范
- [river-ecommica/AGENTS.md](river-ecommica/AGENTS.md) - 优惠站点规范
