package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.dal.dataobject.BlogPostDO;
import jakarta.validation.Valid;

import java.util.List;

public interface BlogPostService {

    Long createPost(@Valid BlogPostDO post);

    void updatePost(@Valid BlogPostDO post);

    void deletePost(Long id);

    BlogPostDO getPost(Long id);

    List<BlogPostDO> getPostList();

    PageResult<BlogPostDO> getPostPage(PostPageReqVO pageReqVO);

    List<BlogPostDO> getPostListByAuthorId(Long authorId);

    void validatePostExists(Long id);

    BlogPostDO getPostBySlug(String slug);
}
