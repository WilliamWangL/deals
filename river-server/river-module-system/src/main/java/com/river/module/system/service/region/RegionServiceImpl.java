package com.river.module.system.service.region;

import com.river.module.system.controller.app.region.vo.RegionRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 全局地区代码 */
    private static final String GLOBAL_CODE = "GLOBAL";
    /** 全球特殊标记 */
    private static final String GLOBAL_MARKER = "00";
    /** 返回的最大国家数量 */
    private static final int MAX_COUNTRIES = 50;
    /** 默认租户 ID */
    private static final long DEFAULT_TENANT_ID = 1L;

    /** ISO 国家代码到名称的映射 */
    private static final Map<String, String> COUNTRY_NAMES = Map.ofEntries(
        Map.entry("US", "United States"),
        Map.entry("GB", "United Kingdom"),
        Map.entry("DE", "Germany"),
        Map.entry("FR", "France"),
        Map.entry("ES", "Spain"),
        Map.entry("IT", "Italy"),
        Map.entry("NL", "Netherlands"),
        Map.entry("AU", "Australia"),
        Map.entry("CA", "Canada"),
        Map.entry("BR", "Brazil"),
        Map.entry("MX", "Mexico"),
        Map.entry("RU", "Russia"),
        Map.entry("IN", "India"),
        Map.entry("CN", "China"),
        Map.entry("JP", "Japan"),
        Map.entry("KR", "South Korea"),
        Map.entry("PL", "Poland"),
        Map.entry("UA", "Ukraine"),
        Map.entry("BY", "Belarus"),
        Map.entry("KZ", "Kazakhstan"),
        Map.entry("00", "Global")
        // 可根据需要扩展
    );

    @Override
    public List<RegionRespVO> getAvailableRegions() {
        Map<String, Integer> regionCounts = new HashMap<>();

        // 统计 Deal 表
        countRegions("SELECT regions FROM river_coupon_deal WHERE deleted = 0 AND tenant_id = " + DEFAULT_TENANT_ID, regionCounts);
        // 统计 Coupon 表
        countRegions("SELECT regions FROM river_coupon_coupon WHERE deleted = 0 AND tenant_id = " + DEFAULT_TENANT_ID, regionCounts);
        // 统计 Merchant 表
        countRegions("SELECT regions FROM river_affiliate_merchant WHERE deleted = 0 AND tenant_id = " + DEFAULT_TENANT_ID, regionCounts);

        // 转换为结果列表
        List<RegionRespVO> result = new ArrayList<>();

        // 添加 GLOBAL 选项到首位
        int globalCount = regionCounts.values().stream().mapToInt(Integer::intValue).sum();
        result.add(new RegionRespVO(GLOBAL_CODE, "Global", globalCount));

        // 添加各个国家，按数量降序排序
        regionCounts.entrySet().stream()
            .filter(e -> !GLOBAL_MARKER.equals(e.getKey())) // 排除特殊标记
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(MAX_COUNTRIES) // 只返回前 MAX_COUNTRIES 个国家
            .forEach(e -> {
                String name = COUNTRY_NAMES.getOrDefault(e.getKey(), e.getKey());
                result.add(new RegionRespVO(e.getKey(), name, e.getValue()));
            });

        return result;
    }

    private void countRegions(String sql, Map<String, Integer> regionCounts) {
        try {
            List<String> regionsList = jdbcTemplate.queryForList(sql, String.class);
            for (String regions : regionsList) {
                if (regions == null || regions.isBlank()) continue;
                for (String code : regions.split(",")) {
                    String trimmed = code.trim();
                    if (!trimmed.isEmpty()) {
                        regionCounts.merge(trimmed, 1, Integer::sum);
                    }
                }
            }
        } catch (Exception e) {
            // 查询失败时记录日志但不影响其他表的统计
            log.warn("Failed to count regions from query: {}", sql, e);
        }
    }
}
