package com.river.module.blog.dal.mysql;

import com.river.framework.mybatis.core.mapper.BaseMapperX;
import com.river.module.blog.dal.dataobject.PostTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostTagMapper extends BaseMapperX<PostTagDO> {

    default List<PostTagDO> selectByPostId(Long postId) {
        return selectList(PostTagDO::getPostId, postId);
    }

    default void deleteByPostId(Long postId) {
        delete(PostTagDO::getPostId, postId);
    }
}
