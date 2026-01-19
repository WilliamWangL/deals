package com.river.framework.common.biz.affiliate.dto;

import lombok.Data;

/**
 * 商家简单信息 DTO
 */
@Data
public class MerchantSimpleRespDTO {

    /**
     * 商家编号
     */
    private Long id;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 商家 Logo URL
     */
    private String logoUrl;

    /**
     * 商家 Slug
     */
    private String slug;

}
