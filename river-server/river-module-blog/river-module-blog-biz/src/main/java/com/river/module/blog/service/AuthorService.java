package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.author.vo.AuthorPageReqVO;
import com.river.module.blog.dal.dataobject.AuthorDO;
import jakarta.validation.Valid;

import java.util.List;

public interface AuthorService {

    Long createAuthor(@Valid AuthorDO author);

    void updateAuthor(@Valid AuthorDO author);

    void deleteAuthor(Long id);

    AuthorDO getAuthor(Long id);

    List<AuthorDO> getAuthorList();

    PageResult<AuthorDO> getAuthorPage(AuthorPageReqVO pageReqVO);

    void validateAuthorExists(Long id);
}
