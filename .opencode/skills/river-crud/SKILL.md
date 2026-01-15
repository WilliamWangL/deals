---
name: river-crud
description: Use when creating CRUD from SQL file - supports single table and master-sub tables, replaces CodeGen UI
---

# River CRUD Generator

根据 SQL 建表语句生成完整 CRUD 代码，支持单表和主子表。

## 输入

提供 SQL 文件路径或建表语句：

```sql
-- 单表示例
CREATE TABLE river_promotion (
    id int8 NOT NULL,
    title varchar(200) NOT NULL,
    status int2 NOT NULL DEFAULT 0,
    -- ... 标准字段
    PRIMARY KEY (id)
);
COMMENT ON TABLE river_promotion IS '促销活动';
COMMENT ON COLUMN river_promotion.title IS '标题';

-- 主子表示例（主表）
CREATE TABLE river_order (
    id int8 NOT NULL,
    order_no varchar(64) NOT NULL,
    -- ...
);
-- 子表（通过外键关联）
CREATE TABLE river_order_item (
    id int8 NOT NULL,
    order_id int8 NOT NULL,  -- 关联主表
    product_name varchar(200),
    -- ...
);
```

## 场景判断

```
┌─────────────────────────────────────┐
│ 是否有子表（外键关联）？             │
├──────────┬──────────────────────────┤
│    否    │ → 单表模式               │
│    是    │ → 主子表模式             │
│          │   ├─ 内嵌子表（Normal）   │
│          │   └─ 分页子表（ERP）     │
└──────────┴──────────────────────────┘
```

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| 单表 | 标准 CRUD | 大多数业务表 |
| 内嵌子表 | 子表内嵌在主表表单中 | 订单+订单项、合同+条款 |
| 分页子表 | 子表独立 tab 分页展示 | 商家+优惠券、用户+订单 |

## 执行流程

### 第一步：解析 SQL

从 SQL 提取：
- 表名 → 模块名、实体名
- 字段 → 类型映射、必填、注释
- 外键 → 主子表关系

**类型映射**：
| SQL 类型 | Java 类型 |
|----------|-----------|
| int8/bigint | Long |
| int4/int | Integer |
| int2/smallint | Integer |
| varchar/text | String |
| timestamp | LocalDateTime |
| date | LocalDate |
| numeric/decimal | BigDecimal |
| bool | Boolean |

### 第二步：读取模板

**必须先读取模板文件**：

```
river-server/river-module-infra/src/main/resources/codegen/
```

| 类型 | 单表模板 | 子表模板 |
|------|----------|----------|
| DO | `java/dal/do.vm` | `java/dal/do_sub.vm` |
| Mapper | `java/dal/mapper.vm` | `java/dal/mapper_sub.vm` |
| VO | `java/controller/vo/*.vm` | - |
| Service | `java/service/*.vm` | - |
| Controller | `java/controller/controller.vm` | - |
| Vue 列表 | `vue3/views/index.vue.vm` | `vue3/views/components/list_sub_*.vm` |
| Vue 表单 | `vue3/views/form.vue.vm` | `vue3/views/components/form_sub_*.vm` |
| Vue API | `vue3/api/api.ts.vm` | - |

### 第三步：读取参考模块

**必须读取现有模块代码**：

| 参考 | 路径 |
|------|------|
| 单表后端 | `river-module-coupon/river-module-coupon-biz/` |
| 单表前端 | `river-ui-admin/src/views/river/deal/` |
| 主子表参考 | 查看 `river-module-system` 中的相关实现 |

### 第四步：生成后端代码

**生成顺序**：

```
1. DO（主表继承 TenantBaseDO，子表继承 BaseDO）
2. Mapper
3. VO（SaveReqVO 包含子表 List）
4. Service（含子表批量操作）
5. Controller
6. ErrorCode
```

**主子表 Service 特殊逻辑**：
```java
// 创建时插入子表
private void create{SubEntity}List(Long {masterId}, List<{SubEntity}DO> list)

// 更新时对比差异
private void update{SubEntity}List(Long {masterId}, List<{SubEntity}DO> list) {
    // 使用 diffList 对比新增、修改、删除
}

// 删除时级联删除子表
private void deleteBy{MasterIdColumn}(Long {masterId})
```

### 第五步：生成前端代码

**单表**：
```
src/views/river/{module}/
├── index.vue
└── {Entity}Form.vue
```

**主子表（内嵌）**：
```
src/views/river/{module}/
├── index.vue
├── {Entity}Form.vue
└── components/
    └── {SubEntity}Form.vue
```

**主子表（ERP 分页）**：
```
src/views/river/{module}/
├── index.vue
├── {Entity}Form.vue
└── components/
    ├── {SubEntity}List.vue
    └── {SubEntity}Form.vue
```

### 第六步：验证

```bash
# 后端编译
cd river-server && mvn compile -pl river-module-{module} -am -q

# 前端检查
cd river-ui-admin && pnpm lint:eslint
```

## 核心约束

| 约束 | 正确 | 错误 |
|------|------|------|
| 主表 DO | `extends TenantBaseDO` | `extends BaseDO` |
| 子表 DO | `extends BaseDO` | `extends TenantBaseDO` |
| 对象转换 | `BeanUtils.toBean()` | 手动转换 |
| 子表更新 | `diffList()` 对比 | 全删全插 |
| 存在校验 | `validate{Entity}Exists()` | 直接操作 |
| 异常抛出 | `exception(ERROR_CODE)` | 原生异常 |

## 菜单权限 SQL（可选）

```sql
-- 主菜单
INSERT INTO system_menu (name, path, component, parent_id, ...) 
VALUES ('{label}管理', '{module}', 'river/{module}/index', {parentId}, ...);

-- 按钮权限
INSERT INTO system_menu (name, permission, type, parent_id, ...) VALUES 
('{label}查询', '{module}:{entity}:query', 3, {menuId}, ...),
('{label}创建', '{module}:{entity}:create', 3, {menuId}, ...),
('{label}修改', '{module}:{entity}:update', 3, {menuId}, ...),
('{label}删除', '{module}:{entity}:delete', 3, {menuId}, ...);
```

## 使用示例

```
使用 river-crud skill，根据以下 SQL 生成代码：

CREATE TABLE river_campaign (
    id int8 NOT NULL,
    name varchar(200) NOT NULL,
    start_time timestamp,
    end_time timestamp,
    status int2 NOT NULL DEFAULT 0,
    creator varchar(64) DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE river_campaign IS '营销活动';
COMMENT ON COLUMN river_campaign.name IS '活动名称';
COMMENT ON COLUMN river_campaign.start_time IS '开始时间';
COMMENT ON COLUMN river_campaign.end_time IS '结束时间';
COMMENT ON COLUMN river_campaign.status IS '状态';
```
