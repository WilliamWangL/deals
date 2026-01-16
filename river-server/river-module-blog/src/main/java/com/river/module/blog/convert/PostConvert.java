package com.river.module.blog.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.post.vo.PostRespVO;
import com.river.module.blog.controller.admin.post.vo.PostSaveReqVO;
import com.river.module.blog.dal.dataobject.BlogPostDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PostConvert {

    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

   BlogPostDO convert(PostSaveReqVO bean);

    PostRespVO convert(BlogPostDO bean);

    List<PostRespVO> convertList(List<BlogPostDO> list);

    PageResult<PostRespVO> convertPage(PageResult<BlogPostDO> page);
}
