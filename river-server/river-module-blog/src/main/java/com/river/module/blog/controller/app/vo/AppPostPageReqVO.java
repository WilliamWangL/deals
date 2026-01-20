package com.river.module.blog.controller.app.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 博客文章分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPostPageReqVO extends PageParam {

    @Schema(description = "文章类型：1-Deal 2-Review 3-Tutorial 4-News")
    private Integer type;

    @Schema(description = "是否精选")
    private Boolean featured;

}
