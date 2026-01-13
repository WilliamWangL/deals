# Critical Fixes Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复 Oracle 审查发现的 Critical 和 Important 问题，使平台核心链路可用

**Architecture:** 
- 实现 `/api/go/{id}` 追踪重定向 + `/api/postback/{network}` 回调接收
- 添加 Attribution 归因实体和逻辑
- 修复多租户隔离问题
- 统一 API 路径规范

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, PostgreSQL

---

## Phase 1: Critical Fixes

### Task 1: 实现追踪重定向接口 /api/go/{id}

**Files:**
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/controller/app/TrackingRedirectController.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/service/ClickService.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/service/ClickServiceImpl.java`
- Modify: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/mysql/ClickMapper.java`

**Step 1: 创建 ClickService 接口**

```java
package com.river.module.tracking.service;

import com.river.module.tracking.dal.dataobject.ClickDO;

public interface ClickService {
    
    /**
     * 记录点击并返回重定向 URL
     * @param trackingLinkId 追踪链接 ID
     * @param sub1-sub5 Sub ID 参数
     * @param ip 用户 IP
     * @param userAgent UA
     * @param referer 来源
     * @return 重定向目标 URL
     */
    String recordClickAndGetRedirectUrl(Long trackingLinkId, 
                                        String sub1, String sub2, String sub3, String sub4, String sub5,
                                        String ip, String userAgent, String referer);
    
    /**
     * 根据 clickId 查询点击记录
     */
    ClickDO getByClickId(String clickId);
}
```

**Step 2: 实现 ClickServiceImpl**

核心逻辑：
1. 查询 TrackingLink，验证存在且启用
2. 生成 ULID 作为 click_id
3. 异步写入 Click 记录
4. 获取关联的 Offer，替换 trackingUrlTemplate 中的占位符
5. 返回最终 URL

```java
@Service
@Validated
public class ClickServiceImpl implements ClickService {

    @Resource
    private ClickMapper clickMapper;
    @Resource
    private TrackingLinkMapper trackingLinkMapper;
    @Resource
    private OfferMapper offerMapper; // 跨模块调用需要 API

    @Override
    public String recordClickAndGetRedirectUrl(Long trackingLinkId, 
            String sub1, String sub2, String sub3, String sub4, String sub5,
            String ip, String userAgent, String referer) {
        
        // 1. 查询追踪链接
        TrackingLinkDO link = trackingLinkMapper.selectById(trackingLinkId);
        if (link == null || link.getStatus() != 1) {
            throw exception(TRACKING_LINK_NOT_EXISTS);
        }
        
        // 2. 生成 ULID
        String clickId = generateUlid();
        
        // 3. 合并预设 Sub ID 和请求 Sub ID
        String finalSub1 = StringUtils.hasText(sub1) ? sub1 : link.getPresetSub1();
        // ... sub2-sub5 同理
        
        // 4. 异步写入 Click
        ClickDO click = ClickDO.builder()
            .clickId(clickId)
            .offerId(link.getOfferId())
            .sub1(finalSub1)
            // ... 其他字段
            .ip(ip)
            .userAgent(userAgent)
            .referer(referer)
            .clickTime(LocalDateTime.now())
            .build();
        
        // 使用异步或直接插入
        clickMapper.insert(click);
        
        // 5. 获取 Offer 并构建重定向 URL
        // 需要跨模块调用 affiliate 模块
        String trackingUrl = buildTrackingUrl(link.getOfferId(), clickId, finalSub1, finalSub2, ...);
        
        return trackingUrl;
    }
    
    private String generateUlid() {
        // 使用 ULID 库或自实现
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 26);
    }
}
```

**Step 3: 创建 TrackingRedirectController**

```java
package com.river.module.tracking.controller.app;

import com.river.module.tracking.service.ClickService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/go")
public class TrackingRedirectController {

    @Resource
    private ClickService clickService;

    @GetMapping("/{id}")
    public ResponseEntity<Void> redirect(
            @PathVariable("id") Long trackingLinkId,
            @RequestParam(value = "sub1", required = false) String sub1,
            @RequestParam(value = "sub2", required = false) String sub2,
            @RequestParam(value = "sub3", required = false) String sub3,
            @RequestParam(value = "sub4", required = false) String sub4,
            @RequestParam(value = "sub5", required = false) String sub5,
            HttpServletRequest request) {
        
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        
        String redirectUrl = clickService.recordClickAndGetRedirectUrl(
            trackingLinkId, sub1, sub2, sub3, sub4, sub5, ip, userAgent, referer);
        
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}
```

**Verification:**
```bash
mvn compile -pl river-module-tracking/river-module-tracking-biz -am
```

**Commit:**
```bash
git add river-module-tracking/
git commit -m "feat(tracking): 实现 /api/go/{id} 追踪重定向接口"
```

---

### Task 2: 实现 Postback 回调接口 /api/postback/{network}

**Files:**
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/controller/app/PostbackController.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/service/PostbackService.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/service/PostbackServiceImpl.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/service/PostbackSignatureValidator.java`

**Step 1: 创建签名验证器**

```java
package com.river.module.tracking.service;

import com.river.module.affiliate.dal.dataobject.PostbackSecretDO;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PostbackSignatureValidator {

    public boolean validate(PostbackSecretDO secret, String payload, String signature) {
        if (!secret.getSignatureEnabled()) {
            return true; // 未启用签名验证
        }
        
        try {
            Mac mac = Mac.getInstance(secret.getAlgorithm().replace("-", ""));
            SecretKeySpec keySpec = new SecretKeySpec(
                secret.getSecretKey().getBytes(), mac.getAlgorithm());
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes());
            String computed = Base64.getEncoder().encodeToString(hash);
            return computed.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validateIp(PostbackSecretDO secret, String clientIp) {
        if (!secret.getIpWhitelistEnabled()) {
            return true;
        }
        // 解析 JSON 数组，检查 IP
        // ...
        return true;
    }
}
```

**Step 2: 创建 PostbackService**

```java
package com.river.module.tracking.service;

public interface PostbackService {
    
    /**
     * 处理 Postback 回调
     * @return 处理结果（成功/重复/失败）
     */
    PostbackResult handlePostback(String networkCode, String clickId, 
                                   String externalConversionId, 
                                   String conversionType, BigDecimal commission,
                                   String currency, String signature, String clientIp,
                                   String rawPayload);
}

public enum PostbackResult {
    SUCCESS,           // 新转化，处理成功
    DUPLICATE,         // 重复回调，已存在
    INVALID_SIGNATURE, // 签名验证失败
    CLICK_NOT_FOUND,   // 找不到点击记录
    ERROR              // 其他错误
}
```

**Step 3: 实现 PostbackServiceImpl**

核心逻辑：
1. 验证签名（HMAC）
2. 验证 IP 白名单（可选）
3. 检查幂等（external_conversion_id）
4. 查找 Click 记录
5. 创建 Conversion + Attribution
6. 如找不到 Click，写入 UnattributedConversion

**Step 4: 创建 PostbackController**

```java
@RestController
@RequestMapping("/api/postback")
@Slf4j
public class PostbackController {

    @Resource
    private PostbackService postbackService;

    @GetMapping("/{networkCode}")
    public ResponseEntity<String> handlePostback(
            @PathVariable String networkCode,
            @RequestParam(value = "click_id", required = false) String clickId,
            @RequestParam(value = "transaction_id", required = false) String externalConversionId,
            @RequestParam(value = "type", defaultValue = "sale") String conversionType,
            @RequestParam(value = "amount", defaultValue = "0") BigDecimal commission,
            @RequestParam(value = "currency", defaultValue = "USD") String currency,
            @RequestParam(value = "sig", required = false) String signature,
            HttpServletRequest request) {
        
        String clientIp = getClientIp(request);
        String rawPayload = request.getQueryString();
        
        PostbackResult result = postbackService.handlePostback(
            networkCode, clickId, externalConversionId, conversionType,
            commission, currency, signature, clientIp, rawPayload);
        
        // 无论结果如何，都返回 200（防止联盟重试）
        return ResponseEntity.ok(result.name());
    }
}
```

**Verification:**
```bash
mvn compile -pl river-module-tracking/river-module-tracking-biz -am
```

**Commit:**
```bash
git add river-module-tracking/
git commit -m "feat(tracking): 实现 /api/postback/{network} 回调接口（含签名验证和幂等）"
```

---

### Task 3: 添加 Attribution 归因实体

**Files:**
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/dataobject/AttributionDO.java`
- Create: `river-module-tracking-biz/src/main/java/com/river/module/tracking/dal/mysql/AttributionMapper.java`
- Create: `river-module-tracking-api/src/main/java/com/river/module/tracking/enums/AttributionTypeEnum.java`
- Modify: `river-server/sql/postgresql/tracking/river_tracking.sql`

**Step 1: 创建 AttributionTypeEnum**

```java
package com.river.module.tracking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttributionTypeEnum {
    LAST_CLICK(1, "最后点击"),
    FIRST_CLICK(2, "首次点击"),
    LINEAR(3, "线性归因");

    private final Integer code;
    private final String name;
}
```

**Step 2: 创建 AttributionDO**

```java
package com.river.module.tracking.dal.dataobject;

@TableName("river_tracking_attribution")
@KeySequence("river_tracking_attribution_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributionDO extends BaseDO {

    @TableId
    private Long id;
    
    /** 关联转化 ID */
    private Long conversionId;
    
    /** 关联点击 ID */
    private String clickId;
    
    /** 归因类型 */
    private Integer attributionType;
    
    /** 归因置信度 (0-100) */
    private Integer confidenceScore;
    
    /** 归因时间窗口（秒） */
    private Long attributionWindow;
}
```

**Step 3: 更新 SQL**

```sql
-- Attribution 表
CREATE TABLE river_tracking_attribution (
    id                  BIGINT PRIMARY KEY,
    conversion_id       BIGINT NOT NULL,
    click_id            VARCHAR(26) NOT NULL,
    attribution_type    SMALLINT NOT NULL DEFAULT 1,
    confidence_score    SMALLINT NOT NULL DEFAULT 100,
    attribution_window  BIGINT,
    creator             VARCHAR(64) DEFAULT '',
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64) DEFAULT '',
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT NOT NULL DEFAULT 0,
    tenant_id           BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_attribution_conversion ON river_tracking_attribution(conversion_id);
CREATE INDEX idx_attribution_click ON river_tracking_attribution(click_id);
CREATE SEQUENCE IF NOT EXISTS river_tracking_attribution_seq START 1;
```

**Commit:**
```bash
git add river-module-tracking/ river-server/sql/
git commit -m "feat(tracking): 添加 Attribution 归因实体"
```

---

### Task 4: 修复多租户隔离

**Files:**
- Modify: 所有模块的 DO 类，将 `extends BaseDO` 改为 `extends TenantBaseDO`

**Step 1: 检查 TenantBaseDO 是否存在**

```bash
grep -r "TenantBaseDO" river-server/river-framework/
```

**Step 2: 批量修改 DO 类**

需要修改的文件：
- `river-module-affiliate-biz/.../dal/dataobject/*.java`
- `river-module-tracking-biz/.../dal/dataobject/*.java`
- `river-module-coupon-biz/.../dal/dataobject/*.java`
- `river-module-blog-biz/.../dal/dataobject/*.java`
- `river-module-campaign-biz/.../dal/dataobject/*.java`
- `river-module-stats-biz/.../dal/dataobject/*.java`

修改内容：
```java
// Before
public class XxxDO extends BaseDO {

// After
public class XxxDO extends TenantBaseDO {
```

**Commit:**
```bash
git add river-module-*/
git commit -m "fix: 所有业务 DO 改用 TenantBaseDO 确保多租户隔离"
```

---

## Phase 2: Important Fixes

### Task 5: 统一 API 路径规范

**Files:**
- Modify: 所有模块的 Controller 类

**Step 1: 确认项目是否有全局前缀配置**

检查 `application.yaml` 或 `WebMvcConfig`。

**Step 2: 如无全局配置，修改 Controller 路径**

```java
// Before
@RequestMapping("/tracking/link")

// After
@RequestMapping("/admin-api/tracking/link")
```

或在 `server` 模块的配置中添加：
```yaml
river:
  web:
    admin-api:
      prefix: /admin-api
```

**Commit:**
```bash
git commit -m "fix: 统一管理后台 API 路径为 /admin-api/"
```

---

### Task 6: 点击表按月分区

**Files:**
- Modify: `river-server/sql/postgresql/tracking/river_tracking.sql`

**Step 1: 修改为分区表**

```sql
-- 删除原表定义，改为分区表
CREATE TABLE river_tracking_click (
    click_id        VARCHAR(26) NOT NULL,
    offer_id        BIGINT,
    campaign_id     BIGINT,
    -- ... 其他字段
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tenant_id       BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (click_id, created_at)
) PARTITION BY RANGE (created_at);

-- 创建当月和下月分区
CREATE TABLE river_tracking_click_2026_01 
    PARTITION OF river_tracking_click
    FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');

CREATE TABLE river_tracking_click_2026_02 
    PARTITION OF river_tracking_click
    FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');
```

**Commit:**
```bash
git commit -m "feat(tracking): Click 表改为按月分区"
```

---

## Verification Checklist

- [ ] `/api/go/{id}` 可访问，返回 302 重定向
- [ ] `/api/postback/{network}` 可接收回调
- [ ] Postback 签名验证生效
- [ ] Postback 幂等检查生效（重复调用返回 DUPLICATE）
- [ ] Attribution 记录被正确创建
- [ ] 多租户隔离：不同租户数据互相不可见
- [ ] 所有模块编译通过
- [ ] 所有 API 路径符合 `/admin-api/` 规范
