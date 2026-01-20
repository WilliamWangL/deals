package com.river.framework.common.util.region;

import java.util.List;

/**
 * 地区过滤工具类
 */
public class RegionUtils {

    /** 全球标记 */
    public static final String GLOBAL = "GLOBAL";

    /** 特殊全球标记（数据库中使用） */
    public static final String GLOBAL_CODE = "00";

    /** 视为全球的最小国家数量 */
    public static final int GLOBAL_THRESHOLD = 50;

    /**
     * 判断是否为全球数据
     */
    public static boolean isGlobalRegion(List<String> regions) {
        if (regions == null || regions.isEmpty()) {
            return true;
        }
        if (regions.contains(GLOBAL_CODE)) {
            return true;
        }
        return regions.size() >= GLOBAL_THRESHOLD;
    }

    /**
     * 判断数据是否匹配指定地区
     */
    public static boolean matchesRegion(List<String> regions, String targetRegion) {
        if (targetRegion == null || targetRegion.isBlank() || GLOBAL.equals(targetRegion)) {
            return isGlobalRegion(regions);
        }
        if (regions == null || regions.isEmpty()) {
            return true; // 空 regions 视为全球可用
        }
        return regions.contains(targetRegion) || isGlobalRegion(regions);
    }

    private RegionUtils() {}
}
