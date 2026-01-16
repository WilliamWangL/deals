package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.dal.dataobject.BlogPostDO;
import com.river.module.blog.dal.mysql.BlogPostMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.blog.enums.ErrorCodeConstants.*;

@Service
@Validated
public class BlogPostServiceImpl implements BlogPostService {

    @Resource
    private BlogPostMapper blogPostMapper;

    @Resource
    private AuthorService authorService;

    @Override
    public Long createPost(BlogPostDO post) {
        authorService.validateAuthorExists(post.getAuthorId());
        validateSlugUnique(null, post.getSlug());
        if (post.getViewCount() == null) {
            post.setViewCount(0);
        }
        blogPostMapper.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(BlogPostDO post) {
        validatePostExists(post.getId());
        authorService.validateAuthorExists(post.getAuthorId());
        validateSlugUnique(post.getId(), post.getSlug());
        blogPostMapper.updateById(post);
    }

    @Override
    public void deletePost(Long id) {
        validatePostExists(id);
        blogPostMapper.deleteById(id);
    }

    @Override
    public BlogPostDO getPost(Long id) {
        return blogPostMapper.selectById(id);
    }

    @Override
    public List<BlogPostDO> getPostList() {
        return blogPostMapper.selectList();
    }

    @Override
    public PageResult<BlogPostDO> getPostPage(PostPageReqVO pageReqVO) {
        return blogPostMapper.selectPage(pageReqVO);
    }

    @Override
    public List<BlogPostDO> getPostListByAuthorId(Long authorId) {
        return blogPostMapper.selectList(BlogPostDO::getAuthorId, authorId);
    }

    @Override
    public void validatePostExists(Long id) {
        if (blogPostMapper.selectById(id) == null) {
            throw exception(POST_NOT_EXISTS);
        }
    }

    @Override
    public BlogPostDO getPostBySlug(String slug) {
        return blogPostMapper.selectBySlug(slug);
    }

    private void validateSlugUnique(Long id, String slug) {
        BlogPostDO existing = blogPostMapper.selectBySlug(slug);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(POST_SLUG_DUPLICATE);
        }
    }
}
