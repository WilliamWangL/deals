package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.author.vo.AuthorPageReqVO;
import com.river.module.blog.dal.dataobject.AuthorDO;
import com.river.module.blog.dal.mysql.AuthorMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.blog.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AuthorServiceImpl implements AuthorService {

    @Resource
    private AuthorMapper authorMapper;

    @Override
    public Long createAuthor(AuthorDO author) {
        validateSlugUnique(null, author.getSlug());
        authorMapper.insert(author);
        return author.getId();
    }

    @Override
    public void updateAuthor(AuthorDO author) {
        validateAuthorExists(author.getId());
        validateSlugUnique(author.getId(), author.getSlug());
        authorMapper.updateById(author);
    }

    @Override
    public void deleteAuthor(Long id) {
        validateAuthorExists(id);
        authorMapper.deleteById(id);
    }

    @Override
    public AuthorDO getAuthor(Long id) {
        return authorMapper.selectById(id);
    }

    @Override
    public List<AuthorDO> getAuthorList() {
        return authorMapper.selectList();
    }

    @Override
    public PageResult<AuthorDO> getAuthorPage(AuthorPageReqVO pageReqVO) {
        return authorMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateAuthorExists(Long id) {
        if (authorMapper.selectById(id) == null) {
            throw exception(AUTHOR_NOT_EXISTS);
        }
    }

    private void validateSlugUnique(Long id, String slug) {
        AuthorDO existing = authorMapper.selectBySlug(slug);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(AUTHOR_SLUG_DUPLICATE);
        }
    }
}
