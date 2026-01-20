package com.river.module.blog.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.controller.app.vo.AppPostPageReqVO;
import com.river.module.blog.controller.app.vo.AppPostRespVO;
import com.river.module.blog.dal.dataobject.AuthorDO;
import com.river.module.blog.dal.dataobject.BlogPostDO;
import com.river.module.blog.enums.PostStatusEnum;
import com.river.module.blog.service.AuthorService;
import com.river.module.blog.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 博客文章")
@RestController
@RequestMapping("/blog/post")
@Validated
@PermitAll
public class AppPostController {

    @Resource
    private BlogPostService blogPostService;

    @Resource
    private AuthorService authorService;

    @GetMapping("/list")
    @Operation(summary = "获取已发布文章列表")
    public CommonResult<List<AppPostRespVO>> getPublishedPostList(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "featured", required = false) Boolean featured) {
        List<BlogPostDO> list = blogPostService.getPostList();
        List<AppPostRespVO> result = list.stream()
                .filter(p -> PostStatusEnum.PUBLISHED.getCode().equals(p.getStatus()))
                .filter(p -> type == null || p.getType().equals(type))
                .filter(p -> featured == null || p.getFeatured().equals(featured))
                .map(this::convertToAppVO)
                .toList();
        return success(result);
    }

    @GetMapping("/page")
    @Operation(summary = "获取已发布文章分页")
    public CommonResult<PageResult<AppPostRespVO>> getPostPage(@Valid AppPostPageReqVO pageReqVO) {
        PostPageReqVO adminPageReqVO = new PostPageReqVO();
        adminPageReqVO.setPageNo(pageReqVO.getPageNo());
        adminPageReqVO.setPageSize(pageReqVO.getPageSize());
        adminPageReqVO.setType(pageReqVO.getType());
        adminPageReqVO.setFeatured(pageReqVO.getFeatured());
        adminPageReqVO.setStatus(PostStatusEnum.PUBLISHED.getCode());
        PageResult<BlogPostDO> pageResult = blogPostService.getPostPage(adminPageReqVO);

        List<AppPostRespVO> result = pageResult.getList().stream()
                .map(this::convertToAppVO)
                .toList();
        return success(new PageResult<>(result, pageResult.getTotal()));
    }

    @GetMapping("/get-by-slug")
    @Operation(summary = "根据 slug 获取文章详情")
    @Parameter(name = "slug", description = "文章 slug", required = true, example = "best-deals-2024")
    public CommonResult<AppPostRespVO> getPostBySlug(@RequestParam("slug") String slug) {
       BlogPostDO post = blogPostService.getPostBySlug(slug);
        if (post == null || !PostStatusEnum.PUBLISHED.getCode().equals(post.getStatus())) {
            return success(null);
        }
        return success(convertToAppVO(post));
    }

    private AppPostRespVO convertToAppVO(BlogPostDO post) {
        if (post == null) {
            return null;
        }
        AppPostRespVO vo = new AppPostRespVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setSlug(post.getSlug());
        vo.setContent(post.getContent());
        vo.setExcerpt(post.getExcerpt());
        vo.setCoverImage(post.getCoverImage());
        vo.setType(post.getType());
        vo.setPublishedAt(post.getPublishedAt());
        vo.setViewCount(post.getViewCount());
        vo.setFeatured(post.getFeatured());
        vo.setMetaTitle(post.getMetaTitle());
        vo.setMetaDescription(post.getMetaDescription());

        if (post.getAuthorId() != null) {
            AuthorDO author = authorService.getAuthor(post.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getName());
                vo.setAuthorAvatar(author.getAvatarUrl());
            }
        }
        return vo;
    }

}
