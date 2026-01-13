package com.river.module.blog.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.tag.vo.TagRespVO;
import com.river.module.blog.controller.admin.tag.vo.TagSaveReqVO;
import com.river.module.blog.dal.dataobject.TagDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TagConvert {

    TagConvert INSTANCE = Mappers.getMapper(TagConvert.class);

    TagDO convert(TagSaveReqVO bean);

    TagRespVO convert(TagDO bean);

    List<TagRespVO> convertList(List<TagDO> list);

    PageResult<TagRespVO> convertPage(PageResult<TagDO> page);
}
