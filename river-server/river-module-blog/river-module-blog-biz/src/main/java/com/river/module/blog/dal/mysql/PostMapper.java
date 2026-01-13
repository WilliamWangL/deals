package com.river.module.blog.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.dal.dataobject.PostDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapperX<PostDO> {

    default PageResult<PostDO> selectPage(PostPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PostDO>()
                .eqIfPresent(PostDO::getAuthorId, reqVO.getAuthorId())
                .likeIfPresent(PostDO::getTitle, reqVO.getTitle())
                .eqIfPresent(PostDO::getType, reqVO.getType())
                .eqIfPresent(PostDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PostDO::getFeatured, reqVO.getFeatured())
                .orderByDesc(PostDO::getId));
    }

    default PostDO selectBySlug(String slug) {
        return selectOne(PostDO::getSlug, slug);
    }
}
