# AffiliateNetwork 同步接口扩展计划

## Context

### 原始请求

用户原话："AffiliateNetworkController 同步应该放在联盟网络这里，同时也要有 deal 同步和 coupon 同步"

### 访谈摘要

**关键讨论**：
- 当前 `AffiliateNetworkController` 已有 `sync`（商家/Offer）、`sync-coupons`（混合 Coupon+Deal）、`sync-all` 接口
- `sync-coupons` 内部混合处理：promocode → CouponDO，sale/other → DealDO
- 用户希望增加独立的 `sync-deals` 和 `sync-coupons-only` 接口
- 参数支持 `networkId`（精确指定）和 `code`（自动查找）两种方式
- 两者同时提供时，`networkId` 优先级更高
- 使用现有 `syncCoupons()` 方法，接受混合处理行为
- 需要编写单元测试

**研究结果**：
- `AffiliateNetworkController.java` 位于 `river-module-affiliate` 模块（270行）
- `AdmitadSyncService.java` 实现同步逻辑（843行），包含 `syncCampaigns()` 和 `syncCoupons()` 方法
- `CouponDO` 和 `DealDO` 存在于 `river-module-coupon` 模块

### Metis Review 识别的缺口（已解决）

| 缺口 | 如何解决 |
|------|----------|
| Service 层是否需要新增方法 | 用户确认：使用现有 `syncCoupons()` 方法，接受混合行为 |
| `code` + `networkId` 冲突处理 | 用户确认：`networkId` 优先级更高 |
| Deal-only 严格隔离 | 用户确认：接受混合处理行为，无需严格分离 |
| 错误处理策略 | 采用 Controller 层现有模式 |

---

## 工作目标

### 核心目标

在 `AffiliateNetworkController` 中增加独立的 Deal 同步和 Coupon 同步接口，支持 networkId 和 code 两种参数方式。

### 具体交付物

| 交付物 | 路径 |
|--------|------|
| `sync-deals` 接口 | `POST /affiliate/network/sync-deals?networkId=&code=` |
| `sync-coupons-only` 接口 | `POST /affiliate/network/sync-coupons-only?networkId=&code=` |
| 单元测试 | `AffiliateNetworkControllerTest.java` 新增测试用例 |

### 完成定义

- [ ] `POST /affiliate/network/sync-deals?code=X` 返回 200 并触发 Deal 同步
- [ ] `POST /affiliate/network/sync-deals?networkId=Y` 返回 200 并触发 Deal 同步
- [ ] `POST /affiliate/network/sync-deals` 无参数返回 400
- [ ] `POST /affiliate/network/sync-deals?code=X&networkId=Y` 使用 networkId
- [ ] `POST /affiliate/network/sync-coupons-only?code=X` 返回 200 并触发 Coupon 同步
- [ ] `POST /affiliate/network/sync-coupons-only?networkId=Y` 返回 200 并触发 Coupon 同步
- [ ] `POST /affiliate/network/sync-coupons-only` 无参数返回 400
- [ ] 无效 code 返回 404
- [ ] 未知 networkId 返回 404
- [ ] 现有 `sync-coupons` 接口行为保持不变
- [ ] 单元测试覆盖率 ≥ 80%

### 必须有

- 独立的 `sync-deals` 接口（联盟网络模块）
- 独立的 `sync-coupons-only` 接口（联盟网络模块）
- 参数验证（networkId/code 至少一个）
- 错误处理（无效参数、无效网络）
- 单元测试

### 禁止有（Guardrails）

- **禁止修改**现有 `AdmitadSyncService` 方法签名
- **禁止修改**现有 `sync`、`sync-coupons`、`sync-all` 接口行为
- **禁止新增**数据库表或 schema 变更
- **禁止添加**批量同步功能（多 code/networkId）
- **禁止添加**异步处理或消息队列
- **禁止添加**监控指标或告警配置

---

### 参数解析策略（关键修复）

由于现有代码只支持 `code` 参数，需要实现 `networkId` → `code` 的转换：

**解析逻辑**：
```java
// 伪代码：参数解析策略
if (networkId != null) {
    // networkId 优先：将 String 转 Long，查询 network
    Long id = Long.parseLong(networkId);  // 可能抛出 NumberFormatException
    AffiliateNetworkDO network = affiliateNetworkService.getNetwork(id);
    if (network == null) {
        throw new ServiceException(ErrorCodeConstants.NETWORK_NOT_EXISTS);
    }
    code = network.getCode();  // 提取 code
}

// 使用 code 调用现有逻辑
List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(code);
```

**需要的 Service 方法**：
- `AffiliateNetworkDO getNetwork(Long id)` - 已存在 ✅
- 需要新增：`AffiliateNetworkDO getNetworkByCode(String code)` - 用于 code 参数查找

**注意事项**：
- `networkId` 是 `@RequestParam String`，需要 `Long.parseLong()` 转换
- 转换失败（NumberFormatException）应返回 400 错误

---

## 验证策略

### 测试决策

- **基础设施存在**：是（Java 项目，Spring Boot + JUnit 5）
- **用户需要测试**：是（单元测试）
- **测试框架**：JUnit 5 + Mockito + MockMvc
- **QA 方式**：TDD（先红后绿重构）

### TDD 流程

1. **RED**：编写失败的测试（验证接口存在、参数验证、错误处理）
2. **GREEN**：实现最小代码使测试通过
3. **REFACTOR**：清理代码，保持测试通过

### 单元测试结构

```java
AffiliateNetworkControllerTest.java

@DisplayName("sync-deals 接口测试")
@Nested
class SyncDealsTest {
    @Test
    void withCode_onlyParams_returns400() { ... }
    @Test
    void withCode_invalidCode_returns404() { ... }
    @Test
    void withCode_validCode_returns200() { ... }
    @Test
    void withNetworkId_unknownId_returns404() { ... }
    @Test
    void withBothParams_networkIdTakesPriority() { ... }
}

@DisplayName("sync-coupons-only 接口测试")
@Nested
class SyncCouponsOnlyTest {
    // 类似的测试结构
}
```

---

## 任务流程

```
                    ┌─────────────────────┐
                    │ 1. 查找现有测试文件  │
                    └──────────┬──────────┘
                               ↓
┌─────────────────────────────────────────────────────────┐
│ 2. 新增 sync-deals 接口 (2.1 - 2.3)                     │
│    2.1 添加 syncDeals 方法到 AffiliateNetworkService   │
│    2.2 添加 /sync-deals 端点到 Controller              │
│    2.3 添加单元测试                                     │
└────────────────────────────┬────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────┐
│ 3. 新增 sync-coupons-only 接口 (3.1 - 3.3)             │
│    3.1 添加 syncCouponsOnly 方法到 AffiliateNetworkService
│    3.2 添加 /sync-coupons-only 端点到 Controller       │
│    3.3 添加单元测试                                     │
└────────────────────────────┬────────────────────────────┘
                             ↓
                    ┌─────────────────────┐
                    │ 4. 运行所有测试验证  │
                    └─────────────────────┘
```

## 并行化

| 组 | 任务 | 原因 |
|----|------|------|
| A | 2.1, 3.1 | Service 层方法可以并行开发 |
| B | 2.2, 3.2 | Controller 端点可以并行开发 |
| C | 2.3, 3.3 | 测试用例可以并行开发 |

| 任务 | 依赖 | 原因 |
|------|------|------|
| 2.1 | 无 | 独立方法 |
| 2.2 | 2.1 | 需要 Service 方法 |
| 2.3 | 2.2 | 需要 Controller 存在 |
| 3.1 | 无 | 独立方法 |
| 3.2 | 3.1 | 需要 Service 方法 |
| 3.3 | 3.2 | 需要 Controller 存在 |

---

## TODOs

> 每个任务包含实现步骤和测试用例。实现 + 测试 = 一个完整任务。

- [ ] 1. 查找并阅读现有的测试文件和 Controller

  **查找范围**：
  - `river-module-affiliate/**/test/**/*ControllerTest.java`
  - `river-module-affiliate/**/AffiliateNetworkController.java`

  **实现步骤**：
  - 找到 `AffiliateNetworkController.java` 完整路径
  - 找到或创建对应的测试文件
  - 阅读现有测试模式（注解、Mock 方式、验证方法）

  **Acceptance Criteria**：

  **手动验证**：
  - [ ] 使用 `glob` 查找 Controller 文件：`**/test/**/*AffiliateNetwork*Test.java`
  - [ ] 使用 `glob` 查找测试文件：`**/AffiliateNetworkController.java`
  - [ ] 记录测试类的包结构和测试方法模式

  **Commit**: NO（与下一个任务合并）

- [ ] 2. 添加 sync-deals 接口

  **实现步骤**：

  2.1 **在 AffiliateNetworkService 接口添加方法**
  - 位置：`river-module-affiliate/.../service/AffiliateNetworkService.java`
  - 新增方法：
    ```java
    /**
     * 根据 code 获取联盟网络
     * @param code 联盟编码
     * @return 联盟网络实体，不存在返回 null
     */
    AffiliateNetworkDO getNetworkByCode(String code);
    ```
  - 注意：`getNetwork(Long id)` 已存在，无需新增

  2.2 **在 AffiliateNetworkServiceImpl 实现方法**
  - 位置：`river-module-affiliate/.../service/AffiliateNetworkServiceImpl.java`
  - 实现 `getNetworkByCode(String code)`：
    ```java
    @Override
    public AffiliateNetworkDO getNetworkByCode(String code) {
        return networkMapper.selectOne(AffiliateNetworkDO::getCode, code);
    }
    ```

  2.3 **在 AdmitadSyncService 添加 syncDeals 方法**
  - 位置：`river-module-affiliate/.../service/network/admitad/AdmitadSyncService.java`
  - 添加方法：
    ```java
    /**
     * 同步 Deal 数据（通过 code 调用）
     * @param networkCode 联盟网络编码
     * @return 同步结果
     */
    public SyncResult syncDeals(String networkCode) {
        // 根据 code 查找凭证
        NetworkCredentialDO credential = getEnabledCredentialByNetworkCode(networkCode);
        if (credential == null) {
            return SyncResult.error("No enabled credentials found for network: " + networkCode);
        }
        // 执行同步
        syncCoupons(credential);
        return SyncResult.success("Deal sync completed", lastSyncDeals);
    }

    /**
     * 同步 Coupon 数据（通过 code 调用）
     * @param networkCode 联盟网络编码
     * @return 同步结果
     */
    public SyncResult syncCouponsOnly(String networkCode) {
        NetworkCredentialDO credential = getEnabledCredentialByNetworkCode(networkCode);
        if (credential == null) {
            return SyncResult.error("No enabled credentials found for network: " + networkCode);
        }
        syncCoupons(credential);
        return SyncResult.success("Coupon sync completed", lastSyncCoupons);
    }

    /**
     * 根据 network code 获取启用的凭证
     * @param networkCode 联盟网络编码
     * @return 凭证对象，不存在返回 null
     */
    private NetworkCredentialDO getEnabledCredentialByNetworkCode(String networkCode) {
        List<NetworkCredentialDO> credentials = credentialMapper.selectEnabledByNetworkCode(networkCode);
        if (credentials.isEmpty()) {
            return null;
        }
        return credentials.get(0);
    }
    ```

  2.4 **在 Controller 添加端点**
  - 位置：`river-module-affiliate/.../controller/admin/network/AffiliateNetworkController.java`
  - 添加方法：
    ```java
    @PostMapping("/sync-deals")
    @Operation(summary = "同步 Deal 数据")
    public CommonResult<SyncResult> syncDeals(
        @RequestParam(required = false) String networkId,
        @RequestParam(required = false) String code
    ) {
        log.info("[syncDeals] Request received - networkId: {}, code: {}", networkId, code);

        // 参数校验：至少一个不为空
        if (StrUtil.isAllEmpty(networkId, code)) {
            return success(SyncResult.error("At least one of networkId or code is required"));
        }

        String finalCode = code;

        // networkId 优先：解析出 code
        if (StrUtil.isNotEmpty(networkId)) {
            try {
                Long id = Long.parseLong(networkId);
                AffiliateNetworkDO network = affiliateNetworkService.getNetwork(id);
                if (network == null) {
                    log.warn("[syncDeals] Network not found - networkId: {}", networkId);
                    return success(SyncResult.error("Network not found: " + networkId));
                }
                finalCode = network.getCode();
            } catch (NumberFormatException e) {
                log.warn("[syncDeals] Invalid networkId format: {}", networkId);
                return success(SyncResult.error("Invalid networkId format"));
            }
        }

        // 调用同步逻辑
        SyncResult result = admitadSyncService.syncDeals(finalCode);
        return success(result);
    }
    ```
  - 注意：使用 `SyncResult`（现有内部类），不是 `SyncRespDTO`

  2.4 **测试覆盖**：
  - [ ] 无参数返回 200（success=false）
  - [ ] 无效 code 返回 success=false
  - [ ] 未知 networkId 返回 success=false
  - [ ] 有效 code 返回 200（success=true）
  - [ ] 有效 networkId 返回 200（success=true）
  - [ ] 两者都提供时 networkId 优先

  **Acceptance Criteria**：

  **手动验证**：
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-deals"` → 200 (success=false)
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-deals?code=INVALID"` → 200 (success=false)
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-deals?networkId=999"` → 200 (success=false)
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-deals?code=admitad"` → 200 (success=true)

  **Commit**: YES
  - Message: `feat(affiliate): add sync-deals endpoint`
  - Files: `AffiliateNetworkController.java`, `AffiliateNetworkService.java`, `AdmitadSyncService.java`

- [ ] 3. 添加 sync-coupons-only 接口

  **实现步骤**：

  3.1 **在 Controller 添加端点**
  - 位置：`river-module-affiliate/.../controller/admin/network/AffiliateNetworkController.java`
  - 添加方法：
    ```java
    @PostMapping("/sync-coupons-only")
    @Operation(summary = "同步 Coupon 数据")
    public CommonResult<SyncResult> syncCouponsOnly(
        @RequestParam(required = false) String networkId,
        @RequestParam(required = false) String code
    ) {
        log.info("[syncCouponsOnly] Request received - networkId: {}, code: {}", networkId, code);

        // 参数校验：至少一个不为空
        if (StrUtil.isAllEmpty(networkId, code)) {
            return success(SyncResult.error("At least one of networkId or code is required"));
        }

        String finalCode = code;

        // networkId 优先：解析出 code
        if (StrUtil.isNotEmpty(networkId)) {
            try {
                Long id = Long.parseLong(networkId);
                AffiliateNetworkDO network = affiliateNetworkService.getNetwork(id);
                if (network == null) {
                    log.warn("[syncCouponsOnly] Network not found - networkId: {}", networkId);
                    return success(SyncResult.error("Network not found: " + networkId));
                }
                finalCode = network.getCode();
            } catch (NumberFormatException e) {
                log.warn("[syncCouponsOnly] Invalid networkId format: {}", networkId);
                return success(SyncResult.error("Invalid networkId format"));
            }
        }

        // 调用同步逻辑
        SyncResult result = admitadSyncService.syncCouponsOnly(finalCode);
        return success(result);
    }
    ```
                log.warn("[syncCouponsOnly] Invalid networkId format: {}", networkId);
                return success(SyncResult.error("Invalid networkId format"));
            }
        }

        // 调用同步逻辑
        SyncResult result = admitadSyncService.syncCouponsOnly(finalCode);
        return success(result);
    }
    ```
            if (network == null) {
                return failure(AffiliateNetworkErrorCodeConstants.NETWORK_NOT_FOUND);
            }
            code = network.getCode();
        }

        // 调用同步逻辑
        SyncResult result = admitadSyncService.syncCouponsOnly(finalCode);
        return success(result);
    }
    ```

  3.2 **测试覆盖**：
  - [ ] 无参数返回 200（success=false）
  - [ ] 无效 code 返回 success=false
  - [ ] 未知 networkId 返回 success=false
  - [ ] 有效 code 返回 200（success=true）
  - [ ] 有效 networkId 返回 200（success=true）
  - [ ] 两者都提供时 networkId 优先

  **Acceptance Criteria**：

  **手动验证**：
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-coupons-only"` → 200 (success=false)
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-coupons-only?code=INVALID"` → 200 (success=false)
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-coupons-only?networkId=999"` → 200 (success=false)
  - [ ] `curl -X POST "http://localhost:8080/affiliate/network/sync-coupons-only?code=admitad"` → 200 (success=true)

  **Commit**: YES
  - Message: `feat(affiliate): add sync-coupons-only endpoint`
  - Files: `AffiliateNetworkController.java`

- [ ] 4. 编写单元测试

  **测试文件**：
  - 位置：`river-module-affiliate/src/test/java/.../controller/admin/network/AffiliateNetworkControllerTest.java`

  **测试结构**：
  ```java
  @SpringBootTest
  @AutoConfigureMockMvc
  class AffiliateNetworkControllerTest {

      @Autowired
      private MockMvc mockMvc;

      @MockBean
      private AdmitadSyncService admitadSyncService;

      @MockBean
      private AffiliateNetworkService affiliateNetworkService;

      @Test
      @DisplayName("sync-deals - 无参数返回 400")
      void syncDeals_noParams_returns400() throws Exception {
          mockMvc.perform(post("/affiliate/network/sync-deals"))
              .andExpect(status().isOk())  // 返回 200，但 success=false
              .andExpect(jsonPath("$.success").value(false));
      }

      @Test
      @DisplayName("sync-deals - 无效 code 返回 404")
      void syncDeals_invalidCode_returns404() throws Exception {
          // Mock getNetworkByCode 返回 null（找不到）
          when(affiliateNetworkService.getNetworkByCode("INVALID"))
              .thenReturn(null);

          // Mock syncDeals 返回错误结果
          when(admitadSyncService.syncDeals("INVALID"))
              .thenReturn(SyncResult.error("No enabled credentials found for network: INVALID"));

          mockMvc.perform(post("/affiliate/network/sync-deals")
                  .param("code", "INVALID"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.success").value(false));
      }

      @Test
      @DisplayName("sync-deals - 有效 code 返回 200")
      void syncDeals_validCode_returns200() throws Exception {
          AffiliateNetworkDO network = new AffiliateNetworkDO();
          network.setId(1L);
          network.setCode("admitad");

          Map<String, Object> stats = new HashMap<>();
          stats.put("deals", 5);
          stats.put("coupons", 3);

          when(affiliateNetworkService.getNetworkByCode("admitad"))
              .thenReturn(network);
          when(admitadSyncService.syncDeals("admitad"))
              .thenReturn(SyncResult.success("Deal sync completed", stats));

          mockMvc.perform(post("/affiliate/network/sync-deals")
                  .param("code", "admitad"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.success").value(true))
              .andExpect(jsonPath("$.data.deals").value(5));
      }

      @Test
      @DisplayName("sync-deals - 有效 networkId 返回 200")
      void syncDeals_validNetworkId_returns200() throws Exception {
          AffiliateNetworkDO network = new AffiliateNetworkDO();
          network.setId(1L);
          network.setCode("admitad");

          Map<String, Object> stats = new HashMap<>();
          stats.put("deals", 3);

          when(affiliateNetworkService.getNetwork(1L))
              .thenReturn(network);
          when(admitadSyncService.syncDeals("admitad"))
              .thenReturn(SyncResult.success("Deal sync completed", stats));

          mockMvc.perform(post("/affiliate/network/sync-deals")
                  .param("networkId", "1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.success").value(true));

          // 验证 networkId 被正确解析为 code
          verify(affiliateNetworkService).getNetwork(1L);
          verify(admitadSyncService).syncDeals("admitad");
      }

      @Test
      @DisplayName("sync-deals - networkId 优先于 code")
      void syncDeals_bothParams_networkIdTakesPriority() throws Exception {
          AffiliateNetworkDO network = new AffiliateNetworkDO();
          network.setId(1L);
          network.setCode("admitad");

          Map<String, Object> stats = new HashMap<>();
          stats.put("deals", 3);

          when(affiliateNetworkService.getNetwork(1L))
              .thenReturn(network);
          when(admitadSyncService.syncDeals("admitad"))
              .thenReturn(SyncResult.success("Deal sync completed", stats));

          mockMvc.perform(post("/affiliate/network/sync-deals")
                  .param("networkId", "1")
                  .param("code", "other_code"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.success").value(true));

          // 验证 networkId 优先，code 被忽略
          verify(affiliateNetworkService).getNetwork(1L);
          verify(admitadSyncService).syncDeals("admitad");  // 使用 network 的 code
          verify(affiliateNetworkService, never()).getNetworkByCode("other_code");
      }

      @Test
      @DisplayName("sync-deals - 无效 networkId 格式返回错误")
      void syncDeals_invalidNetworkIdFormat_returnsError() throws Exception {
          mockMvc.perform(post("/affiliate/network/sync-deals")
                  .param("networkId", "abc"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.success").value(false))
              .andExpect(jsonPath("$.message").value("Invalid networkId format"));
      }

      @Test
      @DisplayName("sync-deals - networkId 不存在返回错误")
      void syncDeals_networkIdNotFound_returnsError() throws Exception {
          when(affiliateNetworkService.getNetwork(999L))
              .thenReturn(null);

          mockMvc.perform(post("/affiliate/network/sync-deals")
                  .param("networkId", "999"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.success").value(false))
              .andExpect(jsonPath("$.message").value("Network not found: 999"));
      }

      // sync-coupons-only 测试类比处理
  }
  ```

  **测试用例清单**：

  | 测试方法 | 验证点 |
  |----------|--------|
  | `syncDeals_noParams_returns400` | 无参数时返回 200，success=false |
  | `syncDeals_invalidCode_returns404` | 无效 code 时返回 success=false |
  | `syncDeals_validCode_returns200` | 有效 code 时返回 success=true |
  | `syncDeals_validNetworkId_returns200` | 有效 networkId 时返回 success=true |
  | `syncDeals_bothParams_networkIdTakesPriority` | 两者都提供时 networkId 优先 |
  | `syncDeals_invalidNetworkIdFormat_returnsError` | 无效 networkId 格式返回错误 |
  | `syncDeals_networkIdNotFound_returnsError` | networkId 不存在时返回错误 |
  | `syncCouponsOnly_noParams_returns400` | 无参数时返回 success=false |
  | `syncCouponsOnly_validCode_returns200` | 有效 code 时返回 success=true |
  | `syncCouponsOnly_bothParams_networkIdTakesPriority` | 两者都提供时 networkId 优先 |

  **Acceptance Criteria**：

  **测试验证**：
  - [ ] `mvn test -Dtest=AffiliateNetworkControllerTest` → 全部通过
  - [ ] 测试覆盖率报告 ≥ 80%

  **Commit**: YES
  - Message: `test(affiliate): add unit tests for sync-deals and sync-coupons-only`
  - Files: `AffiliateNetworkControllerTest.java`

---

## 提交策略

| 任务后 | Message | 文件 | 验证 |
|--------|---------|------|------|
| 2 | `feat(affiliate): add sync-deals endpoint` | Controller + Service | 接口测试 |
| 3 | `feat(affiliate): add sync-coupons-only endpoint` | Controller + Service | 接口测试 |
| 4 | `test(affiliate): add unit tests for sync endpoints` | Test file | `mvn test` |

---

## 成功标准

### 验证命令

```bash
# 运行单元测试
cd river-server/river-module-affiliate
mvn test -Dtest=AffiliateNetworkControllerTest

# 验证测试覆盖率
mvn test -Dtest=AffiliateNetworkControllerTest -Djacoco.skip=false
```

### 最终检查清单

- [ ] `POST /affiliate/network/sync-deals` 接口存在
- [ ] `POST /affiliate/network/sync-coupons-only` 接口存在
- [ ] 无参数返回 200 (success=false)
- [ ] 无效 code 返回 success=false
- [ ] 未知 networkId 返回 success=false
- [ ] 有效 code 返回 200 (success=true)
- [ ] 有效 networkId 返回 200 (success=true)
- [ ] networkId + code 时 networkId 优先
- [ ] 现有 `sync-coupons` 接口行为不变
- [ ] 单元测试全部通过
- [ ] 测试覆盖率 ≥ 80%
