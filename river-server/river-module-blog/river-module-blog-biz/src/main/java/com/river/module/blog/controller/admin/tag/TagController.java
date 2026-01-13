package com.river.module.blog.controller.admin.tag;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.tag.vo.TagPageReqVO;
import com.river.module.blog.controller.admin.tag.vo.TagRespVO;
import com.river.module.blog.controller.admin.tag.vo.TagSaveReqVO;
import com.river.module.blog.convert.TagConvert;
import com.river.module.blog.dal.dataobject.TagDO;
import com.river.module.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 博客标签")
@RestController
@RequestMapping("/blog/tag")
@Validated
public class TagController {

    @Resource
    private TagService tagService;

    @PostMapping("/create")
    @Operation(summary = "创建标签")
    @PreAuthorize("@ss.hasPermission('blog:tag:create')")
    public CommonResult<Long> createTag(@Valid @RequestBody TagSaveReqVO createReqVO) {
        return success(tagService.createTag(TagConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新标签")
    @PreAuthorize("@ss.hasPermission('blog:tag:update')")
    public CommonResult<Boolean> updateTag(@Valid @RequestBody TagSaveReqVO updateReqVO) {
        tagService.updateTag(TagConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除标签")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('blog:tag:delete')")
    public CommonResult<Boolean> deleteTag(@RequestParam("id") Long id) {
        tagService.deleteTag(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取标签")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('blog:tag:query')")
    public CommonResult<TagRespVO> getTag(@RequestParam("id") Long id) {
        TagDO tag = tagService.getTag(id);
        return success(TagConvert.INSTANCE.convert(tag));
    }

    @GetMapping("/list")
    @Operation(summary = "获取标签列表")
    @PreAuthorize("@ss.hasPermission('blog:tag:query')")
    public CommonResult<List<TagRespVO>> getTagList() {
        List<TagDO> list = tagService.getTagList();
        return success(TagConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取标签分页")
    @PreAuthorize("@ss.hasPermission('blog:tag:query')")
    public CommonResult<PageResult<TagRespVO>> getTagPage(@Valid TagPageReqVO pageReqVO) {
        PageResult<TagDO> pageResult = tagService.getTagPage(pageReqVO);
        return success(TagConvert.INSTANCE.convertPage(pageResult));
    }
}
