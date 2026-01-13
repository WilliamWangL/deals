# Phase 3: 优惠券模块实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现 river-module-coupon 模块，管理优惠券 (Coupon) 和 Deal 活动，支持多种折扣类型和热度评分。

**Architecture:**
- 新建 Maven 模块 `river-module-coupon`
- 核心模型：Coupon (优惠券), Deal (特价活动)
- 关联 affiliate 模块的 Merchant 和 Offer

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, MapStruct, PostgreSQL

---

## Task 1: 创建 Maven 模块结构

**Files:**
- Create: `river-server/river-module-coupon/pom.xml`
- Create: `river-server/river-module-coupon/river-module-coupon-api/pom.xml`
- Create: `river-server/river-module-coupon/river-module-coupon-biz/pom.xml`
- Modify: `river-server/pom.xml` (添加子模块)

**Step 1: 创建模块目录结构**
```bash
cd river-server
mkdir -p river-module-coupon/river-module-coupon-api/src/main/java/com/river/module/coupon
mkdir -p river-module-coupon/river-module-coupon-biz/src/main/java/com/river/module/coupon
mkdir -p river-module-coupon/river-module-coupon-biz/src/main/resources
```

**Step 2: Commit**
```bash
git add river-module-coupon/ pom.xml
git commit -m "feat(coupon): 创建 river-module-coupon 模块结构"
```

---

## Task 2: 创建优惠券实体 (Coupon)

**Files:**
- Create: `river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/dataobject/CouponDO.java`

**Step 1: 创建 CouponDO**
- 字段：`code`, `merchantId`, `offerId`
- 折扣：`discountType` (1=PERCENT, 2=FIXED), `discountValue`
- 限制：`minPurchase`, `startTime`, `endTime`
- 状态：`verified` (人工验证), `hotScore`

**Step 2: Commit**
```bash
git add river-module-coupon/
git commit -m "feat(coupon): 添加 CouponDO 实体"
```

---

## Task 3: 创建 Deal 实体 (Deal)

**Files:**
- Create: `river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/dataobject/DealDO.java`

**Step 1: 创建 DealDO**
- 字段：`title`, `description`, `merchantId`, `offerId`
- 价格：`originalPrice`, `dealPrice`, `discountPercent`
- 图片：`imageUrl`
- 状态：`featured` (精选), `hotScore`

**Step 2: Commit**
```bash
git add river-module-coupon/
git commit -m "feat(coupon): 添加 DealDO 实体"
```

---

## Task 4: 创建数据库 SQL 脚本

**Files:**
- Create: `river-server/sql/postgresql/coupon/river_coupon.sql`

**Step 1: 编写 SQL**
- 表：`river_coupon_coupon`, `river_coupon_deal`
- 索引：`merchant_id`, `end_time` (用于查询过期), `verified`, `featured`

**Step 2: Commit**
```bash
git add river-server/sql/
git commit -m "feat(coupon): 添加优惠券模块数据库 SQL"
```

---

## Task 5: 创建 Mapper 接口

**Files:**
- Create: `river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/mysql/CouponMapper.java`
- Create: `river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/mysql/DealMapper.java`

**Step 1: 创建 Mapper**
- 继承 `BaseMapperX`

**Step 2: Commit**
```bash
git add river-module-coupon/
git commit -m "feat(coupon): 添加 Mapper 接口"
```

---

## Verification

完成所有任务后，验证模块编译通过：

```bash
cd river-server
mvn compile -pl river-module-coupon -am
```
