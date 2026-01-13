package com.river.module.affiliate.service;

import cn.hutool.core.collection.CollUtil;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import com.river.module.affiliate.dal.mysql.CategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

/**
 * 分类 Service 实现类
 */
@Service
@Validated
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Long createCategory(CategoryDO category) {
        // 校验父分类存在
        validateParentCategory(category.getParentId());
        // 校验 slug 唯一性
        validateCategorySlugUnique(null, category.getSlug());
        // 插入
        categoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateCategory(CategoryDO category) {
        // 校验存在
        validateCategoryExists(category.getId());
        // 校验父分类存在
        validateParentCategory(category.getParentId());
        // 校验 slug 唯一性
        validateCategorySlugUnique(category.getId(), category.getSlug());
        // 更新
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        // 校验存在
        validateCategoryExists(id);
        // 校验是否有子分类
        if (CollUtil.isNotEmpty(getCategoryListByParentId(id))) {
            throw exception(CATEGORY_EXISTS_CHILDREN);
        }
        // 删除
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
    public List<CategoryDO> getCategoryListByParentId(Long parentId) {
        return categoryMapper.selectList(CategoryDO::getParentId, parentId);
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
        CategoryDO category = categoryMapper.selectOne(CategoryDO::getSlug, slug);
        if (category == null) {
            return;
        }
        if (id == null || !category.getId().equals(id)) {
            throw exception(CATEGORY_SLUG_DUPLICATE);
        }
    }

}
