package com.river.module.blog.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.blog.dal.dataobject.PostOfferDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostOfferMapper extends BaseMapperX<PostOfferDO> {

    default List<PostOfferDO> selectByPostId(Long postId) {
        return selectList(PostOfferDO::getPostId, postId);
    }

    default void deleteByPostId(Long postId) {
        delete(PostOfferDO::getPostId, postId);
    }
}
