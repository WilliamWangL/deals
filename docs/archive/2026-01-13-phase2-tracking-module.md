# Phase 2: 追踪模块实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现 river-module-tracking 模块，支持点击追踪、转化归因、Sub ID 多维分析。

**Architecture:**
- 新建 Maven 模块 `river-module-tracking`
- 核心模型：Click (点击), Conversion (转化), TrackingLink (追踪链接)
- 关键特性：使用 ULID 作为 click_id，支持 Postback 回调归因，未归因转化记录

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, MapStruct, PostgreSQL

---

## Task 1: 创建 Maven 模块结构

**Files:**
- Create: `river-server/river-module-tracking/pom.xml`
- Create: `river-server/river-module-tracking/river-module-tracking-api/pom.xml`
- Create: `river-server/river-module-tracking/river-module-tracking-biz/pom.xml`
- Modify: `river-server/pom.xml` (添加子模块)

**Step 1: 创建模块目录结构**
```bash
cd river-server
mkdir -p river-module-tracking/river-module-tracking-api/src/main/java/com/river/module/tracking
mkdir -p river-module-tracking/river-module-tracking-biz/src/main/java/com/river/module/tracking
mkdir -p river-module-tracking/river-module-tracking-biz/src/main/resources
```

**Step 2: 创建 POM 文件**
(参照 affiliate 模块结构，依赖 river-common, river-spring-boot-starter-biz-tenant 等)

**Step 3: Commit**
```bash
git add river-module-tracking/ pom.xml
git commit -m "feat(tracking): 创建 river-module-tracking 模块结构"
```

---

## Task 2: 创建点击实体 (Click)

**Files:**
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/dataobject/ClickDO.java`

**Step 1: 创建 ClickDO**
- 主键 `clickId` (String, ULID 格式)
- 字段：`offerId`, `campaignId`, `landingPageId`
- 追踪参数：`sub1` - `sub5`
- 环境信息：`ip`, `userAgent`, `referer`, `deviceType`, `country`
- 时间：`clickTime`

**Step 2: Commit**
```bash
git add river-module-tracking/
git commit -m "feat(tracking): 添加 ClickDO 实体"
```

---

## Task 3: 创建转化实体 (Conversion)

**Files:**
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/dataobject/ConversionDO.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/dataobject/UnattributedConversionDO.java`
- Create: `river-module-tracking-api/src/main/java/com/river/module/tracking/enums/ConversionStatusEnum.java`
- Create: `river-module-tracking-api/src/main/java/com/river/module/tracking/enums/ConversionTypeEnum.java`

**Step 1: 创建枚举**
- `ConversionStatusEnum`: 0=PENDING, 1=APPROVED, 2=REJECTED, 3=REVOKED
- `ConversionTypeEnum`: 1=LEAD, 2=SALE, 3=INSTALL, 4=SIGNUP

**Step 2: 创建 ConversionDO**
- 字段：`clickId`, `networkCode`, `externalConversionId`
- 财务：`commission`, `currency`
- 状态：`status`

**Step 3: 创建 UnattributedConversionDO**
- 用于记录无法匹配 click_id 的回调
- 字段：`rawRequest`, `attributionFailReason`

**Step 4: Commit**
```bash
git add river-module-tracking/
git commit -m "feat(tracking): 添加 ConversionDO 和 UnattributedConversionDO"
```

---

## Task 4: 创建追踪链接实体 (TrackingLink)

**Files:**
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/dataobject/TrackingLinkDO.java`

**Step 1: 创建 TrackingLinkDO**
- 字段：`slug` (短链接标识), `offerId`
- 预设参数：`presetSub1` - `presetSub5`
- `utmParams`

**Step 2: Commit**
```bash
git add river-module-tracking/
git commit -m "feat(tracking): 添加 TrackingLinkDO"
```

---

## Task 5: 创建数据库 SQL 脚本

**Files:**
- Create: `river-server/sql/postgresql/tracking/river_tracking.sql`

**Step 1: 编写 SQL**
- 表：`river_tracking_click`, `river_tracking_conversion`, `river_tracking_link`, `river_tracking_unattributed_conversion`
- 索引：`click_id` 主键，`conversion` 的 `uk_network_external` 唯一索引

**Step 2: Commit**
```bash
git add river-server/sql/
git commit -m "feat(tracking): 添加追踪模块数据库 SQL"
```

---

## Task 6: 创建 Mapper 接口

**Files:**
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/mysql/ClickMapper.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/mysql/ConversionMapper.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/mysql/TrackingLinkMapper.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/mysql/UnattributedConversionMapper.java`

**Step 1: 创建 Mapper**
- 继承 `BaseMapperX`

**Step 2: Commit**
```bash
git add river-module-tracking/
git commit -m "feat(tracking): 添加 Mapper 接口"
```

---

## Verification

完成所有任务后，验证模块编译通过：

```bash
cd river-server
mvn compile -pl river-module-tracking -am
```
