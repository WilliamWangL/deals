package com.river.module.affiliate.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.river.framework.common.enums.CommonStatusEnum;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.affiliate.controller.admin.category.vo.CategoryListReqVO;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import com.river.module.affiliate.dal.mysql.CategoryMapper;
import com.river.module.coupon.dal.dataobject.DealDO;
import com.river.module.coupon.dal.mysql.DealMapper;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.dal.mysql.CouponMapper;
import com.river.module.coupon.enums.CouponStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private DealMapper dealMapper;

    @Resource
    private CouponMapper couponMapper;

    @Override
    public Long createCategory(CategoryDO category) {
        validateParentCategory(category.getParentId());
        validateCategorySlugUnique(null, category.getSlug());
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateCategory(CategoryDO category) {
        validateCategoryExists(category.getId());
        validateParentCategory(category.getParentId());
        validateCategorySlugUnique(category.getId(), category.getSlug());
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        validateCategoryExists(id);
        if (CollUtil.isNotEmpty(getCategoryListByParentId(id))) {
            throw exception(CATEGORY_EXISTS_CHILDREN);
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public CategoryDO getCategory(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<CategoryDO> getCategoryList() {
        return categoryMapper.selectList();
    }

    @Override
    public List<CategoryDO> getCategoryList(CategoryListReqVO listReqVO) {
        return categoryMapper.selectList(listReqVO);
    }

    @Override
    public List<CategoryDO> getCategoryListByParentId(Long parentId) {
        return categoryMapper.selectList(CategoryDO::getParentId, parentId);
    }

    @Override
    public List<CategoryDO> getCategoryListByParentId(Long parentId, List<String> regions) {
        if (CollUtil.isEmpty(regions)) {
            return getCategoryListByParentId(parentId);
        }
        Set<Long> categoryIdsWithData = getCategoryIdsWithDataIncludingAncestors(regions);
        return categoryMapper.selectList(CategoryDO::getParentId, parentId).stream()
                .filter(cat -> categoryIdsWithData.contains(cat.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void validateCategoryExists(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
    }

    private void validateParentCategory(Long parentId) {
        if (parentId == null || Objects.equals(parentId, 0L)) {
            return;
        }
        if (categoryMapper.selectById(parentId) == null) {
            throw exception(CATEGORY_PARENT_NOT_EXISTS);
        }
    }

    private void validateCategorySlugUnique(Long id, String slug) {
        if (StrUtil.isBlank(slug)) {
            return;
        }
        CategoryDO category = categoryMapper.selectOne(CategoryDO::getSlug, slug);
        if (category == null) {
            return;
        }
        if (id == null || !category.getId().equals(id)) {
            throw exception(CATEGORY_SLUG_DUPLICATE);
        }
    }

    @Override
    public List<CategoryDO> getCategoryTree() {
        return categoryMapper.selectList();
    }

    @Override
    public List<CategoryDO> getCategoryTree(List<String> regions) {
        if (CollUtil.isEmpty(regions)) {
            return getCategoryTree();
        }
        Set<Long> categoryIdsWithData = getCategoryIdsWithDataIncludingAncestors(regions);
        return categoryMapper.selectList().stream()
                .filter(cat -> categoryIdsWithData.contains(cat.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDO getCategoryBySlug(String slug) {
        return categoryMapper.selectOne(CategoryDO::getSlug, slug);
    }

    @Override
    public CategoryDO getCategoryBySlug(String slug, List<String> regions) {
        CategoryDO category = getCategoryBySlug(slug);
        if (category == null || CollUtil.isEmpty(regions)) {
            return category;
        }
        Set<Long> categoryIdsWithData = getCategoryIdsWithDataIncludingAncestors(regions);
        return categoryIdsWithData.contains(category.getId()) ? category : null;
    }

    @Override
    public List<CategoryDO> getCategoryAncestors(Long categoryId) {
        List<CategoryDO> ancestors = new ArrayList<>();
        CategoryDO current = categoryMapper.selectById(categoryId);
        while (current != null && !Objects.equals(current.getParentId(), 0L)) {
            current = categoryMapper.selectById(current.getParentId());
            if (current != null) {
                ancestors.add(0, current);
            }
        }
        return ancestors;
    }

    /**
     * 获取有数据的分类 ID 集合（包含祖先分类）
     * 若子分类有数据，其所有祖先分类都会被包含
     *
     * @param regions 地区列表，空则查询所有地区
     * @return 有数据的分类 ID 集合（含祖先）
     */
    private Set<Long> getCategoryIdsWithDataIncludingAncestors(List<String> regions) {
        // 1. 获取所有有效的 Deals（未过期 + 启用 + 地区匹配）
        LambdaQueryWrapperX<DealDO> dealWrapper = new LambdaQueryWrapperX<DealDO>()
                .eq(DealDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
        dealWrapper.and(w -> w.isNull(DealDO::getEndTime).or().gt(DealDO::getEndTime, LocalDateTime.now()));
        if (CollUtil.isNotEmpty(regions)) {
            dealWrapper.and(w -> {
                w.and(sub -> sub.isNull(DealDO::getRegions).or().apply("string_to_array(regions, ',') @> ARRAY['00']::text[]"));
                for (String region : regions) {
                    w.or().apply("string_to_array(regions, ',') @> ARRAY[{0}]::text[]", region);
                }
            });
        }
        List<DealDO> validDeals = dealMapper.selectList(dealWrapper);

        // 2. 获取所有有效的 Coupons（未过期 + 激活 + 地区匹配）
        LambdaQueryWrapperX<CouponDO> couponWrapper = new LambdaQueryWrapperX<CouponDO>()
                .eq(CouponDO::getStatus, CouponStatusEnum.ACTIVE.getCode());
        couponWrapper.and(w -> w.isNull(CouponDO::getEndTime).or().gt(CouponDO::getEndTime, LocalDateTime.now()));
        if (CollUtil.isNotEmpty(regions)) {
            couponWrapper.and(w -> {
                w.and(sub -> sub.isNull(CouponDO::getRegions).or().apply("string_to_array(regions, ',') @> ARRAY['00']::text[]"));
                for (String region : regions) {
                    w.or().apply("string_to_array(regions, ',') @> ARRAY[{0}]::text[]", region);
                }
            });
        }
        List<CouponDO> validCoupons = couponMapper.selectList(couponWrapper);

        // 3. 解析 category_ids 字段，获取所有有数据的分类 ID
        Set<Long> dataCategoryIds = new HashSet<>();
        for (DealDO deal : validDeals) {
            parseAndAddCategoryIds(dataCategoryIds, deal.getCategoryIds());
        }
        for (CouponDO coupon : validCoupons) {
            parseAndAddCategoryIds(dataCategoryIds, coupon.getCategoryIds());
        }

        if (CollUtil.isEmpty(dataCategoryIds)) {
            return Collections.emptySet();
        }

        // 4. 添加所有祖先分类 ID
        Set<Long> result = new HashSet<>(dataCategoryIds);
        for (Long categoryId : dataCategoryIds) {
            addAncestorIds(result, categoryId);
        }
        return result;
    }

    /**
     * 解析逗号分隔的 category_ids 字符串，并添加到集合中
     */
    private void parseAndAddCategoryIds(Set<Long> categoryIds, String categoryIdsStr) {
        if (StrUtil.isBlank(categoryIdsStr)) {
            return;
        }
        String[] ids = categoryIdsStr.split(",");
        for (String id : ids) {
            String trimmed = id.trim();
            if (StrUtil.isNotBlank(trimmed)) {
                try {
                    categoryIds.add(Long.parseLong(trimmed));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private void addAncestorIds(Set<Long> result, Long categoryId) {
        CategoryDO current = categoryMapper.selectById(categoryId);
        while (current != null && !Objects.equals(current.getParentId(), 0L)) {
            Long parentId = current.getParentId();
            if (parentId != null && parentId != 0L) {
                result.add(parentId);
                current = categoryMapper.selectById(parentId);
            } else {
                break;
            }
        }
    }

}
