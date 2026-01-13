package com.river.module.blog.controller.admin.post.vo;

import com.river.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 文章分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class PostPageReqVO extends PageParam {

    @Schema(description = "作者 ID")
    private Long authorId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否推荐")
    private Boolean featured;
}
