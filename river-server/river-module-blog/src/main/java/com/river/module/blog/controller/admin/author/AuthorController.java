package com.river.module.blog.controller.admin.author;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.author.vo.AuthorPageReqVO;
import com.river.module.blog.controller.admin.author.vo.AuthorRespVO;
import com.river.module.blog.controller.admin.author.vo.AuthorSaveReqVO;
import com.river.framework.common.util.object.BeanUtils;
import com.river.module.blog.dal.dataobject.AuthorDO;
import com.river.module.blog.service.AuthorService;
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

@Tag(name = "管理后台 - 博客作者")
@RestController
@RequestMapping("/blog/author")
@Validated
public class AuthorController {

    @Resource
    private AuthorService authorService;

    @PostMapping("/create")
    @Operation(summary = "创建作者")
    @PreAuthorize("@ss.hasPermission('blog:author:create')")
    public CommonResult<Long> createAuthor(@Valid @RequestBody AuthorSaveReqVO createReqVO) {
        return success(authorService.createAuthor(BeanUtils.toBean(createReqVO, AuthorDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新作者")
    @PreAuthorize("@ss.hasPermission('blog:author:update')")
    public CommonResult<Boolean> updateAuthor(@Valid @RequestBody AuthorSaveReqVO updateReqVO) {
        authorService.updateAuthor(BeanUtils.toBean(updateReqVO, AuthorDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除作者")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('blog:author:delete')")
    public CommonResult<Boolean> deleteAuthor(@RequestParam("id") Long id) {
        authorService.deleteAuthor(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取作者")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('blog:author:query')")
    public CommonResult<AuthorRespVO> getAuthor(@RequestParam("id") Long id) {
        AuthorDO author = authorService.getAuthor(id);
        return success(BeanUtils.toBean(author, AuthorRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取作者列表")
    @PreAuthorize("@ss.hasPermission('blog:author:query')")
    public CommonResult<List<AuthorRespVO>> getAuthorList() {
        List<AuthorDO> list = authorService.getAuthorList();
        return success(BeanUtils.toBean(list, AuthorRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取作者分页")
    @PreAuthorize("@ss.hasPermission('blog:author:query')")
    public CommonResult<PageResult<AuthorRespVO>> getAuthorPage(@Valid AuthorPageReqVO pageReqVO) {
        PageResult<AuthorDO> pageResult = authorService.getAuthorPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AuthorRespVO.class));
    }
}
