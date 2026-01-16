package com.river.module.blog.dal.mysql;

import com.river.framework.common.pojo.PageResult;
import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.river.module.blog.controller.admin.author.vo.AuthorPageReqVO;
import com.river.module.blog.dal.dataobject.AuthorDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthorMapper extends BaseMapperX<AuthorDO> {

    default PageResult<AuthorDO> selectPage(AuthorPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AuthorDO>()
                .likeIfPresent(AuthorDO::getName, reqVO.getName())
                .eqIfPresent(AuthorDO::getStatus, reqVO.getStatus())
                .orderByDesc(AuthorDO::getId));
    }

    default AuthorDO selectBySlug(String slug) {
        return selectOne(AuthorDO::getSlug, slug);
    }
}
