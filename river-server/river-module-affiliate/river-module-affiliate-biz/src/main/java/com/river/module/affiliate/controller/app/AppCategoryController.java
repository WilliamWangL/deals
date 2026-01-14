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
        List<CategoryDO> children = categoryService.getCategoryListByParentId(category.getId());
        vo.setChildren(children.stream().map(this::convertToAppVO).collect(Collectors.toList()));
        return success(vo);
    }

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
