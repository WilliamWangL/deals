package com.river.module.blog.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.blog.controller.admin.post.vo.PostPageReqVO;
import com.river.module.blog.dal.dataobject.BlogPostDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogPostMapper extends BaseMapperX<BlogPostDO> {

    default PageResult<BlogPostDO> selectPage(PostPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BlogPostDO>()
                .eqIfPresent(BlogPostDO::getAuthorId, reqVO.getAuthorId())
                .likeIfPresent(BlogPostDO::getTitle, reqVO.getTitle())
                .eqIfPresent(BlogPostDO::getType, reqVO.getType())
                .eqIfPresent(BlogPostDO::getStatus, reqVO.getStatus())
                .eqIfPresent(BlogPostDO::getFeatured, reqVO.getFeatured())
                .orderByDesc(BlogPostDO::getId));
    }

    default BlogPostDO selectBySlug(String slug) {
        return selectOne(BlogPostDO::getSlug, slug);
    }
}
