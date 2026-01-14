package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.dal.dataobject.PostDO;
import com.river.module.blog.dal.mysql.PostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.blog.enums.ErrorCodeConstants.*;

@Service
@Validated
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private AuthorService authorService;

    @Override
    public Long createPost(PostDO post) {
        authorService.validateAuthorExists(post.getAuthorId());
        validateSlugUnique(null, post.getSlug());
        if (post.getViewCount() == null) {
            post.setViewCount(0);
        }
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostDO post) {
        validatePostExists(post.getId());
        authorService.validateAuthorExists(post.getAuthorId());
        validateSlugUnique(post.getId(), post.getSlug());
        postMapper.updateById(post);
    }

    @Override
    public void deletePost(Long id) {
        validatePostExists(id);
        postMapper.deleteById(id);
    }

    @Override
    public PostDO getPost(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public List<PostDO> getPostList() {
        return postMapper.selectList();
    }

    @Override
    public PageResult<PostDO> getPostPage(PostPageReqVO pageReqVO) {
        return postMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PostDO> getPostListByAuthorId(Long authorId) {
        return postMapper.selectList(PostDO::getAuthorId, authorId);
    }

    @Override
    public void validatePostExists(Long id) {
        if (postMapper.selectById(id) == null) {
            throw exception(POST_NOT_EXISTS);
        }
    }

    @Override
    public PostDO getPostBySlug(String slug) {
        return postMapper.selectBySlug(slug);
    }

    private void validateSlugUnique(Long id, String slug) {
        PostDO existing = postMapper.selectBySlug(slug);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(POST_SLUG_DUPLICATE);
        }
    }
}
