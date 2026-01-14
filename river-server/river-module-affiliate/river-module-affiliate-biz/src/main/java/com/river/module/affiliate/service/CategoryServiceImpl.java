package com.river.module.affiliate.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.river.module.affiliate.controller.admin.category.vo.CategoryListReqVO;
import com.river.module.affiliate.dal.dataobject.CategoryDO;
import com.river.module.affiliate.dal.mysql.CategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.affiliate.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

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
    public CategoryDO getCategoryBySlug(String slug) {
        return categoryMapper.selectOne(CategoryDO::getSlug, slug);
    }

    @Override
    public List<CategoryDO> getCategoryAncestors(Long categoryId) {
        List<CategoryDO> ancestors = new ArrayList<>();
        CategoryDO current = categoryMapper.selectById(categoryId);
        while (current != null && current.getParentId() != 0) {
            current = categoryMapper.selectById(current.getParentId());
            if (current != null) {
                ancestors.add(0, current);
            }
        }
        return ancestors;
    }

}
