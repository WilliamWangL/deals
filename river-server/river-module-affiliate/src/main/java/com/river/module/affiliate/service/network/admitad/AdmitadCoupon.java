package com.river.module.affiliate.service.network.admitad;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Admitad 优惠券 API 响应模型
 * 对应 API: GET /coupons/website/{websiteId}/
 */
@Data
public class AdmitadCoupon {

    private Long id;

    /** 优惠券名称 */
    private String name;

    /** 简短名称 */
    @JsonProperty("short_name")
    private String shortName;

    /** 优惠码 */
    private String promocode;

    /** 折扣描述 */
    private String discount;

    /** 描述/使用条款 */
    private String description;

    /** 开始日期 (格式: "2026-01-01 00:00:00") */
    @JsonProperty("date_start")
    private String dateStart;

    /** 结束日期 (格式: "2026-12-31 23:59:59") */
    @JsonProperty("date_end")
    private String dateEnd;

    /** 图片 URL */
    private String image;

    /** 跳转链接 */
    @JsonProperty("goto_link")
    private String gotoLink;

    /** 是否独家 */
    private Boolean exclusive;

    /** 类型：promocode, sale, other */
    private String species;

    /** 关联的广告活动 */
    private Campaign campaign;

    /** 适用地区列表 */
    private List<String> regions;

    /** 分类列表 */
    private List<Category> categories;

    /** 验证状态 */
    private Boolean verification;

    @Data
    public static class Campaign {
        private Long id;
        private String name;
        @JsonProperty("site_url")
        private String siteUrl;
    }

    @Data
    public static class Category {
        private Long id;
        private String name;
    }
}
