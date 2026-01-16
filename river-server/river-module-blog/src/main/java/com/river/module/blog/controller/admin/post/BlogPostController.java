package com.river.module.blog.controller.admin.post;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.controller.admin.post.vo.PostRespVO;
import com.river.module.blog.controller.admin.post.vo.PostSaveReqVO;
import com.river.module.blog.convert.PostConvert;
import com.river.module.blog.dal.dataobject.BlogPostDO;
import com.river.module.blog.service.BlogPostService;
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

@Tag(name = "管理后台 - 博客文章")
@RestController
@RequestMapping("/blog/post")
@Validated
public class BlogPostController {

    @Resource
    private BlogPostService blogPostService;

    @PostMapping("/create")
    @Operation(summary = "创建文章")
    @PreAuthorize("@ss.hasPermission('blog:post:create')")
    public CommonResult<Long> createPost(@Valid @RequestBody PostSaveReqVO createReqVO) {
        return success(blogPostService.createPost(PostConvert.INSTANCE.convert(createReqVO)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新文章")
    @PreAuthorize("@ss.hasPermission('blog:post:update')")
    public CommonResult<Boolean> updatePost(@Valid @RequestBody PostSaveReqVO updateReqVO) {
        blogPostService.updatePost(PostConvert.INSTANCE.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文章")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('blog:post:delete')")
    public CommonResult<Boolean> deletePost(@RequestParam("id") Long id) {
        blogPostService.deletePost(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取文章")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('blog:post:query')")
    public CommonResult<PostRespVO> getPost(@RequestParam("id") Long id) {
       BlogPostDO post = blogPostService.getPost(id);
        return success(PostConvert.INSTANCE.convert(post));
    }

    @GetMapping("/list")
    @Operation(summary = "获取文章列表")
    @PreAuthorize("@ss.hasPermission('blog:post:query')")
    public CommonResult<List<PostRespVO>> getPostList() {
        List<BlogPostDO> list = blogPostService.getPostList();
        return success(PostConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获取文章分页")
    @PreAuthorize("@ss.hasPermission('blog:post:query')")
    public CommonResult<PageResult<PostRespVO>> getPostPage(@Valid PostPageReqVO pageReqVO) {
        PageResult<BlogPostDO> pageResult = blogPostService.getPostPage(pageReqVO);
        return success(PostConvert.INSTANCE.convertPage(pageResult));
    }
}
