# Ecommica 地区过滤功能设计

## 概述

为 river-ecommica 后台接口添加基于 IP 地理定位的地区过滤功能，返回用户所在国家可用的 Deal、Coupon、Merchant 数据。

## 需求摘要

| 项目 | 决策 |
|------|------|
| IP 定位服务 | MaxMind GeoLite2（免费、离线、国家级别） |
| 过滤粒度 | 国家级别（ISO 3166-1 alpha-2） |
| 过滤范围 | Deal + Coupon + Merchant |
| 无法定位时 | 返回"全球"可用数据 |
| 地区选择优先级 | URL 参数 > Cookie > IP 定位 |
| 参数传递方式 | 请求参数 `?region=US` |
| 定位执行层 | Next.js 服务端（Node.js Runtime） |

## 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    用户访问 ecommica                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Next.js Middleware (middleware.ts)                         │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 1. 检查 URL 参数 ?region=XX                              ││
│  │ 2. 检查 Cookie region                                   ││
│  │ 3. 从 X-Forwarded-For 获取 IP → MaxMind GeoLite2 定位   ││
│  │ 4. 将 region 写入请求上下文                              ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  API 调用层 (lib/api.ts)                                     │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ fetchDeals({ region: 'US' })                            ││
│  │ → GET /app-api/coupon/deal/page?region=US               ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Spring Boot 后端                                            │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 接收 region 参数，过滤 regions 字段包含该国家的记录       ││
│  │ 无 region 参数时返回全球数据                              ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

## 数据模型

现有 `regions` 字段格式：ISO 代码，逗号分隔（如 `US`、`ES,US`、`AT,AU,BE,...`）

### DO 字段优化

使用 `StringListTypeHandler` 将 `regions` 字段从 `String` 改为 `List<String>`：

```java
@TableField(typeHandler = StringListTypeHandler.class)
private List<String> regions;  // ["US", "GB", "DE"]
```

### 全球数据判定规则

- `regions` 为空或 null
- `regions` 包含 `"00"`
- `regions` 数量 >= 50

## Next.js 前端实现

### MaxMind GeoLite2 集成

```typescript
// src/lib/geo.ts
import { Reader } from '@maxmind/geoip2-node'
import path from 'path'

let reader: Reader | null = null

export async function initGeoReader() {
  if (!reader) {
    const dbPath = path.join(process.cwd(), 'data', 'GeoLite2-Country.mmdb')
    reader = await Reader.open(dbPath)
  }
  return reader
}

export async function getCountryByIP(ip: string): Promise<string | null> {
  const r = await initGeoReader()
  try {
    if (ip === '::1' || ip === '127.0.0.1' || ip.startsWith('192.168.')) {
      return null
    }
    const response = r.country(ip)
    return response.country?.isoCode || null
  } catch {
    return null
  }
}

export function getClientIP(headers: Headers): string {
  return headers.get('x-forwarded-for')?.split(',')[0]?.trim()
    || headers.get('x-real-ip')
    || headers.get('cf-connecting-ip')
    || ''
}
```

### 地区优先级逻辑

```typescript
// src/lib/region.ts
export function resolveRegion(
  urlParam: string | null,
  cookie: string | null,
  ipCountry: string | null
): string {
  return urlParam || cookie || ipCountry || 'GLOBAL'
}
```

### 数据存储

| 存储位置 | 用途 | 过期时间 |
|---------|------|---------|
| URL 参数 `?region=XX` | 分享链接、SEO | 无 |
| Cookie `region` | 用户手动选择 | 30 天 |

## Spring Boot 后端实现

### 服务层过滤逻辑

```java
public PageResult<DealDO> getDealPage(DealPageReqVO reqVO, String region) {
    PageResult<DealDO> pageResult = dealMapper.selectPage(reqVO);

    if (StrUtil.isBlank(region) || "GLOBAL".equals(region)) {
        List<DealDO> filtered = pageResult.getList().stream()
            .filter(this::isGlobalDeal)
            .toList();
        return new PageResult<>(filtered, filtered.size());
    }

    List<DealDO> filtered = pageResult.getList().stream()
        .filter(d -> d.getRegions() != null && d.getRegions().contains(region))
        .toList();
    return new PageResult<>(filtered, filtered.size());
}

private boolean isGlobalDeal(DealDO deal) {
    List<String> regions = deal.getRegions();
    if (regions == null || regions.isEmpty()) return true;
    if (regions.contains("00")) return true;
    return regions.size() >= 50;
}
```

### 国家列表 API

```java
@GetMapping("/available")
@Operation(summary = "获取有数据的国家列表")
public CommonResult<List<RegionRespVO>> getAvailableRegions() {
    return success(regionService.getAvailableRegions());
}
```

返回结构：

```json
{
  "code": 0,
  "data": [
    { "code": "GLOBAL", "name": "Global", "count": 120 },
    { "code": "US", "name": "United States", "count": 45 },
    { "code": "GB", "name": "United Kingdom", "count": 32 }
  ]
}
```

### 缓存策略

| 层级 | 缓存时间 |
|------|---------|
| Redis | 1 小时 |
| Next.js | 5 分钟 |

## 实现任务清单

### 后端改动 (river-server)

| 文件 | 改动 |
|------|------|
| `DealDO.java` | `regions` 改为 `List<String>` + TypeHandler |
| `CouponDO.java` | `regions` 改为 `List<String>` + TypeHandler |
| `MerchantDO.java` | `regions` 改为 `List<String>` + TypeHandler |
| `AppDealController.java` | 添加 `region` 参数 |
| `AppCouponController.java` | 添加 `region` 参数 |
| `AppMerchantController.java` | 添加 `region` 参数 |
| `DealServiceImpl.java` | 添加地区过滤逻辑 |
| `CouponServiceImpl.java` | 添加地区过滤逻辑 |
| `MerchantServiceImpl.java` | 添加地区过滤逻辑 |
| **新增** `AppRegionController.java` | 国家列表 API |
| **新增** `RegionService.java` | 国家聚合逻辑 |

### 前端改动 (river-ecommica)

| 文件 | 改动 |
|------|------|
| **新增** `src/lib/geo.ts` | MaxMind 集成 |
| **新增** `src/lib/region.ts` | 地区优先级逻辑 |
| `src/middleware.ts` | 添加地区检测逻辑 |
| `src/lib/api.ts` | 所有 API 添加 `region` 参数 |
| **新增** `src/components/layout/RegionSelector.tsx` | 地区选择器 |
| `src/components/layout/Header.tsx` | 集成地区选择器 |
| `data/GeoLite2-Country.mmdb` | MaxMind 数据库文件 |
| `.gitignore` | 忽略 mmdb 文件 |
| `package.json` | 添加 `@maxmind/geoip2-node` |

### 依赖项

| 项目 | 依赖 | 版本 |
|------|------|------|
| river-ecommica | `@maxmind/geoip2-node` | latest |
| river-ecommica | `cookies-next` | latest |

## MaxMind GeoLite2 获取方式

1. 注册 https://www.maxmind.com/en/geolite2/signup
2. 获取 License Key
3. 下载 `GeoLite2-Country.mmdb`（约 6MB）
4. 放置于 `river-ecommica/data/` 目录
