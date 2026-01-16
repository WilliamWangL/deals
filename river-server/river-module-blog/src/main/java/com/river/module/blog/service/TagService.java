package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.tag.vo.TagPageReqVO;
import com.river.module.blog.dal.dataobject.TagDO;
import jakarta.validation.Valid;

import java.util.List;

public interface TagService {

    Long createTag(@Valid TagDO tag);

    void updateTag(@Valid TagDO tag);

    void deleteTag(Long id);

    TagDO getTag(Long id);

    List<TagDO> getTagList();

    PageResult<TagDO> getTagPage(TagPageReqVO pageReqVO);

    void validateTagExists(Long id);
}
