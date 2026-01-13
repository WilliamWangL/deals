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

## Superpowers 工作流

本项目使用 [Superpowers](https://github.com/obra/superpowers) 规范化开发流程。

### 开发阶段与 Skills

| 阶段 | 触发条件 | 使用 Skill | 说明 |
|------|----------|------------|------|
| **1. 头脑风暴** | "做一个..."、"实现..."、"添加功能..." | `superpowers:brainstorming` | 澄清需求，探索方案，产出设计文档 |
| **2. 创建分支** | 设计确认后 | `superpowers:using-git-worktrees` | 创建隔离工作分支，验证测试基线 |
| **3. 编写计划** | 有设计文档 | `superpowers:writing-plans` | 拆解任务（每个 2-5 分钟），明确文件路径和验证步骤 |
| **4. 执行计划** | 有实施计划 | `superpowers:executing-plans` | 批量执行任务，人工检查点 |
| **5. 代码实现** | 具体编码时 | `superpowers:test-driven-development` | RED-GREEN-REFACTOR：先写测试，看失败，再实现 |
| **6. 调试修复** | 遇到 bug | `superpowers:systematic-debugging` | 4 阶段根因分析，禁止盲目尝试 |
| **7. 代码审查** | 任务完成后 | `superpowers:requesting-code-review` | 自查清单，报告问题严重级别 |
| **8. 完成分支** | 所有任务完成 | `superpowers:finishing-a-development-branch` | 验证测试，选择合并/PR/保留/丢弃 |

### 快速参考

```
# 查看所有可用 skills
使用 find_skills 工具

# 加载特定 skill
使用 use_skill 工具，skill_name: "superpowers:brainstorming"
```

### 阶段提醒

AI 助手应在以下时机主动提醒：
- **新功能请求** → 提醒进入头脑风暴阶段
- **设计确认后** → 提醒创建工作分支
- **开始编码前** → 提醒先写测试（TDD）
- **遇到错误 2+ 次** → 提醒使用系统化调试
- **任务完成时** → 提醒请求代码审查

### Subagent 代理

本项目可使用以下专业代理（oh-my-opencode 提供）：

| 代理 | 用途 | 触发时机 |
|------|------|----------|
| `explore` | 代码探索 - 快速理解项目结构 | 不熟悉模块、跨层查找模式 |
| `librarian` | 文档查询 - 外部库文档和最佳实践 | 使用不熟悉的库、查找 API 用法 |
| `oracle` | 架构咨询 - 复杂设计决策和架构评审 | 多系统权衡、修复失败 2+ 次、自我审查 |
| `frontend-ui-ux-engineer` | 前端 UI/UX 设计与实现 | 视觉/样式相关改动（非纯逻辑） |
| `document-writer` | 技术文档撰写 | README、API 文档、架构文档 |
| `multimodal-looker` | 媒体分析 - PDF/图片/图表解读 | 需要分析设计稿、流程图等 |

#### 使用原则

1. **并行优先**：explore/librarian 作为后台任务并行启动，不阻塞主流程
2. **Oracle 慎用**：昂贵资源，仅用于复杂架构决策或多次失败后
3. **前端委派**：视觉相关改动（颜色、布局、动画）必须委派给 `frontend-ui-ux-engineer`
4. **文档委派**：文档任务委派给 `document-writer`

## 子项目规范

各子项目有独立的 AGENTS.md，包含技术栈特定规范：
- [river-server/AGENTS.md](river-server/AGENTS.md) - 后端规范
- [river-ui-admin/AGENTS.md](river-ui-admin/AGENTS.md) - 管理后台规范
- [river-ecommica/AGENTS.md](river-ecommica/AGENTS.md) - 优惠站点规范
