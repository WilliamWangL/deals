package com.river.module.blog.convert;

import com.river.framework.common.pojo.PageResult;
import com.river.module.blog.controller.admin.author.vo.AuthorRespVO;
import com.river.module.blog.controller.admin.author.vo.AuthorSaveReqVO;
import com.river.module.blog.dal.dataobject.AuthorDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AuthorConvert {

    AuthorConvert INSTANCE = Mappers.getMapper(AuthorConvert.class);

    AuthorDO convert(AuthorSaveReqVO bean);

    AuthorRespVO convert(AuthorDO bean);

    List<AuthorRespVO> convertList(List<AuthorDO> list);

    PageResult<AuthorRespVO> convertPage(PageResult<AuthorDO> page);
}
