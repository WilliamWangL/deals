package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.dal.dataobject.PostDO;
import jakarta.validation.Valid;

import java.util.List;

public interface PostService {

    Long createPost(@Valid PostDO post);

    void updatePost(@Valid PostDO post);

    void deletePost(Long id);

    PostDO getPost(Long id);

    List<PostDO> getPostList();

    PageResult<PostDO> getPostPage(PostPageReqVO pageReqVO);

    List<PostDO> getPostListByAuthorId(Long authorId);

    void validatePostExists(Long id);

    PostDO getPostBySlug(String slug);
}
