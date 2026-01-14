# Backend API Completion Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Complete river-server App API by adding AppCategoryController and fixing Merchant statistics.

**Architecture:** Add new App endpoints following existing patterns (AppDealController, AppMerchantController). Use manual VO conversion (not MapStruct) consistent with other App controllers. Implement recursive tree building for categories.

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, Lombok, Swagger/OpenAPI

**Worktree:** `/Users/apple/Projects/shixiaohe/river-ad-workspace/.worktrees/backend-api-completion`

---

## Task 1: Create AppCategoryRespVO

**Files:**
- Create: `river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/controller/app/vo/AppCategoryRespVO.java`

**Step 1: Create the VO class**

```java
package com.river.module.affiliate.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "用户 App - 分类 Response VO")
public class AppCategoryRespVO {

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "Electronics")
    private String name;

    @Schema(description = "分类标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "electronics")
    private String slug;

    @Schema(description = "分类图标", example = "laptop")
    private String icon;

    @Schema(description = "层级", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer level;

    @Schema(description = "父分类编号", example = "0")
    private Long parentId;

    @Schema(description = "子分类列表")
    private List<AppCategoryRespVO> children;

}
```

**Step 2: Verify compilation**

Run: `cd river-server && mvn compile -pl river-module-affiliate/river-module-affiliate-biz -am -q`
Expected: BUILD SUCCESS (no output means success)

**Step 3: Commit**

```bash
git add river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/controller/app/vo/AppCategoryRespVO.java
git commit -m "feat(affiliate): add AppCategoryRespVO for app category API"
```

---

## Task 2: Add CategoryService tree methods

**Files:**
- Modify: `river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/service/CategoryService.java`
- Modify: `river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/service/CategoryServiceImpl.java`

**Step 1: Add interface methods to CategoryService.java**

Add after existing methods:

```java
    /**
     * 获取分类树
     *
     * @return 分类树列表
     */
    List<CategoryDO> getCategoryTree();

    /**
     * 根据 slug 获取分类
     *
     * @param slug 分类标识
     * @return 分类
     */
    CategoryDO getCategoryBySlug(String slug);

    /**
     * 获取分类的祖先链路（面包屑）
     *
     * @param categoryId 分类编号
     * @return 祖先分类列表（从根到当前）
     */
    List<CategoryDO> getCategoryAncestors(Long categoryId);
```

**Step 2: Implement methods in CategoryServiceImpl.java**

Add after existing methods:

```java
    @Override
    public List<CategoryDO> getCategoryTree() {
        // 获取所有启用的分类
        List<CategoryDO> allCategories = categoryMapper.selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByAsc(CategoryDO::getSort));
        return allCategories;
    }

    @Override
    public CategoryDO getCategoryBySlug(String slug) {
        return categoryMapper.selectOne(new LambdaQueryWrapperX<CategoryDO>()
                .eq(CategoryDO::getSlug, slug)
                .eq(CategoryDO::getStatus, CommonStatusEnum.ENABLE.getStatus()));
    }

    @Override
    public List<CategoryDO> getCategoryAncestors(Long categoryId) {
        List<CategoryDO> ancestors = new ArrayList<>();
        CategoryDO current = categoryMapper.selectById(categoryId);
        while (current != null && current.getParentId() != 0) {
            current = categoryMapper.selectById(current.getParentId());
            if (current != null) {
                ancestors.add(0, current); // 添加到开头，保持从根到当前的顺序
            }
        }
        return ancestors;
    }
```

Add import at top:

```java
import java.util.ArrayList;
```

**Step 3: Verify compilation**

Run: `cd river-server && mvn compile -pl river-module-affiliate/river-module-affiliate-biz -am -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/service/CategoryService.java
git add river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/service/CategoryServiceImpl.java
git commit -m "feat(affiliate): add category tree and ancestor query methods"
```

---

## Task 3: Create AppCategoryController

**Files:**
- Create: `river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/controller/app/AppCategoryController.java`

**Step 1: Create the controller**

```java
package com.river.module.affiliate.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.affiliate.controller.app.vo.AppCategoryRespVO;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import com.river.module.affiliate.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 分类")
@RestController
@RequestMapping("/affiliate/category")
@Validated
public class AppCategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/tree")
    @Operation(summary = "获取分类树")
    public CommonResult<List<AppCategoryRespVO>> getCategoryTree() {
        List<CategoryDO> categories = categoryService.getCategoryTree();
        List<AppCategoryRespVO> tree = buildTree(categories);
        return success(tree);
    }

    @GetMapping("/list")
    @Operation(summary = "获取分类列表")
    @Parameter(name = "parentId", description = "父分类编号", example = "0")
    public CommonResult<List<AppCategoryRespVO>> getCategoryList(
            @RequestParam(required = false, defaultValue = "0") Long parentId) {
        List<CategoryDO> categories = categoryService.getCategoryListByParentId(parentId);
        List<AppCategoryRespVO> result = categories.stream()
                .map(this::convertToAppVO)
                .collect(Collectors.toList());
        return success(result);
    }

    @GetMapping("/get-by-slug")
    @Operation(summary = "根据 slug 获取分类详情")
    @Parameter(name = "slug", description = "分类标识", required = true, example = "electronics")
    public CommonResult<AppCategoryRespVO> getCategoryBySlug(@RequestParam String slug) {
        CategoryDO category = categoryService.getCategoryBySlug(slug);
        if (category == null) {
            return success(null);
        }
        AppCategoryRespVO vo = convertToAppVO(category);
        // 获取子分类
        List<CategoryDO> children = categoryService.getCategoryListByParentId(category.getId());
        vo.setChildren(children.stream().map(this::convertToAppVO).collect(Collectors.toList()));
        return success(vo);
    }

    /**
     * 构建分类树
     */
    private List<AppCategoryRespVO> buildTree(List<CategoryDO> categories) {
        Map<Long, List<CategoryDO>> childrenMap = categories.stream()
                .collect(Collectors.groupingBy(CategoryDO::getParentId));

        return buildTreeRecursive(0L, childrenMap);
    }

    private List<AppCategoryRespVO> buildTreeRecursive(Long parentId, Map<Long, List<CategoryDO>> childrenMap) {
        List<CategoryDO> children = childrenMap.get(parentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        return children.stream()
                .map(category -> {
                    AppCategoryRespVO vo = convertToAppVO(category);
                    vo.setChildren(buildTreeRecursive(category.getId(), childrenMap));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private AppCategoryRespVO convertToAppVO(CategoryDO category) {
        return AppCategoryRespVO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .icon(category.getIcon())
                .level(category.getLevel())
                .parentId(category.getParentId())
                .build();
    }

}
```

**Step 2: Verify compilation**

Run: `cd river-server && mvn compile -pl river-module-affiliate/river-module-affiliate-biz -am -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/controller/app/AppCategoryController.java
git commit -m "feat(affiliate): add AppCategoryController with tree/list/get-by-slug endpoints"
```

---

## Task 4: Add Mapper count methods for Deal and Coupon

**Files:**
- Modify: `river-server/river-module-coupon/river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/mysql/DealMapper.java`
- Modify: `river-server/river-module-coupon/river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/mysql/CouponMapper.java`

**Step 1: Add method to DealMapper.java**

Add after existing methods:

```java
    default Long selectCountByMerchantId(Long merchantId) {
        return selectCount(new LambdaQueryWrapperX<DealDO>()
                .eq(DealDO::getMerchantId, merchantId));
    }
```

**Step 2: Add method to CouponMapper.java**

Add after existing methods:

```java
    default Long selectCountByMerchantId(Long merchantId) {
        return selectCount(new LambdaQueryWrapperX<CouponDO>()
                .eq(CouponDO::getMerchantId, merchantId));
    }
```

**Step 3: Verify compilation**

Run: `cd river-server && mvn compile -pl river-module-coupon/river-module-coupon-biz -am -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add river-server/river-module-coupon/river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/mysql/DealMapper.java
git add river-server/river-module-coupon/river-module-coupon-biz/src/main/java/com/river/module/coupon/dal/mysql/CouponMapper.java
git commit -m "feat(coupon): add selectCountByMerchantId to DealMapper and CouponMapper"
```

---

## Task 5: Fix AppMerchantController statistics

**Files:**
- Modify: `river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/controller/app/AppMerchantController.java`

**Step 1: Add imports and inject mappers**

Add imports:

```java
import com.river.module.coupon.dal.mysql.DealMapper;
import com.river.module.coupon.dal.mysql.CouponMapper;
```

Add resource injection after existing `@Resource` fields:

```java
    @Resource
    private DealMapper dealMapper;

    @Resource
    private CouponMapper couponMapper;
```

**Step 2: Update convertToAppVO method**

Replace the existing `convertToAppVO` method:

```java
    private AppMerchantRespVO convertToAppVO(MerchantDO merchant) {
        Long dealCount = dealMapper.selectCountByMerchantId(merchant.getId());
        Long couponCount = couponMapper.selectCountByMerchantId(merchant.getId());

        return AppMerchantRespVO.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .slug(merchant.getSlug())
                .domain(merchant.getDomain())
                .logoUrl(merchant.getLogoUrl())
                .description(merchant.getDescription())
                .rating(merchant.getRating())
                .regions(merchant.getRegions())
                .dealCount(dealCount != null ? dealCount.intValue() : 0)
                .couponCount(couponCount != null ? couponCount.intValue() : 0)
                .build();
    }
```

**Step 3: Verify compilation**

Run: `cd river-server && mvn compile -pl river-module-affiliate/river-module-affiliate-biz -am -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add river-server/river-module-affiliate/river-module-affiliate-biz/src/main/java/com/river/module/affiliate/controller/app/AppMerchantController.java
git commit -m "fix(affiliate): use real deal/coupon counts in AppMerchantController"
```

---

## Task 6: Full Build Verification

**Step 1: Run full Maven build**

Run: `cd river-server && mvn compile -q`
Expected: BUILD SUCCESS

**Step 2: Run tests (if exist)**

Run: `cd river-server && mvn test -q -Dtest=*CategoryService* 2>&1 || echo "No matching tests"`
Expected: Tests pass or no matching tests

---

## Verification Checklist

- [ ] `AppCategoryRespVO.java` created with correct fields
- [ ] `CategoryService.java` has new methods: getCategoryTree, getCategoryBySlug, getCategoryAncestors
- [ ] `CategoryServiceImpl.java` implements new methods
- [ ] `AppCategoryController.java` created with /tree, /list, /get-by-slug endpoints
- [ ] `DealMapper.java` has selectCountByMerchantId method
- [ ] `CouponMapper.java` has selectCountByMerchantId method
- [ ] `AppMerchantController.java` uses real counts
- [ ] Full build passes
- [ ] All changes committed
