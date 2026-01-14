package com.river.module.blog.controller.app;

import com.river.framework.common.pojo.CommonResult;
import com.river.module.blog.controller.app.vo.AppPostRespVO;
import com.river.module.blog.dal.dataobject.AuthorDO;
import com.river.module.blog.dal.dataobject.PostDO;
import com.river.module.blog.service.AuthorService;
import com.river.module.blog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.river.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 博客文章")
@RestController
@RequestMapping("/blog/post")
@Validated
public class AppPostController {

    @Resource
    private PostService postService;

    @Resource
    private AuthorService authorService;

    @GetMapping("/list")
    @Operation(summary = "获取已发布文章列表")
    public CommonResult<List<AppPostRespVO>> getPublishedPostList(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "featured", required = false) Boolean featured) {
        List<PostDO> list = postService.getPostList();
        List<AppPostRespVO> result = list.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 2)
                .filter(p -> type == null || p.getType().equals(type))
                .filter(p -> featured == null || p.getFeatured().equals(featured))
                .map(this::convertToAppVO)
                .toList();
        return success(result);
    }

    @GetMapping("/get-by-slug")
    @Operation(summary = "根据 slug 获取文章详情")
    @Parameter(name = "slug", description = "文章 slug", required = true, example = "best-deals-2024")
    public CommonResult<AppPostRespVO> getPostBySlug(@RequestParam("slug") String slug) {
        PostDO post = postService.getPostBySlug(slug);
        if (post == null || post.getStatus() != 2) {
            return success(null);
        }
        return success(convertToAppVO(post));
    }

    private AppPostRespVO convertToAppVO(PostDO post) {
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
