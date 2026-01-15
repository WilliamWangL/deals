# AGENTS.md - River 广告平台

本文档为 AI 编程助手在此仓库工作时提供指导规范。

## 项目概述

River 广告平台是一个广告联盟管理系统，包含：
- **river-server**: Java 17 + Spring Boot 3.5 后端 API
- **river-ui-admin**: Vue 3 + Element Plus 管理后台
- **river-ecommica**: Next.js 15 优惠聚合站点 (deals.ecommica.com)

## 通用规范

### 数据库
- **类型**：PostgreSQL 17
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

1. **遵循现有模式** - 创建新代码前先查看类似模块
2. **未明确要求不得 commit**

## Superpowers 工作流

本项目使用 [Superpowers](https://github.com/obra/superpowers) 规范化开发流程。

<EXTREMELY-IMPORTANT>
如果你认为有哪怕 1% 的可能性某个 skill 适用于当前任务，你**必须**调用该 skill。

这不是可选的，这不是建议，这是强制性的工作流。

**在执行任何响应或操作之前，先检查是否有适用的 skill。**
</EXTREMELY-IMPORTANT>

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

# 主 Agent 自己加载 skill（用于理解流程规范）
使用 use_skill 工具，skill_name: "superpowers:brainstorming"

# 委派给 subagent 并注入 skill（推荐方式）
使用 task 工具：
  category: "general"  # 后端实现使用 Category
  skills: ["superpowers:test-driven-development"]
  prompt: "实现功能..."
  run_in_background: false  # 必填！false=同步委派，true=后台并行

# 或使用 subagent_type 指定特定代理
使用 task 工具：
  subagent_type: "oracle"  # 指定 Agent
  skills: ["superpowers:requesting-code-review"]
  prompt: "审查代码..."
  run_in_background: false
```

### 阶段提醒

AI 助手应在以下时机主动提醒：
- **新功能请求** → 提醒进入头脑风暴阶段
- **设计确认后** → 提醒创建工作分支
- **开始编码前** → 提醒先写测试（TDD）
- **遇到错误 2+ 次** → 提醒使用系统化调试
- **任务完成时** → 提醒请求代码审查

### ULW（一路往下）模式规范

当用户要求"一路往下"或"ulw"连续执行多阶段任务时，**必须遵循**：

#### 每阶段必做事项

```
每个 Phase 开始前：
1. 加载相关 superpowers skill（流程规范）
2. 创建计划文档 → docs/plans/YYYY-MM-DD-phase{N}-{module}.md
3. 更新 TodoWrite 标记当前阶段
4. 按 subagent-driven-development 流程执行（调研→实现→审查→修复→提交）
5. 验证完成（编译通过、提交成功）
6. 更新 TodoWrite 标记完成
```

#### ULW 模式下的代理使用规范

每个 Phase 执行时，必须遵循统一的代理分工：

| 阶段 | 使用代理 | 说明 |
|------|---------|------|
| 调研 | explore, librarian, oracle, multimodal | 只读，收集信息 |
| 实现 | category: "general"（后端）/ frontend-ui-ux-engineer（前端 UI） | 遵循 TDD |
| 审查 | oracle | 规格审查 + 质量审查 |
| 修复 | category: "general" / frontend-ui-ux-engineer | 根据 oracle 建议修复 |
| 文档 | document-writer | 撰写技术文档 |

**禁止**：
- ❌ 让 oracle 直接修改代码（oracle 是只读代理）
- ❌ 跳过审查环节
- ❌ 使用 superpowers 的 code-reviewer 代理（已统一用 oracle）

#### 计划文档格式

```markdown
# [Module] Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans

**Goal:** [一句话描述]
**Architecture:** [2-3 句架构说明]
**Tech Stack:** [技术栈]

## Task 1: [任务名]
**Files:** [文件列表]
**Steps:** [步骤]
**Verification:** [验证命令]
**Commit:** [提交命令]

## Task N: ...

## Verification Checklist
- [ ] 编译通过
- [ ] 所有文件已创建
- [ ] 已提交 Git
```

#### 禁止行为

- ❌ 跳过计划文档直接执行
- ❌ 多个阶段合并成一个计划
- ❌ 执行后才补充计划文档

#### 正确流程示例

```
用户: ulw 直到完成

Phase 1:
  → 创建 docs/plans/2026-01-13-phase1-xxx.md
  → 执行任务
  → 提交

Phase 2:
  → 创建 docs/plans/2026-01-13-phase2-xxx.md
  → 执行任务
  → 提交

...
```

### Subagent 代理（统一使用 oh-my-opencode）

本项目统一使用 oh-my-opencode 提供的子代理，不使用 superpowers 定义的代理角色。

#### 两种委派方式

oh-my-opencode 的 `sisyphus_task` 工具支持两种互斥的委派方式：

| 参数 | 说明 | 适用场景 |
|------|------|----------|
| `category` | 预设分类，自动选择模型 | 通用任务，按任务类型分类 |
| `subagent_type` | 直接指定代理 | 需要特定代理能力时 |

#### Category 分类（使用 Sisyphus-Junior 执行）

| Category | 模型 | 用途 |
|----------|------|------|
| `general` | claude-opus-4-5-thinking | **通用任务**（后端实现推荐） |
| `ultrabrain` | gpt-5.2-codex | 严格架构设计、复杂业务逻辑 |
| `visual-engineering` | gemini-3-pro-high | 前端、UI/UX、动画 |
| `artistry` | gemini-3-pro-high (高温度) | 高度创意任务 |
| `quick` | gemini-3-flash | 小任务、低成本（需详细 prompt） |
| `most-capable` | claude-opus-4-5-thinking | 需要最强能力的复杂任务 |
| `writing` | gemini-3-flash | 文档、散文、技术写作 |

#### Agent 代理（直接使用指定代理）

| 代理 | 权限 | 用途 | 触发时机 |
|------|------|------|----------|
| `explore` | 只读 | 代码探索 - 快速理解项目结构 | 不熟悉模块、跨层查找模式 |
| `librarian` | 只读 | 文档查询 - 外部库文档和最佳实践 | 使用不熟悉的库、查找 API 用法 |
| `oracle` | 只读 | 架构咨询 + 规格审查 + 质量审查 | 架构设计、代码审查、疑难调试 |
| `multimodal-looker` | 只读 | 媒体分析 - PDF/图片/图表解读 | 需要分析设计稿、流程图等 |
| `Metis (Plan Consultant)` | 只读 | 计划咨询 - 帮助制定实施计划 | 复杂任务规划、拆解大任务 |
| `Momus (Plan Reviewer)` | 只读 | 计划审查 - 审核计划完整性 | 验证计划是否可行、完整 |
| `frontend-ui-ux-engineer` | **可写** | **前端 UI/UX 实现** | 视觉/样式相关改动 |
| `document-writer` | **可写** | 技术文档撰写 | README、API 文档、架构文档 |

> **注意**：后端实现任务推荐使用 `category: "general"` 或 `category: "ultrabrain"`（复杂业务逻辑时）。

#### 角色映射（superpowers → oh-my-opencode）

| superpowers 角色 | 映射到 oh-my-opencode 代理 |
|-----------------|---------------------------|
| implementer (后端) | `category: "general"` |
| implementer (前端 UI) | `frontend-ui-ux-engineer` |
| spec-reviewer | `oracle` |
| code-quality-reviewer | `oracle` |
| code-reviewer | `oracle` |

#### 使用原则

1. **并行优先**：explore/librarian 作为后台任务并行启动，不阻塞主流程
2. **实现分工**：后端用 `category: "general"`，前端 UI 用 `frontend-ui-ux-engineer`
3. **审查统一**：规格审查和质量审查都用 `oracle`
4. **oracle 只读**：oracle 只给建议，修复由 general/frontend 执行
5. **文档委派**：文档任务委派给 `document-writer`

#### 审查-修复循环

oracle 是只读代理，发现问题后由实现代理修复：

```
oracle 审查 → 发现问题 → general/frontend 修复 → oracle 再审查 → 通过
```

### 代理与技能协同

Sisyphus（主代理）通过 `task` 工具的 `skills` 参数将技能**预注入**到 subagent 的 system prompt，subagent 不主动加载 skill。

#### Skill 传递机制

```typescript
// Sisyphus 委派任务时，技能通过 skills 参数注入
task({
  category: "general",  // 使用 Category（后端实现）
  skills: ["superpowers:test-driven-development"],  // ← 技能在此注入到 subagent
  prompt: "为 river_campaign 表实现 CRUD",
  run_in_background: false  // 必填
})

// 或者使用 subagent_type 指定特定代理
task({
  subagent_type: "oracle",  // 使用 Agent（架构咨询）
  skills: ["superpowers:requesting-code-review"],
  prompt: "审查这段代码...",
  run_in_background: false
})

// Subagent 收到的 system prompt 已包含：
// 1. superpowers:test-driven-development 的完整内容
// 2. 如有嵌入 MCP，可通过 skill_mcp 调用
```

#### 协同架构

```
┌─────────────────────────────────────────────────────────────────┐
│                      Sisyphus（主协调者）                        │
│                                                                 │
│  职责：理解需求 → 加载 Skill → 派发代理 → 验证结果               │
└─────────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
   ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
   │ Superpowers │     │ oh-my-opencode │   │   用户      │
   │   Skills    │     │   Subagents    │   │  AGENTS.md  │
   │  (流程规范)  │     │   (执行者)     │   │  (项目规范)  │
   └─────────────┘     └─────────────┘     └─────────────┘
```

#### 执行计划详细流程（subagent-driven-development）

```
对每个任务：
│
├─ 【调研】（只读代理）
│   ├─ explore      → 探索相关代码
│   ├─ librarian    → 查询外部文档/库用法
│   ├─ oracle       → 架构咨询（复杂时）
│   └─ multimodal   → 分析设计稿（有图时）
│
├─ 【实现】（可写代理 + TDD）
│   ├─ 后端/通用代码 → category: "general"
│   │   └─ 遵循 superpowers:test-driven-development
│   │   └─ 先写测试 → 看失败 → 实现 → 通过
│   │
│   └─ 前端 UI/样式 → frontend-ui-ux-engineer
│       └─ 遵循 frontend-design skill
│
├─ 【规格审查】
│   └─ oracle 审查是否符合需求规格
│       ├─ 通过 → 继续
│       └─ 不通过 → general/frontend 修复 → 再审查
│
├─ 【质量审查】
│   └─ oracle 审查代码质量
│       ├─ 通过 → 继续
│       └─ 不通过 → general/frontend 修复 → 再审查
│
├─ 【文档】（如需要）
│   └─ document-writer → 撰写/更新文档
│
└─ 【提交】
    └─ 验证通过 → git commit → 标记任务完成
```

#### 标准协同组合

| 阶段 | Superpowers Skill | oh-my-opencode 代理 |
|------|-------------------|---------------------|
| 头脑风暴 | `superpowers:brainstorming` | explore, librarian, oracle |
| 编写计划 | `superpowers:writing-plans` | oracle (咨询) |
| 后端实现 | `superpowers:test-driven-development` | `category: "general"` |
| 前端 UI 实现 | `frontend-design` | `frontend-ui-ux-engineer` |
| 规格审查 | `superpowers:requesting-code-review` | `oracle` |
| 质量审查 | `superpowers:requesting-code-review` | `oracle` |
| 调试修复 | `superpowers:systematic-debugging` | `oracle` (分析) + `general` (修复) |
| 文档撰写 | `doc-coauthoring` | `document-writer` |

#### 项目特定协同规则

| 子项目 | 实现代理 | 配合 Skill |
|--------|----------|-----------|
| river-server (Java) | `category: "general"` | `superpowers:test-driven-development` |
| river-ui-admin (Vue 3) | `category: "general"`（逻辑）/ `frontend-ui-ux-engineer`（UI） | `frontend-design` |
| river-ecommica (Next.js) | `category: "general"`（逻辑）/ `frontend-ui-ux-engineer`（UI） | `frontend-design` |

#### 委派时的 Skill 注入示例

| 任务类型 | category / subagent_type | skills 参数 |
|----------|--------------------------|-------------|
| 后端实现 | `category: "general"` | `["superpowers:test-driven-development"]` |
| 前端 UI | `subagent_type: "frontend-ui-ux-engineer"` | `["frontend-design"]` |
| 代码审查 | `subagent_type: "oracle"` | `["superpowers:requesting-code-review"]` |
| 调试分析 | `subagent_type: "oracle"` | `["superpowers:systematic-debugging"]` |
| CRUD 生成 | `category: "general"` | `["river-crud"]` |

## 子项目规范

各子项目有独立的 AGENTS.md，包含技术栈特定规范：
- [river-server/AGENTS.md](river-server/AGENTS.md) - 后端规范
- [river-ui-admin/AGENTS.md](river-ui-admin/AGENTS.md) - 管理后台规范
- [river-ecommica/AGENTS.md](river-ecommica/AGENTS.md) - 优惠站点规范
