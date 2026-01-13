package com.river.module.blog.service;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.tag.vo.TagPageReqVO;
import com.river.module.blog.dal.dataobject.TagDO;
import com.river.module.blog.dal.mysql.TagMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.river.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.river.module.blog.enums.ErrorCodeConstants.*;

@Service
@Validated
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Override
    public Long createTag(TagDO tag) {
        validateSlugUnique(null, tag.getSlug());
        if (tag.getPostCount() == null) {
            tag.setPostCount(0);
        }
        tagMapper.insert(tag);
        return tag.getId();
    }

    @Override
    public void updateTag(TagDO tag) {
        validateTagExists(tag.getId());
        validateSlugUnique(tag.getId(), tag.getSlug());
        tagMapper.updateById(tag);
    }

    @Override
    public void deleteTag(Long id) {
        validateTagExists(id);
        tagMapper.deleteById(id);
    }

    @Override
    public TagDO getTag(Long id) {
        return tagMapper.selectById(id);
    }

    @Override
    public List<TagDO> getTagList() {
        return tagMapper.selectList();
    }

    @Override
    public PageResult<TagDO> getTagPage(TagPageReqVO pageReqVO) {
        return tagMapper.selectPage(pageReqVO);
    }

    @Override
    public void validateTagExists(Long id) {
        if (tagMapper.selectById(id) == null) {
            throw exception(TAG_NOT_EXISTS);
        }
    }

    private void validateSlugUnique(Long id, String slug) {
        TagDO existing = tagMapper.selectBySlug(slug);
        if (existing != null && !existing.getId().equals(id)) {
            throw exception(TAG_SLUG_DUPLICATE);
        }
    }
}
