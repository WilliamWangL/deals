package com.river.module.blog.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.blog.controller.admin.tag.vo.TagPageReqVO;
import com.river.module.blog.dal.dataobject.TagDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapperX<TagDO> {

    default PageResult<TagDO> selectPage(TagPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TagDO>()
                .likeIfPresent(TagDO::getName, reqVO.getName())
                .eqIfPresent(TagDO::getStatus, reqVO.getStatus())
                .orderByDesc(TagDO::getId));
    }

    default TagDO selectBySlug(String slug) {
        return selectOne(TagDO::getSlug, slug);
    }
}
