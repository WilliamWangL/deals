package com.river.module.affiliate.dal.dataobject;

import com.river.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 分类 DO
 */
@TableName("river_affiliate_category")
@KeySequence("river_affiliate_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDO extends BaseDO {

    @TableId
    private Long id;

    /** 父分类 ID，0 表示顶级 */
    private Long parentId;

    /** 分类名称 */
    private String name;

    /** URL Slug */
    private String slug;

    /** 层级：1-一级，2-二级，3-三级 */
    private Integer level;

    /** 排序 */
    private Integer sort;

    /** 图标 */
    private String icon;

    /** 状态：0-停用，1-启用 */
    private Integer status;
}
