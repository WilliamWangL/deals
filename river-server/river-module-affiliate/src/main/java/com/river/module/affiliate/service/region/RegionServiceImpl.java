package com.river.module.affiliate.service.region;

import com.river.module.affiliate.controller.app.region.vo.RegionRespVO;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.dal.mysql.MerchantMapper;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.dal.dataobject.DealDO;
import com.river.module.coupon.dal.mysql.CouponMapper;
import com.river.module.coupon.dal.mysql.DealMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.river.framework.common.util.region.RegionUtils.GLOBAL_CODE;

/**
 * 地区查询服务实现
 * 使用 MyBatis Plus 查询，自动获得多租户支持
 */
@Slf4j
@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private DealMapper dealMapper;

    @Resource
    private CouponMapper couponMapper;

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
        Map.entry(GLOBAL_CODE, "Global")
    );

    @Override
    public List<RegionRespVO> getAvailableRegions() {
        Map<String, Integer> regionCounts = new HashMap<>();

        // 使用 Mapper 查询，自动获得多租户支持（tenant_id 由 TenantDatabaseInterceptor 注入）
        countRegionsFromMerchant(regionCounts);
        countRegionsFromDeal(regionCounts);
        countRegionsFromCoupon(regionCounts);

        // 转换为结果列表
        List<RegionRespVO> result = new ArrayList<>();

        // 添加 GLOBAL 选项到首位
        int globalCount = regionCounts.getOrDefault(GLOBAL_CODE, 0);
        int otherCount = regionCounts.entrySet().stream()
            .filter(e -> !GLOBAL_CODE.equals(e.getKey()))
            .mapToInt(Map.Entry::getValue)
            .sum();
        int totalGlobalCount = globalCount + otherCount;
        result.add(new RegionRespVO(GLOBAL_CODE, "Global", totalGlobalCount));

        // 添加各个国家，按数量降序排序
        regionCounts.entrySet().stream()
            .filter(e -> !GLOBAL_CODE.equals(e.getKey()))
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> {
                String name = COUNTRY_NAMES.getOrDefault(e.getKey(), e.getKey());
                result.add(new RegionRespVO(e.getKey(), name, e.getValue()));
            });

        return result;
    }

    private void countRegionsFromMerchant(Map<String, Integer> regionCounts) {
        try {
            List<MerchantDO> list = merchantMapper.selectList();
            countRegions(list, regionCounts);
        } catch (Exception e) {
            log.warn("Failed to count regions from merchant table", e);
        }
    }

    private void countRegionsFromDeal(Map<String, Integer> regionCounts) {
        try {
            List<DealDO> list = dealMapper.selectList();
            countRegions(list, regionCounts);
        } catch (Exception e) {
            log.warn("Failed to count regions from deal table", e);
        }
    }

    private void countRegionsFromCoupon(Map<String, Integer> regionCounts) {
        try {
            List<CouponDO> list = couponMapper.selectList();
            countRegions(list, regionCounts);
        } catch (Exception e) {
            log.warn("Failed to count regions from coupon table", e);
        }
    }

    private void countRegions(List<?> list, Map<String, Integer> regionCounts) {
        for (Object obj : list) {
            try {
                java.lang.reflect.Method getRegions = obj.getClass().getMethod("getRegions");
                Object regionsValue = getRegions.invoke(obj);
                if (regionsValue == null) continue;
                
                String regions;
                if (regionsValue instanceof List) {
                    regions = String.join(",", (List<String>) regionsValue);
                } else {
                    regions = regionsValue.toString();
                }
                
                if (regions.isBlank()) continue;
                for (String code : regions.split(",")) {
                    String trimmed = code.trim();
                    if (!trimmed.isEmpty()) {
                        regionCounts.merge(trimmed, 1, Integer::sum);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to get regions from object: {}", obj, e);
            }
        }
    }
}
