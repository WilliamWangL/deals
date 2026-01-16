package com.river.module.blog.controller.admin.author.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 作者分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorPageReqVO extends PageParam {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;
}
