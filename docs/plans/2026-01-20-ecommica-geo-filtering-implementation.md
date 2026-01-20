# Ecommica 地区过滤功能实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 ecommica 添加基于 IP 地理定位的地区过滤功能，返回用户所在国家可用的 Deal、Coupon、Merchant 数据。

**Architecture:** Next.js Middleware 检测用户 IP 并通过 MaxMind GeoLite2 获取国家代码，将 region 参数传递给后端 API。后端根据 region 参数过滤 regions 字段包含该国家的数据。

**Tech Stack:** MaxMind GeoLite2, @maxmind/geoip2-node, Spring Boot, MyBatis Plus StringListTypeHandler

---

## Phase 1: 后端 - DO 字段类型优化

### Task 1.1: 修改 DealDO 使用 StringListTypeHandler

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/dal/dataobject/DealDO.java:55-56`

**Step 1: 添加 import 语句**

在 DealDO.java 顶部添加：

```java
import com.baomidou.mybatisplus.annotation.TableField;
import com.river.framework.mybatis.core.type.StringListTypeHandler;
import java.util.List;
```

**Step 2: 修改 regions 字段**

将：
```java
/** 适用地区（ISO 代码，逗号分隔） */
private String regions;
```

改为：
```java
/** 适用地区（ISO 代码列表） */
@TableField(typeHandler = StringListTypeHandler.class)
private List<String> regions;
```

**Step 3: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-coupon -am`
Expected: BUILD SUCCESS

---

### Task 1.2: 修改 CouponDO 使用 StringListTypeHandler

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/dal/dataobject/CouponDO.java:52-53`

**Step 1: 添加 import 语句**

```java
import com.baomidou.mybatisplus.annotation.TableField;
import com.river.framework.mybatis.core.type.StringListTypeHandler;
import java.util.List;
```

**Step 2: 修改 regions 字段**

将：
```java
/** 适用地区（ISO 代码，逗号分隔） */
private String regions;
```

改为：
```java
/** 适用地区（ISO 代码列表） */
@TableField(typeHandler = StringListTypeHandler.class)
private List<String> regions;
```

**Step 3: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-coupon -am`
Expected: BUILD SUCCESS

---

### Task 1.3: 修改 MerchantDO 使用 StringListTypeHandler

**Files:**
- Modify: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/dal/dataobject/MerchantDO.java:54-55`

**Step 1: 添加 import 语句**

```java
import com.baomidou.mybatisplus.annotation.TableField;
import com.river.framework.mybatis.core.type.StringListTypeHandler;
import java.util.List;
```

**Step 2: 修改 regions 字段**

将：
```java
/** 支持的国家/地区（JSON 数组） */
private String regions;
```

改为：
```java
/** 支持的国家/地区列表 */
@TableField(typeHandler = StringListTypeHandler.class)
private List<String> regions;
```

**Step 3: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-affiliate -am`
Expected: BUILD SUCCESS

---

### Task 1.4: 修复 AppMerchantController 中的 parseRegions 方法

**Files:**
- Modify: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/controller/app/AppMerchantController.java:86,92-97,116`

regions 字段已改为 `List<String>`，不再需要 parseRegions 方法。

**Step 1: 删除 parseRegions 方法**

删除以下代码（第 92-97 行）：
```java
private List<String> parseRegions(String regions) {
    if (regions == null || regions.isBlank()) {
        return Collections.emptyList();
    }
    return Arrays.asList(regions.split(","));
}
```

**Step 2: 修改 convertToAppVO 方法**

将第 86 行：
```java
vo.setRegions(parseRegions(merchant.getRegions()));
```

改为：
```java
vo.setRegions(merchant.getRegions() != null ? merchant.getRegions() : Collections.emptyList());
```

**Step 3: 修改 convertToAppVOList 方法**

将第 116 行：
```java
vo.setRegions(parseRegions(merchant.getRegions()));
```

改为：
```java
vo.setRegions(merchant.getRegions() != null ? merchant.getRegions() : Collections.emptyList());
```

**Step 4: 删除无用 import**

删除：
```java
import java.util.Arrays;
```

**Step 5: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-affiliate -am`
Expected: BUILD SUCCESS

---

## Phase 2: 后端 - 地区过滤工具类

### Task 2.1: 创建 RegionUtils 工具类

**Files:**
- Create: `river-server/river-framework/river-common/src/main/java/com/river/framework/common/util/region/RegionUtils.java`

**Step 1: 创建文件**

```java
package com.river.framework.common.util.region;

import java.util.List;

/**
 * 地区过滤工具类
 */
public class RegionUtils {

    /** 全球标记 */
    public static final String GLOBAL = "GLOBAL";

    /** 特殊全球标记（数据库中使用） */
    public static final String GLOBAL_CODE = "00";

    /** 视为全球的最小国家数量 */
    public static final int GLOBAL_THRESHOLD = 50;

    /**
     * 判断是否为全球数据
     */
    public static boolean isGlobalRegion(List<String> regions) {
        if (regions == null || regions.isEmpty()) {
            return true;
        }
        if (regions.contains(GLOBAL_CODE)) {
            return true;
        }
        return regions.size() >= GLOBAL_THRESHOLD;
    }

    /**
     * 判断数据是否匹配指定地区
     */
    public static boolean matchesRegion(List<String> regions, String targetRegion) {
        if (targetRegion == null || targetRegion.isBlank() || GLOBAL.equals(targetRegion)) {
            return isGlobalRegion(regions);
        }
        if (regions == null || regions.isEmpty()) {
            return true; // 空 regions 视为全球可用
        }
        return regions.contains(targetRegion) || isGlobalRegion(regions);
    }

    private RegionUtils() {}
}
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-framework/river-common`
Expected: BUILD SUCCESS

---

## Phase 3: 后端 - Service 层添加地区过滤

### Task 3.1: 修改 DealService 接口添加地区过滤方法

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/service/DealService.java`

**Step 1: 添加方法签名**

在接口中添加：
```java
PageResult<DealDO> getDealPageByRegion(DealPageReqVO pageReqVO, String region);

List<DealDO> getDealListByRegion(String region);
```

**Step 2: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-coupon -am`
Expected: BUILD FAILURE (实现类未实现新方法)

---

### Task 3.2: 修改 DealServiceImpl 实现地区过滤

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/service/DealServiceImpl.java`

**Step 1: 添加 import**

```java
import com.river.framework.common.util.region.RegionUtils;
```

**Step 2: 实现 getDealPageByRegion 方法**

```java
@Override
public PageResult<DealDO> getDealPageByRegion(DealPageReqVO pageReqVO, String region) {
    PageResult<DealDO> pageResult = dealMapper.selectPage(pageReqVO);

    List<DealDO> filtered = pageResult.getList().stream()
        .filter(deal -> RegionUtils.matchesRegion(deal.getRegions(), region))
        .toList();

    return new PageResult<>(filtered, (long) filtered.size());
}

@Override
public List<DealDO> getDealListByRegion(String region) {
    List<DealDO> list = dealMapper.selectList();
    return list.stream()
        .filter(deal -> RegionUtils.matchesRegion(deal.getRegions(), region))
        .toList();
}
```

**Step 3: 编译验证**

Run: `cd river-server && mvn compile -pl river-module-coupon -am`
Expected: BUILD SUCCESS

---

### Task 3.3: 修改 CouponService 接口添加地区过滤方法

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/service/CouponService.java`

**Step 1: 添加方法签名**

```java
PageResult<CouponDO> getCouponPageByRegion(CouponPageReqVO pageReqVO, String region);

List<CouponDO> getCouponListByRegion(String region);
```

---

### Task 3.4: 修改 CouponServiceImpl 实现地区过滤

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/service/CouponServiceImpl.java`

**Step 1: 添加 import**

```java
import com.river.framework.common.util.region.RegionUtils;
```

**Step 2: 实现方法**

```java
@Override
public PageResult<CouponDO> getCouponPageByRegion(CouponPageReqVO pageReqVO, String region) {
    PageResult<CouponDO> pageResult = couponMapper.selectPage(pageReqVO);

    List<CouponDO> filtered = pageResult.getList().stream()
        .filter(coupon -> RegionUtils.matchesRegion(coupon.getRegions(), region))
        .toList();

    return new PageResult<>(filtered, (long) filtered.size());
}

@Override
public List<CouponDO> getCouponListByRegion(String region) {
    List<CouponDO> list = couponMapper.selectList();
    return list.stream()
        .filter(coupon -> RegionUtils.matchesRegion(coupon.getRegions(), region))
        .toList();
}
```

---

### Task 3.5: 修改 MerchantService 接口添加地区过滤方法

**Files:**
- Modify: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/service/MerchantService.java`

**Step 1: 添加方法签名**

```java
PageResult<MerchantDO> getMerchantPageByRegion(MerchantPageReqVO pageReqVO, String region);

List<MerchantDO> getMerchantListByRegion(String region);
```

---

### Task 3.6: 修改 MerchantServiceImpl 实现地区过滤

**Files:**
- Modify: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/service/MerchantServiceImpl.java`

**Step 1: 添加 import**

```java
import com.river.framework.common.util.region.RegionUtils;
```

**Step 2: 实现方法**

```java
@Override
public PageResult<MerchantDO> getMerchantPageByRegion(MerchantPageReqVO pageReqVO, String region) {
    PageResult<MerchantDO> pageResult = merchantMapper.selectPage(pageReqVO);

    List<MerchantDO> filtered = pageResult.getList().stream()
        .filter(merchant -> RegionUtils.matchesRegion(merchant.getRegions(), region))
        .toList();

    return new PageResult<>(filtered, (long) filtered.size());
}

@Override
public List<MerchantDO> getMerchantListByRegion(String region) {
    List<MerchantDO> list = merchantMapper.selectList();
    return list.stream()
        .filter(merchant -> RegionUtils.matchesRegion(merchant.getRegions(), region))
        .toList();
}
```

---

## Phase 4: 后端 - Controller 添加 region 参数

### Task 4.1: 修改 AppDealController 添加 region 参数

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/AppDealController.java`

**Step 1: 修改 getDealList 方法**

将：
```java
@GetMapping("/list")
@Operation(summary = "获取 Deal 列表")
public CommonResult<List<AppDealRespVO>> getDealList(
        @RequestParam(value = "merchantId", required = false) Long merchantId,
        @RequestParam(value = "featured", required = false) Boolean featured) {
    List<DealDO> list = dealService.getDealList();
```

改为：
```java
@GetMapping("/list")
@Operation(summary = "获取 Deal 列表")
public CommonResult<List<AppDealRespVO>> getDealList(
        @RequestParam(value = "merchantId", required = false) Long merchantId,
        @RequestParam(value = "featured", required = false) Boolean featured,
        @RequestParam(value = "region", required = false) String region) {
    List<DealDO> list = dealService.getDealListByRegion(region);
```

**Step 2: 修改 getDealPage 方法**

将：
```java
@GetMapping("/page")
@Operation(summary = "获取 Deal 分页")
public CommonResult<PageResult<AppDealRespVO>> getDealPage(@Valid AppDealPageReqVO pageReqVO) {
```

改为：
```java
@GetMapping("/page")
@Operation(summary = "获取 Deal 分页")
public CommonResult<PageResult<AppDealRespVO>> getDealPage(
        @Valid AppDealPageReqVO pageReqVO,
        @RequestParam(value = "region", required = false) String region) {
```

并修改调用：
```java
PageResult<DealDO> pageResult = dealService.getDealPageByRegion(adminPageReqVO, region);
```

---

### Task 4.2: 修改 AppCouponController 添加 region 参数

**Files:**
- Modify: `river-server/river-module-coupon/src/main/java/com/river/module/coupon/controller/app/AppCouponController.java`

**Step 1: 修改 getCouponList 方法**

添加 `region` 参数：
```java
@RequestParam(value = "region", required = false) String region
```

调用改为：
```java
List<CouponDO> list = couponService.getCouponListByRegion(region);
```

**Step 2: 修改 getCouponPage 方法**

添加 `region` 参数并修改调用：
```java
PageResult<CouponDO> pageResult = couponService.getCouponPageByRegion(adminPageReqVO, region);
```

---

### Task 4.3: 修改 AppMerchantController 添加 region 参数

**Files:**
- Modify: `river-server/river-module-affiliate/src/main/java/com/river/module/affiliate/controller/app/AppMerchantController.java`

**Step 1: 修改 getMerchantList 方法**

```java
@GetMapping("/list")
@Operation(summary = "获取商家列表")
public CommonResult<List<AppMerchantRespVO>> getMerchantList(
        @RequestParam(value = "region", required = false) String region) {
    List<MerchantDO> list = merchantService.getMerchantListByRegion(region);
    return success(convertToAppVOList(list));
}
```

**Step 2: 修改 getMerchantPage 方法**

添加 `region` 参数：
```java
@GetMapping("/page")
@Operation(summary = "获取商家分页")
public CommonResult<PageResult<AppMerchantRespVO>> getMerchantPage(
        @Valid AppMerchantPageReqVO pageReqVO,
        @RequestParam(value = "region", required = false) String region) {
    // ...
    PageResult<MerchantDO> pageResult = merchantService.getMerchantPageByRegion(adminPageReqVO, region);
    return success(convertToAppVOPage(pageResult));
}
```

---

## Phase 5: 后端 - 国家列表 API

### Task 5.1: 创建 RegionRespVO

**Files:**
- Create: `river-server/river-module-system/src/main/java/com/river/module/system/controller/app/region/vo/RegionRespVO.java`

**Step 1: 创建 VO 类**

```java
package com.river.module.system.controller.app.region.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 App - 可用地区 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionRespVO {

    @Schema(description = "国家代码", example = "US")
    private String code;

    @Schema(description = "国家名称", example = "United States")
    private String name;

    @Schema(description = "数据数量", example = "45")
    private Integer count;
}
```

---

### Task 5.2: 创建 RegionService 接口

**Files:**
- Create: `river-server/river-module-system/src/main/java/com/river/module/system/service/region/RegionService.java`

**Step 1: 创建接口**

```java
package com.river.module.system.service.region;

import com.river.module.system.controller.app.region.vo.RegionRespVO;

import java.util.List;

public interface RegionService {

    /**
     * 获取有数据的可用地区列表
     */
    List<RegionRespVO> getAvailableRegions();
}
```

---

### Task 5.3: 创建 RegionServiceImpl 实现类

**Files:**
- Create: `river-server/river-module-system/src/main/java/com/river/module/system/service/region/RegionServiceImpl.java`

**Step 1: 创建实现类**

```java
package com.river.module.system.service.region;

import com.river.module.system.controller.app.region.vo.RegionRespVO;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /** ISO 国家代码到名称的映射 */
    private static final Map<String, String> COUNTRY_NAMES = Map.ofEntries(
        Map.entry("US", "United States"),
        Map.entry("GB", "United Kingdom"),
        Map.entry("DE", "Germany"),
        Map.entry("FR", "France"),
        Map.entry("ES", "Spain"),
        Map.entry("IT", "Italy"),
        Map.entry("NL", "Netherlands"),
        Map.entry("AU", "Australia"),
        Map.entry("CA", "Canada"),
        Map.entry("BR", "Brazil"),
        Map.entry("MX", "Mexico"),
        Map.entry("RU", "Russia"),
        Map.entry("IN", "India"),
        Map.entry("CN", "China"),
        Map.entry("JP", "Japan"),
        Map.entry("KR", "South Korea"),
        Map.entry("PL", "Poland"),
        Map.entry("UA", "Ukraine"),
        Map.entry("BY", "Belarus"),
        Map.entry("KZ", "Kazakhstan"),
        Map.entry("00", "Global")
        // 可根据需要扩展
    );

    @Override
    public List<RegionRespVO> getAvailableRegions() {
        Map<String, Integer> regionCounts = new HashMap<>();

        // 统计 Deal 表
        countRegions("SELECT regions FROM river_coupon_deal WHERE deleted = 0", regionCounts);
        // 统计 Coupon 表
        countRegions("SELECT regions FROM river_coupon_coupon WHERE deleted = 0", regionCounts);
        // 统计 Merchant 表
        countRegions("SELECT regions FROM river_affiliate_merchant WHERE deleted = 0", regionCounts);

        // 转换为结果列表
        List<RegionRespVO> result = new ArrayList<>();

        // 添加 GLOBAL 选项到首位
        int globalCount = regionCounts.values().stream().mapToInt(Integer::intValue).sum();
        result.add(new RegionRespVO("GLOBAL", "Global", globalCount));

        // 添加各个国家，按数量降序排序
        regionCounts.entrySet().stream()
            .filter(e -> !"00".equals(e.getKey())) // 排除特殊标记
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(50) // 只返回前50个国家
            .forEach(e -> {
                String name = COUNTRY_NAMES.getOrDefault(e.getKey(), e.getKey());
                result.add(new RegionRespVO(e.getKey(), name, e.getValue()));
            });

        return result;
    }

    private void countRegions(String sql, Map<String, Integer> regionCounts) {
        List<String> regionsList = jdbcTemplate.queryForList(sql, String.class);
        for (String regions : regionsList) {
            if (regions == null || regions.isBlank()) continue;
            for (String code : regions.split(",")) {
                String trimmed = code.trim();
                if (!trimmed.isEmpty()) {
                    regionCounts.merge(trimmed, 1, Integer::sum);
                }
            }
        }
    }
}
```

---

### Task 5.4: 创建 AppRegionController

**Files:**
- Create: `river-server/river-module-system/src/main/java/com/river/module/system/controller/app/region/AppRegionController.java`

**Step 1: 创建 Controller**

```java
package com.river.module.system.controller.app.region;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.system.controller.app.region.vo.RegionRespVO;
import com.river.module.system.service.region.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 地区")
@RestController
@RequestMapping("/system/region")
@Validated
@PermitAll
public class AppRegionController {

    @Resource
    private RegionService regionService;

    @GetMapping("/available")
    @Operation(summary = "获取有数据的国家列表")
    public CommonResult<List<RegionRespVO>> getAvailableRegions() {
        return success(regionService.getAvailableRegions());
    }
}
```

---

### Task 5.5: 编译验证后端

**Step 1: 编译整个项目**

Run: `cd river-server && mvn clean compile`
Expected: BUILD SUCCESS

---

## Phase 6: 前端 - MaxMind 集成

### Task 6.1: 安装依赖

**Step 1: 安装 @maxmind/geoip2-node**

Run: `cd river-ecommica && pnpm add @maxmind/geoip2-node`
Expected: 成功安装

**Step 2: 安装 cookies-next（如果未安装）**

Run: `cd river-ecommica && pnpm add cookies-next`
Expected: 成功安装

---

### Task 6.2: 更新 .gitignore

**Files:**
- Modify: `river-ecommica/.gitignore`

**Step 1: 添加 mmdb 忽略规则**

在文件末尾添加：
```
# MaxMind GeoLite2 database
data/*.mmdb
```

---

### Task 6.3: 创建 data 目录和占位文件

**Step 1: 创建目录**

Run: `mkdir -p river-ecommica/data`

**Step 2: 创建 README**

**Files:**
- Create: `river-ecommica/data/README.md`

```markdown
# GeoLite2 数据库

此目录用于存放 MaxMind GeoLite2 数据库文件。

## 获取方式

1. 注册 MaxMind 账号：https://www.maxmind.com/en/geolite2/signup
2. 获取 License Key
3. 下载 GeoLite2-Country.mmdb（约 6MB）
4. 将文件放置于此目录

## 注意事项

- `.mmdb` 文件已被 .gitignore 忽略
- 部署时需单独上传此文件
```

---

### Task 6.4: 创建 geo.ts

**Files:**
- Create: `river-ecommica/src/lib/geo.ts`

**Step 1: 创建文件**

```typescript
import { Reader, CountryResponse } from '@maxmind/geoip2-node'
import path from 'path'
import fs from 'fs'

let reader: Reader | null = null
let readerError: Error | null = null

/**
 * 初始化 GeoIP Reader（单例模式）
 */
async function initGeoReader(): Promise<Reader | null> {
  if (reader) return reader
  if (readerError) return null

  const dbPath = path.join(process.cwd(), 'data', 'GeoLite2-Country.mmdb')

  // 检查文件是否存在
  if (!fs.existsSync(dbPath)) {
    console.warn('[GeoIP] GeoLite2-Country.mmdb not found at:', dbPath)
    readerError = new Error('Database file not found')
    return null
  }

  try {
    reader = await Reader.open(dbPath)
    console.log('[GeoIP] Database loaded successfully')
    return reader
  } catch (error) {
    console.error('[GeoIP] Failed to load database:', error)
    readerError = error as Error
    return null
  }
}

/**
 * 根据 IP 获取国家代码
 */
export async function getCountryByIP(ip: string): Promise<string | null> {
  // 处理本地开发环境的 IP
  if (!ip || ip === '::1' || ip === '127.0.0.1' || ip.startsWith('192.168.') || ip.startsWith('10.')) {
    return null
  }

  const r = await initGeoReader()
  if (!r) return null

  try {
    const response: CountryResponse = r.country(ip)
    return response.country?.isoCode || null
  } catch (error) {
    // IP 不在数据库中（如保留 IP）
    return null
  }
}

/**
 * 从请求头中获取客户端 IP
 */
export function getClientIP(headers: Headers): string {
  // 优先级：Cloudflare > X-Forwarded-For > X-Real-IP
  const cfIP = headers.get('cf-connecting-ip')
  if (cfIP) return cfIP

  const forwardedFor = headers.get('x-forwarded-for')
  if (forwardedFor) {
    // X-Forwarded-For 可能包含多个 IP，取第一个
    return forwardedFor.split(',')[0].trim()
  }

  const realIP = headers.get('x-real-ip')
  if (realIP) return realIP

  return ''
}
```

---

### Task 6.5: 创建 region.ts

**Files:**
- Create: `river-ecommica/src/lib/region.ts`

**Step 1: 创建文件**

```typescript
import { cookies } from 'next/headers'

export const REGION_COOKIE_NAME = 'region'
export const REGION_COOKIE_MAX_AGE = 30 * 24 * 60 * 60 // 30 days
export const DEFAULT_REGION = 'GLOBAL'

/**
 * 解析地区优先级
 * URL 参数 > Cookie > IP 定位 > 默认全球
 */
export function resolveRegion(
  urlParam: string | null | undefined,
  cookieValue: string | null | undefined,
  ipCountry: string | null | undefined
): string {
  if (urlParam && urlParam !== '') {
    return urlParam.toUpperCase()
  }
  if (cookieValue && cookieValue !== '') {
    return cookieValue.toUpperCase()
  }
  if (ipCountry && ipCountry !== '') {
    return ipCountry.toUpperCase()
  }
  return DEFAULT_REGION
}

/**
 * 从服务端获取当前地区（用于 Server Components）
 */
export async function getServerRegion(
  searchParams: { region?: string },
  ipCountry: string | null
): Promise<string> {
  const cookieStore = await cookies()
  const cookieValue = cookieStore.get(REGION_COOKIE_NAME)?.value

  return resolveRegion(searchParams.region, cookieValue, ipCountry)
}
```

---

### Task 6.6: 修改 api.ts 添加 region 参数

**Files:**
- Modify: `river-ecommica/src/lib/api.ts`

**Step 1: 修改 fetchDeals 函数**

将：
```typescript
export async function fetchDeals(params?: { merchantId?: number; featured?: boolean; pageNo?: number; pageSize?: number }): Promise<PageResult<Deal>> {
```

改为：
```typescript
export async function fetchDeals(params?: { merchantId?: number; featured?: boolean; pageNo?: number; pageSize?: number; region?: string }): Promise<PageResult<Deal>> {
```

并添加：
```typescript
if (params?.region) url.searchParams.set('region', params.region)
```

**Step 2: 修改 fetchStores 函数**

添加 `region` 参数：
```typescript
export async function fetchStores(params?: { pageNo?: number; pageSize?: number; name?: string; region?: string }): Promise<PageResult<Store>> {
```

并添加：
```typescript
if (params?.region) url.searchParams.set('region', params.region);
```

**Step 3: 修改 fetchCoupons 函数**

添加 `region` 参数：
```typescript
export async function fetchCoupons(params?: { merchantId?: number; verified?: boolean; pageNo?: number; pageSize?: number; region?: string }): Promise<PageResult<Coupon>> {
```

并添加：
```typescript
if (params?.region) url.searchParams.set('region', params.region)
```

**Step 4: 添加 fetchAvailableRegions 函数**

```typescript
export interface Region {
  code: string;
  name: string;
  count: number;
}

export async function fetchAvailableRegions(): Promise<Region[]> {
  const res = await fetchWithTenant(`${API_BASE_URL}/system/region/available`, {
    next: { revalidate: 300 },
  })
  if (!res.ok) throw new Error('Fetch regions failed')
  const json = await res.json()
  return json.data || []
}
```

---

### Task 6.7: 创建 RegionSelector 组件

**Files:**
- Create: `river-ecommica/src/components/layout/RegionSelector.tsx`

**Step 1: 创建组件**

```typescript
'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { setCookie } from 'cookies-next'
import { Globe, ChevronDown, Check } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { cn } from '@/lib/utils'
import { REGION_COOKIE_NAME, REGION_COOKIE_MAX_AGE } from '@/lib/region'

interface Region {
  code: string
  name: string
  count: number
}

interface RegionSelectorProps {
  currentRegion: string
  regions: Region[]
}

export function RegionSelector({ currentRegion, regions }: RegionSelectorProps) {
  const router = useRouter()
  const [isOpen, setIsOpen] = useState(false)

  const currentRegionData = regions.find(r => r.code === currentRegion) || regions[0]

  const handleSelect = (code: string) => {
    setCookie(REGION_COOKIE_NAME, code, { maxAge: REGION_COOKIE_MAX_AGE })
    setIsOpen(false)
    router.refresh()
  }

  return (
    <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="sm"
          className="h-9 px-2 gap-1.5 text-muted-foreground hover:text-foreground"
        >
          <Globe className="h-4 w-4" />
          <span className="hidden sm:inline text-sm">
            {currentRegionData?.name || 'Global'}
          </span>
          <ChevronDown className="h-3 w-3" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-48 max-h-64 overflow-y-auto">
        {regions.map((region) => (
          <DropdownMenuItem
            key={region.code}
            onClick={() => handleSelect(region.code)}
            className={cn(
              "flex items-center justify-between cursor-pointer",
              currentRegion === region.code && "bg-muted"
            )}
          >
            <span>{region.name}</span>
            <div className="flex items-center gap-2">
              <span className="text-xs text-muted-foreground">{region.count}</span>
              {currentRegion === region.code && (
                <Check className="h-4 w-4 text-primary" />
              )}
            </div>
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
```

---

### Task 6.8: 修改 Header.tsx 集成地区选择器

**Files:**
- Modify: `river-ecommica/src/components/layout/Header.tsx`

**Step 1: 添加 import**

```typescript
import { RegionSelector } from '@/components/layout/RegionSelector'
```

**Step 2: 修改组件 Props**

将：
```typescript
export function Header() {
```

改为：
```typescript
interface HeaderProps {
  currentRegion?: string
  regions?: { code: string; name: string; count: number }[]
}

export function Header({ currentRegion = 'GLOBAL', regions = [] }: HeaderProps) {
```

**Step 3: 在右侧操作区添加 RegionSelector**

在 `LanguageSwitcher` 之前添加：
```tsx
{regions.length > 0 && (
  <RegionSelector currentRegion={currentRegion} regions={regions} />
)}
```

---

### Task 6.9: 创建 RegionProvider 用于获取地区数据

**Files:**
- Create: `river-ecommica/src/components/providers/RegionProvider.tsx`

**Step 1: 创建 Provider**

```typescript
import { headers } from 'next/headers'
import { cookies } from 'next/headers'
import { getClientIP, getCountryByIP } from '@/lib/geo'
import { resolveRegion, REGION_COOKIE_NAME } from '@/lib/region'
import { fetchAvailableRegions } from '@/lib/api'

interface RegionData {
  currentRegion: string
  regions: { code: string; name: string; count: number }[]
}

export async function getRegionData(searchParams?: { region?: string }): Promise<RegionData> {
  // 获取请求头和 Cookie
  const headersList = await headers()
  const cookieStore = await cookies()

  // 获取客户端 IP 并定位
  const ip = getClientIP(headersList)
  const ipCountry = await getCountryByIP(ip)

  // 获取 Cookie 中的地区
  const cookieRegion = cookieStore.get(REGION_COOKIE_NAME)?.value

  // 解析最终地区
  const currentRegion = resolveRegion(
    searchParams?.region,
    cookieRegion,
    ipCountry
  )

  // 获取可用地区列表
  let regions: { code: string; name: string; count: number }[] = []
  try {
    regions = await fetchAvailableRegions()
  } catch (error) {
    console.error('[Region] Failed to fetch regions:', error)
    // 降级为默认 GLOBAL
    regions = [{ code: 'GLOBAL', name: 'Global', count: 0 }]
  }

  return { currentRegion, regions }
}
```

---

### Task 6.10: 编译验证前端

**Step 1: 类型检查**

Run: `cd river-ecommica && pnpm ts:check`
Expected: 无错误

**Step 2: 构建验证**

Run: `cd river-ecommica && pnpm build`
Expected: 构建成功

---

## Phase 7: 集成测试

### Task 7.1: 启动后端服务

Run: `cd river-server/river-server && mvn spring-boot:run`
Expected: 服务启动成功

### Task 7.2: 测试国家列表 API

Run: `curl http://localhost:48080/app-api/system/region/available -H "tenant-id: 1"`
Expected: 返回国家列表 JSON

### Task 7.3: 测试 Deal 地区过滤

Run: `curl "http://localhost:48080/app-api/coupon/deal/page?region=US" -H "tenant-id: 1"`
Expected: 只返回 regions 包含 US 的数据

### Task 7.4: 启动前端服务

Run: `cd river-ecommica && pnpm dev`
Expected: 服务启动成功，可在 Header 看到地区选择器

---

## Commit 检查点

完成每个 Phase 后建议提交：

1. **Phase 1-4 完成后**: `git commit -m "feat(backend): add region filtering for Deal/Coupon/Merchant APIs"`
2. **Phase 5 完成后**: `git commit -m "feat(backend): add available regions API"`
3. **Phase 6-7 完成后**: `git commit -m "feat(ecommica): integrate MaxMind geo-location and region selector"`
