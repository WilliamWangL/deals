# Phase 1: 联盟管理模块实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现 river-module-affiliate 模块，支持多联盟网络管理、商家管理、Offer 管理，先对接 Admitad API 和手动上传。

**Architecture:** 
- 新建 Maven 模块 river-module-affiliate（api + biz 子模块）
- 遵循 ruoyi-vue-pro 代码规范：DO + Mapper + Service + Controller + VO
- 使用 MapStruct 转换，Jakarta 校验，MyBatis-Plus 持久化

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, MapStruct, PostgreSQL

---

## Task 1: 创建 Maven 模块结构

**Files:**
- Create: `river-server/river-module-affiliate/pom.xml`
- Create: `river-server/river-module-affiliate/river-module-affiliate-api/pom.xml`
- Create: `river-server/river-module-affiliate/river-module-affiliate-biz/pom.xml`
- Modify: `river-server/pom.xml` (添加子模块)

**Step 1: 创建模块目录结构**

```bash
cd river-server
mkdir -p river-module-affiliate/river-module-affiliate-api/src/main/java/com/river/module/affiliate
mkdir -p river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate
mkdir -p river-module-affiliate/river-module-affiliate-biz/src/main/resources
```

**Step 2: 创建父模块 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.river</groupId>
        <artifactId>river-server</artifactId>
        <version>${revision}</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>river-module-affiliate</artifactId>
    <packaging>pom</packaging>
    <name>${project.artifactId}</name>
    <description>affiliate 模块 - 联盟管理</description>

    <modules>
        <module>river-module-affiliate-api</module>
        <module>river-module-affiliate-biz</module>
    </modules>
</project>
```

**Step 3: 创建 api 子模块 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.river</groupId>
        <artifactId>river-module-affiliate</artifactId>
        <version>${revision}</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>river-module-affiliate-api</artifactId>
    <packaging>jar</packaging>
    <name>${project.artifactId}</name>
    <description>affiliate 模块 API，暴露给其他模块的 API</description>

    <dependencies>
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-common</artifactId>
        </dependency>
    </dependencies>
</project>
```

**Step 4: 创建 biz 子模块 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.river</groupId>
        <artifactId>river-module-affiliate</artifactId>
        <version>${revision}</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>river-module-affiliate-biz</artifactId>
    <packaging>jar</packaging>
    <name>${project.artifactId}</name>
    <description>affiliate 模块业务实现</description>

    <dependencies>
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-module-affiliate-api</artifactId>
            <version>${revision}</version>
        </dependency>
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-module-system-api</artifactId>
            <version>${revision}</version>
        </dependency>
        <!-- Spring Boot -->
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-spring-boot-starter-biz-tenant</artifactId>
        </dependency>
        <!-- Web -->
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Security -->
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-spring-boot-starter-security</artifactId>
        </dependency>
        <!-- DB -->
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-spring-boot-starter-mybatis</artifactId>
        </dependency>
        <!-- Test -->
        <dependency>
            <groupId>com.river</groupId>
            <artifactId>river-spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

**Step 5: 修改 river-server/pom.xml 添加子模块**

在 `<modules>` 中添加：
```xml
<module>river-module-affiliate</module>
```

**Step 6: 编译验证**

```bash
cd river-server
mvn compile -pl river-module-affiliate -am
```
Expected: BUILD SUCCESS

**Step 7: Commit**

```bash
git add river-module-affiliate/ pom.xml
git commit -m "feat(affiliate): 创建 river-module-affiliate 模块结构"
```

---

## Task 2: 创建联盟网络实体 (AffiliateNetwork)

**Files:**
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/dataobject/AffiliateNetworkDO.java`
- Create: `river-module-affiliate-api/src/main/java/com/river/module/affiliate/enums/NetworkTypeEnum.java`
- Create: `river-module-affiliate-api/src/main/java/com/river/module/affiliate/enums/NetworkStatusEnum.java`

**Step 1: 创建网络类型枚举**

```java
package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NetworkTypeEnum {

    CPS(1, "CPS", "按销售付费"),
    CPA(2, "CPA", "按行动付费"),
    CPC(3, "CPC", "按点击付费"),
    HYBRID(4, "HYBRID", "混合模式");

    private final Integer code;
    private final String name;
    private final String description;

    public static NetworkTypeEnum getByCode(Integer code) {
        for (NetworkTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
```

**Step 2: 创建网络状态枚举**

```java
package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NetworkStatusEnum {

    ACTIVE(1, "启用"),
    INACTIVE(0, "停用"),
    PENDING(2, "待审核");

    private final Integer code;
    private final String name;

    public static NetworkStatusEnum getByCode(Integer code) {
        for (NetworkStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
```

**Step 3: 创建 AffiliateNetworkDO**

```java
package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.river.module.affiliate.enums.NetworkStatusEnum;
import com.river.module.affiliate.enums.NetworkTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 联盟网络 DO
 */
@TableName("river_affiliate_network")
@KeySequence("river_affiliate_network_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateNetworkDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 联盟编码（唯一标识）
     * 如：admitad, cj, awin
     */
    private String code;

    /**
     * 联盟名称
     */
    private String name;

    /**
     * 联盟类型
     * {@link NetworkTypeEnum}
     */
    private Integer type;

    /**
     * API 基础地址
     */
    private String apiBaseUrl;

    /**
     * 状态
     * {@link NetworkStatusEnum}
     */
    private Integer status;

    /**
     * 联盟官网
     */
    private String websiteUrl;

    /**
     * Logo URL
     */
    private String logoUrl;

    /**
     * 描述
     */
    private String description;

    /**
     * 备注
     */
    private String remark;
}
```

**Step 4: 编译验证**

```bash
mvn compile -pl river-module-affiliate/river-module-affiliate-biz -am
```
Expected: BUILD SUCCESS

**Step 5: Commit**

```bash
git add river-module-affiliate/
git commit -m "feat(affiliate): 添加 AffiliateNetworkDO 实体和枚举类"
```

---

## Task 3: 创建网络凭证实体 (NetworkCredential)

**Files:**
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/dataobject/NetworkCredentialDO.java`
- Create: `river-module-affiliate-api/src/main/java/com/river/module/affiliate/enums/AuthTypeEnum.java`

**Step 1: 创建认证类型枚举**

```java
package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthTypeEnum {

    OAUTH2(1, "OAuth2"),
    BEARER_TOKEN(2, "Bearer Token"),
    API_KEY(3, "API Key"),
    BASIC_AUTH(4, "Basic Auth");

    private final Integer code;
    private final String name;
}
```

**Step 2: 创建 NetworkCredentialDO**

```java
package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.river.module.affiliate.enums.AuthTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 联盟网络凭证 DO
 */
@TableName("river_affiliate_network_credential")
@KeySequence("river_affiliate_network_credential_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkCredentialDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 关联的联盟网络 ID
     */
    private Long networkId;

    /**
     * 认证类型
     * {@link AuthTypeEnum}
     */
    private Integer authType;

    /**
     * 凭证数据（JSON 加密存储）
     * OAuth2: {"client_id": "xxx", "client_secret": "xxx", "access_token": "xxx", "refresh_token": "xxx"}
     * Bearer: {"token": "xxx"}
     * API Key: {"api_key": "xxx", "api_secret": "xxx"}
     */
    private String credentials;

    /**
     * Token 过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
```

**Step 3: Commit**

```bash
git add river-module-affiliate/
git commit -m "feat(affiliate): 添加 NetworkCredentialDO 凭证实体"
```

---

## Task 4: 创建 Postback 密钥实体 (PostbackSecret)

**Files:**
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/dataobject/PostbackSecretDO.java`

**Step 1: 创建 PostbackSecretDO**

```java
package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Postback 回调验证配置 DO
 */
@TableName("river_affiliate_postback_secret")
@KeySequence("river_affiliate_postback_secret_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostbackSecretDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 关联的联盟网络 ID
     */
    private Long networkId;

    /**
     * 签名密钥
     */
    private String secretKey;

    /**
     * 签名算法
     * 如：HMAC-SHA256, HMAC-MD5
     */
    private String algorithm;

    /**
     * 允许的 IP 白名单（JSON 数组）
     * ["1.2.3.4", "5.6.7.8"]
     */
    private String allowedIps;

    /**
     * 是否启用 IP 白名单
     */
    private Boolean ipWhitelistEnabled;

    /**
     * 是否启用签名验证
     */
    private Boolean signatureEnabled;
}
```

**Step 2: Commit**

```bash
git add river-module-affiliate/
git commit -m "feat(affiliate): 添加 PostbackSecretDO 回调验证配置"
```

---

## Task 5: 创建商家实体 (Merchant)

**Files:**
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/dataobject/MerchantDO.java`

**Step 1: 创建 MerchantDO**

```java
package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 商家/广告主 DO
 */
@TableName("river_affiliate_merchant")
@KeySequence("river_affiliate_merchant_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 关联的联盟网络 ID
     */
    private Long networkId;

    /**
     * 联盟侧商家 ID
     */
    private String externalId;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 商家域名
     */
    private String domain;

    /**
     * Logo URL
     */
    private String logoUrl;

    /**
     * 商家描述
     */
    private String description;

    /**
     * 商家评级（1-5）
     */
    private BigDecimal rating;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;

    /**
     * 支持的国家/地区（JSON 数组）
     */
    private String regions;

    /**
     * 分类 ID 列表（JSON 数组）
     */
    private String categoryIds;
}
```

**Step 2: Commit**

```bash
git add river-module-affiliate/
git commit -m "feat(affiliate): 添加 MerchantDO 商家实体"
```

---

## Task 6: 创建分类实体 (Category)

**Files:**
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/dataobject/CategoryDO.java`

**Step 1: 创建 CategoryDO**

```java
package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 分类 DO
 */
@TableName("river_affiliate_category")
@KeySequence("river_affiliate_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 父分类 ID，0 表示顶级
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * URL Slug
     */
    private String slug;

    /**
     * 层级：1-一级，2-二级，3-三级
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态：0-停用，1-启用
     */
    private Integer status;
}
```

**Step 2: Commit**

```bash
git add river-module-affiliate/
git commit -m "feat(affiliate): 添加 CategoryDO 分类实体"
```

---

## Task 7: 创建 Offer 实体

**Files:**
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/dataobject/OfferDO.java`
- Create: `river-module-affiliate-api/src/main/java/com/river/module/affiliate/enums/CommissionTypeEnum.java`
- Create: `river-module-affiliate-api/src/main/java/com/river/module/affiliate/enums/OfferStatusEnum.java`

**Step 1: 创建佣金类型枚举**

```java
package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommissionTypeEnum {

    PERCENT(1, "百分比"),
    FIXED(2, "固定金额"),
    TIERED(3, "阶梯佣金");

    private final Integer code;
    private final String name;
}
```

**Step 2: 创建 Offer 状态枚举**

```java
package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OfferStatusEnum {

    ACTIVE(1, "可投放"),
    PAUSED(2, "暂停"),
    ENDED(3, "已结束"),
    PENDING(0, "待审核");

    private final Integer code;
    private final String name;
}
```

**Step 3: 创建 OfferDO**

```java
package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.river.module.affiliate.enums.CommissionTypeEnum;
import com.river.module.affiliate.enums.OfferStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * Offer/广告 DO
 */
@TableName("river_affiliate_offer")
@KeySequence("river_affiliate_offer_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 关联商家 ID
     */
    private Long merchantId;

    /**
     * 关联联盟网络 ID
     */
    private Long networkId;

    /**
     * 联盟侧 Offer ID
     */
    private String externalId;

    /**
     * Offer 名称
     */
    private String name;

    /**
     * Offer 描述
     */
    private String description;

    /**
     * 佣金类型
     * {@link CommissionTypeEnum}
     */
    private Integer commissionType;

    /**
     * 佣金数值
     * 百分比时为 0.05 表示 5%，固定金额时为具体金额
     */
    private BigDecimal commissionValue;

    /**
     * 佣金货币
     */
    private String currency;

    /**
     * Cookie 有效期（天）
     */
    private Integer cookieDays;

    /**
     * 追踪链接模板
     * 包含占位符：{click_id}, {sub1}, {sub2} 等
     */
    private String trackingUrlTemplate;

    /**
     * 落地页 URL
     */
    private String landingUrl;

    /**
     * 状态
     * {@link OfferStatusEnum}
     */
    private Integer status;

    /**
     * 支持的地区（JSON 数组）
     */
    private String regions;

    /**
     * 分类 ID 列表（JSON 数组）
     */
    private String categoryIds;

    /**
     * 标签（JSON 数组）
     * ["热门", "高佣", "新品"]
     */
    private String tags;

    /**
     * 图片 URL
     */
    private String imageUrl;

    /**
     * EPC（每次点击收益）- 统计值
     */
    private BigDecimal epc;

    /**
     * 转化率 - 统计值
     */
    private BigDecimal conversionRate;

    /**
     * 是否编辑推荐
     */
    private Boolean featured;

    /**
     * 热度分数
     */
    private Integer hotScore;
}
```

**Step 4: 编译验证**

```bash
mvn compile -pl river-module-affiliate/river-module-affiliate-biz -am
```
Expected: BUILD SUCCESS

**Step 5: Commit**

```bash
git add river-module-affiliate/
git commit -m "feat(affiliate): 添加 OfferDO 实体和佣金/状态枚举"
```

---

## Task 8: 创建数据库 SQL 脚本

**Files:**
- Create: `river-server/sql/postgresql/affiliate/river_affiliate.sql`

**Step 1: 创建 SQL 文件**

```sql
-- =============================================
-- River Affiliate Module - PostgreSQL Schema
-- =============================================

-- 联盟网络表
CREATE TABLE river_affiliate_network (
    id              BIGINT PRIMARY KEY,
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    type            SMALLINT NOT NULL DEFAULT 1,
    api_base_url    VARCHAR(500),
    status          SMALLINT NOT NULL DEFAULT 1,
    website_url     VARCHAR(500),
    logo_url        VARCHAR(500),
    description     TEXT,
    remark          VARCHAR(500),
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_affiliate_network_code ON river_affiliate_network(code, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_affiliate_network_status ON river_affiliate_network(status);
COMMENT ON TABLE river_affiliate_network IS '联盟网络表';

-- 联盟凭证表
CREATE TABLE river_affiliate_network_credential (
    id              BIGINT PRIMARY KEY,
    network_id      BIGINT NOT NULL,
    auth_type       SMALLINT NOT NULL,
    credentials     TEXT NOT NULL,
    expires_at      TIMESTAMP,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_network_credential_network ON river_affiliate_network_credential(network_id);
COMMENT ON TABLE river_affiliate_network_credential IS '联盟网络凭证表';

-- Postback 密钥表
CREATE TABLE river_affiliate_postback_secret (
    id                      BIGINT PRIMARY KEY,
    network_id              BIGINT NOT NULL,
    secret_key              VARCHAR(200) NOT NULL,
    algorithm               VARCHAR(50) NOT NULL DEFAULT 'HMAC-SHA256',
    allowed_ips             TEXT,
    ip_whitelist_enabled    BOOLEAN NOT NULL DEFAULT FALSE,
    signature_enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 SMALLINT NOT NULL DEFAULT 0,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_postback_secret_network ON river_affiliate_postback_secret(network_id);
COMMENT ON TABLE river_affiliate_postback_secret IS 'Postback回调验证配置表';

-- 商家表
CREATE TABLE river_affiliate_merchant (
    id              BIGINT PRIMARY KEY,
    network_id      BIGINT NOT NULL,
    external_id     VARCHAR(100),
    name            VARCHAR(200) NOT NULL,
    domain          VARCHAR(200),
    logo_url        VARCHAR(500),
    description     TEXT,
    rating          DECIMAL(3,2),
    status          SMALLINT NOT NULL DEFAULT 1,
    regions         TEXT,
    category_ids    TEXT,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_merchant_network ON river_affiliate_merchant(network_id);
CREATE INDEX idx_merchant_status ON river_affiliate_merchant(status);
CREATE UNIQUE INDEX uk_merchant_external ON river_affiliate_merchant(network_id, external_id, tenant_id) WHERE deleted = 0 AND external_id IS NOT NULL;
COMMENT ON TABLE river_affiliate_merchant IS '商家/广告主表';

-- 分类表
CREATE TABLE river_affiliate_category (
    id              BIGINT PRIMARY KEY,
    parent_id       BIGINT NOT NULL DEFAULT 0,
    name            VARCHAR(100) NOT NULL,
    slug            VARCHAR(100) NOT NULL,
    level           SMALLINT NOT NULL DEFAULT 1,
    sort            INT NOT NULL DEFAULT 0,
    icon            VARCHAR(200),
    status          SMALLINT NOT NULL DEFAULT 1,
    creator         VARCHAR(64) DEFAULT '',
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64) DEFAULT '',
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT NOT NULL DEFAULT 0,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_category_slug ON river_affiliate_category(slug, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_category_parent ON river_affiliate_category(parent_id);
COMMENT ON TABLE river_affiliate_category IS '分类表';

-- Offer 表
CREATE TABLE river_affiliate_offer (
    id                      BIGINT PRIMARY KEY,
    merchant_id             BIGINT NOT NULL,
    network_id              BIGINT NOT NULL,
    external_id             VARCHAR(100),
    name                    VARCHAR(300) NOT NULL,
    description             TEXT,
    commission_type         SMALLINT NOT NULL DEFAULT 1,
    commission_value        DECIMAL(10,4) NOT NULL DEFAULT 0,
    currency                VARCHAR(10) NOT NULL DEFAULT 'USD',
    cookie_days             INT DEFAULT 30,
    tracking_url_template   TEXT,
    landing_url             VARCHAR(1000),
    status                  SMALLINT NOT NULL DEFAULT 1,
    regions                 TEXT,
    category_ids            TEXT,
    tags                    TEXT,
    image_url               VARCHAR(500),
    epc                     DECIMAL(10,4),
    conversion_rate         DECIMAL(5,4),
    featured                BOOLEAN NOT NULL DEFAULT FALSE,
    hot_score               INT NOT NULL DEFAULT 0,
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 SMALLINT NOT NULL DEFAULT 0,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_offer_merchant ON river_affiliate_offer(merchant_id);
CREATE INDEX idx_offer_network ON river_affiliate_offer(network_id);
CREATE INDEX idx_offer_status ON river_affiliate_offer(status);
CREATE INDEX idx_offer_featured ON river_affiliate_offer(featured) WHERE featured = TRUE;
CREATE UNIQUE INDEX uk_offer_external ON river_affiliate_offer(network_id, external_id, tenant_id) WHERE deleted = 0 AND external_id IS NOT NULL;
COMMENT ON TABLE river_affiliate_offer IS 'Offer/广告表';

-- 序列
CREATE SEQUENCE IF NOT EXISTS river_affiliate_network_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_network_credential_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_postback_secret_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_merchant_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_category_seq START 1;
CREATE SEQUENCE IF NOT EXISTS river_affiliate_offer_seq START 1;
```

**Step 2: Commit**

```bash
git add river-server/sql/
git commit -m "feat(affiliate): 添加联盟模块数据库 SQL 脚本"
```

---

## Task 9: 创建 Mapper 接口

**Files:**
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/mysql/AffiliateNetworkMapper.java`
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/mysql/MerchantMapper.java`
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/mysql/CategoryMapper.java`
- Create: `river-module-affiliate-biz/src/main/java/com/river/module/affiliate/dal/mysql/OfferMapper.java`

**Step 1: 创建 AffiliateNetworkMapper**

```java
package com.river.module.affiliate.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AffiliateNetworkMapper extends BaseMapperX<AffiliateNetworkDO> {

}
```

**Step 2: 创建 MerchantMapper**

```java
package com.river.module.affiliate.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper extends BaseMapperX<MerchantDO> {

}
```

**Step 3: 创建 CategoryMapper**

```java
package com.river.module.affiliate.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapperX<CategoryDO> {

}
```

**Step 4: 创建 OfferMapper**

```java
package com.river.module.affiliate.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.affiliate.dal.dataobject.OfferDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OfferMapper extends BaseMapperX<OfferDO> {

}
```

**Step 5: Commit**

```bash
git add river-module-affiliate/
git commit -m "feat(affiliate): 添加 Mapper 接口"
```

---

## Task 10-15: Service 层实现

> 后续任务（Service、Controller、VO）将在下一个计划文档中详细展开。
> 本计划覆盖 Phase 1 的基础实体和数据层，预计 2-3 天完成。

---

## 检查点

完成 Task 1-9 后，验证：

```bash
cd river-server
mvn compile -pl river-module-affiliate -am
```

Expected: BUILD SUCCESS，所有实体和 Mapper 编译通过。

---

## 下一步

完成本计划后，继续：
1. `2026-01-13-phase1-affiliate-service.md` - Service 层实现
2. `2026-01-13-phase1-affiliate-controller.md` - Controller 和 VO 实现
3. `2026-01-13-phase1-affiliate-admitad.md` - Admitad API 对接
