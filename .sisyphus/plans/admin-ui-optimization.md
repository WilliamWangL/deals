# River Admin-UI 优化计划

## Context

### Original Request
用户要求优化 admin-ui 部分和后端的交互展示，补足功能。

### Interview Summary

**Key Discussions**:
- 用户强调：注意基础框架已经提供了很多功能，**不要重复造轮子**
- 优先优化方向：关联数据展示 + 数据可视化增强
- 所有模块都需要优化：Affiliate、Campaign、Tracking、Coupon、Stats、Blog

**User Decisions**:
- 饼图：DailyStatsDO 没有 conversionType 字段，暂时不做
- 多维筛选：仅扩展分页接口，不扩展 summary/trend API

**Research Findings**:
- 项目基于 Yudao UI Admin (vue-element-plus-admin) 构建
- 框架已提供高价值组件：Table、Form、Echart、Descriptions、Pagination
- River 业务模块 CRUD 已实现，需要增强交互和数据展示
- 后端 DailyStatsDO 只有 dimensionType/dimensionId，没有 conversionType
- 后端只有 DailyStatsPageReqVO，没有 DailyStatsQueryVO

### Metis Review

**Identified Gaps** (addressed in plan):
- 需要明确每个模块的具体页面范围
- 需要明确关联数据的字段和 API 来源
- 需要设定性能约束（分页限制、图表数据量）
- 避免范围蔓延到新页面

---

## Work Objectives

### Core Objective
在现有 CRUD 基础上，**增强关联数据展示**和**数据可视化**，提升用户体验，不重复造轮子。

### Concrete Deliverables
1. **Tracking 模块增强**
   - 转化记录增加点击详情关联跳转
   - 列表 ID 可点击跳转到对应详情弹窗
   
2. **关联信息展示**
   - 点击记录关联 Offer/Campaign 名称
   - Merchant 名称无法显示（ClickVO 无 merchantId 字段）
   
3. **Stats 页面增强**
   - 更多指标（CPA、Conversion Rate、ROI）
   - 多维度筛选功能（仅分页接口）

### Definition of Done
- [ ] 转化记录的 clickId 可点击，跳转到点击详情弹窗
- [ ] 列表中的 ID 字段可点击，显示详情弹窗
- [ ] 关联信息在详情弹窗中展示（Offer/Campaign，Merchant 显示 N/A）
- [ ] Stats 页面增加更多统计指标
- [ ] Stats 页面支持多维度筛选（分页接口）

### Must Have
- 复用框架组件（el-descriptions、el-table-column + el-link）
- 不新增页面，只优化现有页面
- 扩展后端分页 API 添加筛选参数

### Must NOT Have (Guardrails)
- 不新增业务模块页面
- 不新增后端统计 API（无 conversionType 字段）
- 不新增组件库（复用框架已有）
- 不添加动画/转场效果（纯功能增强）

**例外说明**（已获用户确认）：
- Stats 页面现有分页 API 新增 `offerId`、`campaignId`、`trafficSourceId` 筛选参数

---

## Verification Strategy (MANDATORY)

### Test Decision
- **Infrastructure exists**: YES (Vitest)
- **User wants tests**: Tests after implementation

### Implementation Verification (Manual + Tests)

Each task includes:

1. **Frontend Build Verification**:
   ```bash
   cd river-ui-admin && pnpm build:local
   # Expected: Build success, no errors
   ```

2. **Manual UI Verification**:
   - Navigate to each modified page
   - Verify new links/buttons render correctly
   - Verify dialogs open with correct data

3. **Test Files to Add**:
   - `src/views/river/**/*.spec.ts` for component tests

---

## Task Flow

```
Task 1: Fix Tracking Click Offer Loading
  └─ Task 2: Add ID Clickable Links (All Modules)
      ├─ Task 2a: Tracking Module
      ├─ Task 2b: Campaign Module
      ├─ Task 2c: Affiliate Module
      ├─ Task 2d: Coupon Module
      └─ Task 2e: Blog Module
          └─ Task 3: Add Conversion→Click Jump
              └─ Task 4: Add Related Info in Details
                  └─ Task 5: Stats More Metrics
                      └─ Task 6: Stats Multi-dimension Filter (Page Only)
```

## Parallelization

| Group | Tasks | Reason |
|-------|-------|--------|
| A | 2a, 2b, 2c, 2d, 2e | Similar pattern, different files |

| Task | Depends On | Reason |
|------|------------|--------|
| 2 | 1 | Fix loading first |
| 3 | 2 | Need ID links first |
| 4 | 3 | Need jump first |
| 5 | 4 | Related info first |
| 6 | 5 | Same page, sequential |

---

## TODOs

- [ ] 1. Fix Tracking Click Offer Loading (TODO in getOfferList)

  **What to do**:
  - Implement `getOfferList()` in `river-ui-admin/src/views/river/tracking/click/index.vue`
  - Call `OfferApi.getOfferList()` to get offer options for select dropdown
  
  **Must NOT do**:
  - Don't modify backend API

  **Parallelizable**: NO (depends on existing structure)

  **References**:
  - `river-ui-admin/src/views/river/tracking/click/index.vue` - Line ~76 has TODO comment
  - `river-ui-admin/src/api/river/affiliate/index.ts:OfferApi.getOfferList` - CONFIRMED EXISTS

  **Acceptance Criteria**:
  - [ ] `getOfferList()` implemented without errors
  - [ ] Offer select dropdown in search form populated with data
  - [ ] `pnpm build:local` succeeds

  **Manual Execution Verification**:
  - Navigate to: `http://localhost:3100/river/tracking/click`
  - Find "Offer" dropdown in search form
  - Verify dropdown has options populated

- [ ] 2. Add ID Clickable Links (All Modules)

  **What to do**:
  - Modify table columns to make ID fields clickable using `el-link` pattern
  - **Click behavior**: Open NEW inline detail dialog (NOT reusing form/edit)
  - Each module adds its OWN inline detail dialog for read-only display

  **Implementation for each module**:
  1. Add clickable el-link on ID column
  2. Add state: `const detailVisible = ref(false)` and `const currentDetail = ref({})`
  3. Add inline `<el-dialog>` for detail (copy structure from click/index.vue)
  4. Add `handleDetail(row)` function

  **Must NOT do**:
  - Don't modify data structure
  - Don't add new API calls
  - Don't use existing form/edit dialogs (add new inline detail dialogs only)

  **Parallelizable**: YES (with 2a, 2b, 2c, 2d, 2e)

  **References**:
  - `river-ui-admin/src/views/river/tracking/click/index.vue:handleView pattern` - Base pattern to copy
  - `river-ui-admin/src/views/river/tracking/click/index.vue:115-152` - Inline detail dialog structure

  **Acceptance Criteria**:
  - [ ] Table ID columns have clickable el-link style
  - [ ] Clicking ID opens NEW inline detail dialog
  - [ ] Dialog shows read-only information for that entity

  **Template Pattern**:
  ```html
  <el-table-column label="ID" prop="id" width="80">
    <template #default="scope">
      <el-link :underline="false" type="primary" @click="handleDetail(scope.row)">
        {{ scope.row.id }}
      </el-link>
    </template>
  </el-table-column>
  ```

  **Script Pattern**:
  ```typescript
  const detailVisible = ref(false)
  const currentDetail = ref({})
  const handleDetail = (row: any) => {
    currentDetail.value = row
    detailVisible.value = true
  }
  ```

  **NEW Dialog Template** (add after table):
  ```html
  <el-dialog v-model="detailVisible" title="详情" width="600px">
    <el-descriptions :column="1" border>
      <!-- Add descriptions for entity fields -->
    </el-descriptions>
  </el-dialog>
  ```

---

  #### 2a. Tracking Module ID Links

  **What to do**:
  - Add clickable link to Click ID → Click Detail Dialog
  - Use same pattern as Task 2 main description
  
  **References**:
  - `river-ui-admin/src/views/river/tracking/click/index.vue` - Main table (lines ~62-65)

  **Acceptance Criteria**:
  - [ ] Click record ID column has clickable el-link
  - [ ] Clicking opens detail dialog with full info

  #### 2b. Campaign Module ID Links

  **What to do**:
  - Add clickable link to:
    - Campaign ID → Campaign Detail Dialog
    - Traffic Source ID → Traffic Source Detail Dialog
  
  **References**:
  - `river-ui-admin/src/views/river/campaign/campaign/index.vue`
  - `river-ui-admin/src/views/river/campaign/traffic-source/index.vue`

  **Acceptance Criteria**:
  - [ ] Campaign ID column clickable
  - [ ] Traffic Source ID column clickable

  #### 2c. Affiliate Module ID Links

  **What to do**:
  - Add clickable link to:
    - Merchant ID → Merchant Detail Dialog
    - Offer ID → Offer Detail Dialog
    - Category ID → Category Detail Dialog
  
  **References**:
  - `river-ui-admin/src/views/river/affiliate/merchant/index.vue`
  - `river-ui-admin/src/views/river/affiliate/offer/index.vue`
  - `river-ui-admin/src/views/river/affiliate/category/index.vue`

  **Acceptance Criteria**:
  - [ ] Merchant ID clickable
  - [ ] Offer ID clickable
  - [ ] Category ID clickable

  #### 2d. Coupon Module ID Links

  **What to do**:
  - Add clickable link to Coupon ID → Coupon Detail Dialog
  
  **References**:
  - `river-ui-admin/src/views/river/coupon/coupon/index.vue`

  **Acceptance Criteria**:
  - [ ] Coupon ID column clickable

  #### 2e. Blog Module ID Links

  **What to do**:
  - Add clickable link to:
    - Post ID → Post Detail Dialog
    - Author ID → Author Detail Dialog
    - Tag ID → Tag Detail Dialog
  
  **References**:
  - `river-ui-admin/src/views/river/blog/post/index.vue`
  - `river-ui-admin/src/views/river/blog/author/index.vue`
  - `river-ui-admin/src/views/river/blog/tag/index.vue`

  **Acceptance Criteria**:
  - [ ] Post ID clickable
  - [ ] Author ID clickable
  - [ ] Tag ID clickable

- [ ] 3. Add Conversion→Click Jump

  **What to do**:
  - In Conversion list, add clickable link on ClickId column
  - Clicking opens NEW Click Detail inline dialog with full click information
  - Use `ClickApi.getClick(clickId)` to fetch click details
  
  **Implementation Location**:
  - Modify `river-ui-admin/src/views/river/tracking/conversion/index.vue`:
    - Add NEW state: `const clickDetailVisible = ref(false)` (NEW name to avoid conflict)
    - Add NEW state: `const currentClickDetail = ref({})` (NEW name)
    - Add NEW inline `<el-dialog>` for click detail (after existing conversion detail dialog)
    - Add `handleClickDetail(clickId)` function using `ClickApi.getClick`

  **Must NOT do**:
  - Don't conflict with existing `detailVisible`/`currentDetail` (conversion detail)

  **References**:
  - `river-ui-admin/src/views/river/tracking/conversion/index.vue` - ClickId column (lines 97-104)
  - `river-ui-admin/src/views/river/tracking/click/index.vue:115-152` - Click detail dialog pattern
  - `river-ui-admin/src/api/river/tracking/index.ts:ClickApi.getClick` - CONFIRMED EXISTS

  **Acceptance Criteria**:
  - [ ] Conversion list ClickId column has clickable el-link
  - [ ] Clicking opens NEW click detail dialog (separate from conversion detail)
  - [ ] Dialog shows full click information

  **Implementation Pattern**:
  ```typescript
  // NEW states (avoid conflict with existing detailVisible/currentDetail)
  const clickDetailVisible = ref(false)
  const currentClickDetail = ref({})
  
  // NEW handler
  const handleClickDetail = async (clickId: string) => {
    const data = await ClickApi.getClick(clickId)
    currentClickDetail.value = data
    clickDetailVisible.value = true
  }
  ```

- [ ] 4. Add Related Info in Details

  **What to do**:
  - In Click Detail dialog (inline in click/index.vue), add associated information:
    - **Offer Name**: Display offer name from `offerList` cache (already have `getOfferName` function)
    - **Campaign Name**: Create helper function to lookup campaign name from `campaignList` cache
    - **Merchant Name**: NOT AVAILABLE - ClickVO does NOT have `merchantId` field
  
  **Implementation**:
  - Add `campaignList` ref loaded on mount
  - Create `getCampaignName(id)` helper similar to existing `getOfferName`
  - Add Campaign Name to detail dialog

  **Must NOT do**:
  - Don't add new API calls per-row (use cached lists)
  - Don't add merchantId field (not in ClickVO)

  **References**:
  - `river-ui-admin/src/views/river/tracking/click/index.vue` - Inline detail dialog (lines 115-152)
  - `river-ui-admin/src/views/river/tracking/click/index.vue:180-183` - Existing getOfferName function
  - `river-ui-admin/src/api/river/campaign/index.ts:CampaignApi.getCampaignPage` - For campaign list
  - `river-ui-admin/src/api/river/tracking/index.ts:ClickVO` - Confirmed NO merchantId field

  **Acceptance Criteria**:
  - [ ] Click Detail shows Offer Name (already working via getOfferName)
  - [ ] Click Detail shows Campaign Name (new lookup)
  - [ ] Click Detail shows "N/A" for Merchant Name (field not available in API)

  **Note**: Merchant Name cannot be shown because ClickVO does not contain `merchantId` field.
  To show merchant, backend would need to return merchantId or join with offer table.

- [ ] 5. Stats More Metrics

  **What to do**:
  - Add more KPI cards to Stats page using available API fields. Formulas:
    - **CPA** = `totalCost / totalConversions` (handle div by zero)
    - **Conversion Rate** = `totalConversions / totalClicks * 100`
    - **ROI** = `totalProfit / totalCost * 100`
    - **CTR**: NOT AVAILABLE - `DailyStatsSummaryRespVO` has NO `totalImpressions` field
  
  **API Fields Available**:
  - `totalClicks`, `totalConversions`, `totalRevenue`, `totalCost`, `totalProfit`
  - Pre-calculated: `avgEpc`, `avgCr`, `avgRoi`
  
  **Implementation**:
  - Add new KPI cards for CPA, Conversion Rate, ROI
  - CTR cannot be implemented (impressions field not in API)

  **Must NOT do**:
  - Don't modify backend (use available fields only)

  **References**:
  - `river-ui-admin/src/api/river/stats/index.ts:52-61` - DailyStatsSummaryRespVO fields
  - `river-ui-admin/src/views/river/stats/index.vue` - KPI card structure

  **Acceptance Criteria**:
  - [ ] CPA displayed in new KPI card (formatted as currency)
  - [ ] Conversion Rate displayed (formatted as percentage)
  - [ ] ROI displayed (formatted as percentage)
  - [ ] CTR shown as "N/A" (field not available in API)

- [ ] 6. Stats Multi-dimension Filter (Page Only, Add New Fields)

  **What to do**:
  - Add new filter dropdowns ABOVE existing dimension filter:
    - **Offer filter**: Use `OfferApi.getOfferList()`
    - **Campaign filter**: Use `getCampaignList()` wrapper
    - **Traffic Source filter**: Use `TrafficSourceApi.getTrafficSourceList()`
  
  **Relationship with existing dimension filter**:
  - **NEW filters are ADDITIONAL to** existing `dimensionType` + `dimensionId`
  - All filters are combined (AND logic)
  
  **Implementation Strategy**:
  - Add new fields to DailyStatsDO and database: `offerId`, `campaignId`, `trafficSourceId`
  - These fields store the original dimension's source IDs for direct filtering
  
  **Backend API Extension (Page API Only)**:
  - Extend `POST /stats/daily/page` to accept new params: `offerId`, `campaignId`, `trafficSourceId`
  - Backend filters daily_stats table by these new dimensions
  
  **Implementation**:
  - Add `offerId`, `campaignId`, `trafficSourceId` to frontend `DailyStatsPageReqVO`
  - Add `offerId`, `campaignId`, `trafficSourceId` to backend `DailyStatsPageReqVO.java`
  - Add `offerId`, `campaignId`, `trafficSourceId` to `DailyStatsDO.java`
  - Add columns to database table `river_stats_daily`
  - Add WHERE clause in `DailyStatsMapper.java` / `DailyStatsServiceImpl.java`
  - Load lists on mount (same as dimensionOptions loading pattern)
  - Pass selected filter values to `DailyStatsApi.getPage()` only

  **Must NOT do**:
  - Don't remove existing dimensionType/dimensionId filters
  - Don't extend summary/trend APIs (only page API)

  **References**:
  - `river-ui-admin/src/views/river/stats/index.vue` - Search form location
  - `river-ui-admin/src/views/river/stats/index.vue:303-335` - handleDimensionTypeChange pattern
  - `river-ui-admin/src/api/river/stats/index.ts:16-23` - DailyStatsPageReqVO (ADD FIELDS HERE)
  - `river-ui-admin/src/api/river/stats/index.ts:26-31` - DailyStatsQueryVO (NOTE: this interface EXISTS in frontend, used by getSummary/getTrend/exportExcel)
  - `river-ui-admin/src/api/river/affiliate/index.ts:OfferApi.getOfferList`
  - `river-ui-admin/src/api/river/campaign/index.ts:TrafficSourceApi.getTrafficSourceList`

  **Frontend Changes**:
  - `river-ui-admin/src/api/river/stats/index.ts:16-23` - Add `offerId`, `campaignId`, `trafficSourceId` to DailyStatsPageReqVO
  - `river-ui-admin/src/views/river/stats/index.vue` - Add 3 filter dropdowns and pass to buildQueryParams

  **Backend Changes Required**:
  - `river-server: DailyStatsPageReqVO.java` - Add `offerId`, `campaignId`, `trafficSourceId` fields
  - `river-server: DailyStatsDO.java` - Add `offerId`, `campaignId`, `trafficSourceId` fields (Long type)
  - `river-server: database` - ALTER TABLE river_stats_daily ADD COLUMN offer_id BIGINT, campaign_id BIGINT, traffic_source_id BIGINT
  - `river-server: DailyStatsController.java` - Ensure params passed through
  - `river-server: DailyStatsServiceImpl.java` - Add WHERE clause for new filters (LambdaQueryWrapperX)
  - `river-server: DailyStatsMapper.java` - Ensure new fields are queryable

  **Acceptance Criteria**:
  - [ ] NEW Offer/Campaign/Traffic Source dropdowns added ABOVE dimension filter
  - [ ] All three dropdowns populated with data
  - [ ] Selecting filters sends params to getPage() API only
  - [ ] Backend API returns filtered data
  - [ ] DailyStatsDO has new fields and database table updated

---

## Commit Strategy

| After Task | Message | Files | Verification |
|------------|---------|-------|--------------|
| 1 | `fix(tracking): implement getOfferList in click page` | `src/views/river/tracking/click/index.vue` | Build pass |
| 2a-2e | `feat(ui): add clickable ID links to tables` | `src/views/river/**/index.vue` | Build pass + UI check |
| 3 | `feat(tracking): add conversion to click jump` | `src/views/river/tracking/conversion/index.vue` | Build pass + UI check |
| 4 | `feat(ui): show related info in detail dialogs` | `src/views/river/tracking/click/index.vue` | Build pass + UI check |
| 5 | `feat(stats): add CPA, Conversion Rate, ROI metrics` | `src/views/river/stats/index.vue` | Build pass + UI check |
| 6 | `feat(stats): add multi-dimension filters` | `src/views/river/stats/index.vue` | Build pass + UI check |

---

## Success Criteria

### Verification Commands
```bash
cd river-ui-admin
pnpm build:local  # Expected: Build success
pnpm ts:check     # Expected: No type errors
```

### Final Checklist
- [ ] All "Must Have" present (6 tasks completed)
- [ ] All "Must NOT Have" absent (no new pages)
- [ ] All tables have clickable ID links
- [ ] Stats page has more metrics
- [ ] Stats page has multi-dimension filters (page only)
- [ ] Build succeeds without errors
