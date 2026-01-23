package com.river.module.affiliate.controller.admin.network;

import com.river.framework.common.pojo.CommonResult;
import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.affiliate.dal.dataobject.AffiliateNetworkDO;
import com.river.module.affiliate.service.AffiliateNetworkService;
import com.river.module.affiliate.service.network.admitad.AdmitadSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AffiliateNetworkController 同步接口单元测试
 */
class AffiliateNetworkControllerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AffiliateNetworkController controller;

    @Mock
    private AdmitadSyncService admitadSyncService;

    @Mock
    private AffiliateNetworkService affiliateNetworkService;

    private AffiliateNetworkDO testNetwork;

    @BeforeEach
    void setUp() {
        testNetwork = new AffiliateNetworkDO();
        testNetwork.setId(1L);
        testNetwork.setCode("admitad");
        testNetwork.setName("Admitad");
    }

    @Nested
    @DisplayName("sync-deals 接口测试")
    class SyncDealsTest {

        @Test
        @DisplayName("无参数返回错误")
        void noParams_returnsError() {
            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncDeals(null, null);

            // Then
            assertNotNull(result);
            assertFalse(result.getData().isSuccess());
            assertEquals("At least one of networkId or code is required", result.getData().getMessage());
        }

        @Test
        @DisplayName("无效 networkId 格式返回错误")
        void invalidNetworkIdFormat_returnsError() {
            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncDeals("abc", null);

            // Then
            assertNotNull(result);
            assertFalse(result.getData().isSuccess());
            assertEquals("Invalid networkId format", result.getData().getMessage());
        }

        @Test
        @DisplayName("networkId 不存在返回错误")
        void networkIdNotFound_returnsError() {
            // Given
            when(affiliateNetworkService.getNetwork(999L)).thenReturn(null);

            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncDeals("999", null);

            // Then
            assertNotNull(result);
            assertFalse(result.getData().isSuccess());
            assertEquals("Network not found: 999", result.getData().getMessage());
        }

        @Test
        @DisplayName("有效 code 返回成功")
        void validCode_returnsSuccess() {
            // Given
            Map<String, Object> stats = new HashMap<>();
            stats.put("deals", 5);
            stats.put("coupons", 3);
            stats.put("failed", 0);

            AffiliateNetworkController.SyncResult syncResult = AffiliateNetworkController.SyncResult.success("Deal sync completed", stats);
            when(admitadSyncService.syncDeals("admitad")).thenReturn(syncResult);

            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncDeals(null, "admitad");

            // Then
            assertNotNull(result);
            assertTrue(result.getData().isSuccess());
            assertEquals("Deal sync completed", result.getData().getMessage());
            assertNotNull(result.getData().getData());
            assertEquals(5, result.getData().getData().get("deals"));
        }

        @Test
        @DisplayName("有效 networkId 返回成功")
        void validNetworkId_returnsSuccess() {
            // Given
            Map<String, Object> stats = new HashMap<>();
            stats.put("deals", 3);
            stats.put("coupons", 2);
            stats.put("failed", 0);

            AffiliateNetworkController.SyncResult syncResult = AffiliateNetworkController.SyncResult.success("Deal sync completed", stats);
            when(affiliateNetworkService.getNetwork(1L)).thenReturn(testNetwork);
            when(admitadSyncService.syncDeals("admitad")).thenReturn(syncResult);

            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncDeals("1", null);

            // Then
            assertNotNull(result);
            assertTrue(result.getData().isSuccess());
            assertEquals(3, result.getData().getData().get("deals"));

            // 验证 networkId 被正确解析为 code
            verify(affiliateNetworkService).getNetwork(1L);
            verify(admitadSyncService).syncDeals("admitad");
        }

        @Test
        @DisplayName("networkId 优先于 code")
        void bothParams_networkIdTakesPriority() {
            // Given
            Map<String, Object> stats = new HashMap<>();
            stats.put("deals", 3);
            stats.put("coupons", 2);
            stats.put("failed", 0);

            AffiliateNetworkController.SyncResult syncResult = AffiliateNetworkController.SyncResult.success("Deal sync completed", stats);
            when(affiliateNetworkService.getNetwork(1L)).thenReturn(testNetwork);
            when(admitadSyncService.syncDeals("admitad")).thenReturn(syncResult);

            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncDeals("1", "other_code");

            // Then
            assertNotNull(result);
            assertTrue(result.getData().isSuccess());

            // 验证 networkId 优先，code 被忽略
            verify(affiliateNetworkService).getNetwork(1L);
            verify(admitadSyncService).syncDeals("admitad");  // 使用 network 的 code
            verify(affiliateNetworkService, never()).getNetworkByCode("other_code");
        }
    }

    @Nested
    @DisplayName("sync-coupons-only 接口测试")
    class SyncCouponsOnlyTest {

        @Test
        @DisplayName("无参数返回错误")
        void noParams_returnsError() {
            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncCouponsOnly(null, null);

            // Then
            assertNotNull(result);
            assertFalse(result.getData().isSuccess());
            assertEquals("At least one of networkId or code is required", result.getData().getMessage());
        }

        @Test
        @DisplayName("无效 networkId 格式返回错误")
        void invalidNetworkIdFormat_returnsError() {
            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncCouponsOnly("abc", null);

            // Then
            assertNotNull(result);
            assertFalse(result.getData().isSuccess());
            assertEquals("Invalid networkId format", result.getData().getMessage());
        }

        @Test
        @DisplayName("有效 code 返回成功")
        void validCode_returnsSuccess() {
            // Given
            Map<String, Object> stats = new HashMap<>();
            stats.put("coupons", 10);
            stats.put("deals", 2);
            stats.put("failed", 0);

            AffiliateNetworkController.SyncResult syncResult = AffiliateNetworkController.SyncResult.success("Coupon sync completed", stats);
            when(admitadSyncService.syncCouponsOnly("admitad")).thenReturn(syncResult);

            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncCouponsOnly(null, "admitad");

            // Then
            assertNotNull(result);
            assertTrue(result.getData().isSuccess());
            assertEquals("Coupon sync completed", result.getData().getMessage());
            assertEquals(10, result.getData().getData().get("coupons"));
        }

        @Test
        @DisplayName("有效 networkId 返回成功")
        void validNetworkId_returnsSuccess() {
            // Given
            Map<String, Object> stats = new HashMap<>();
            stats.put("coupons", 7);
            stats.put("deals", 1);
            stats.put("failed", 0);

            AffiliateNetworkController.SyncResult syncResult = AffiliateNetworkController.SyncResult.success("Coupon sync completed", stats);
            when(affiliateNetworkService.getNetwork(1L)).thenReturn(testNetwork);
            when(admitadSyncService.syncCouponsOnly("admitad")).thenReturn(syncResult);

            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncCouponsOnly("1", null);

            // Then
            assertNotNull(result);
            assertTrue(result.getData().isSuccess());
            assertEquals(7, result.getData().getData().get("coupons"));

            verify(affiliateNetworkService).getNetwork(1L);
            verify(admitadSyncService).syncCouponsOnly("admitad");
        }

        @Test
        @DisplayName("networkId 优先于 code")
        void bothParams_networkIdTakesPriority() {
            // Given
            Map<String, Object> stats = new HashMap<>();
            stats.put("coupons", 5);
            stats.put("deals", 1);
            stats.put("failed", 0);

            AffiliateNetworkController.SyncResult syncResult = AffiliateNetworkController.SyncResult.success("Coupon sync completed", stats);
            when(affiliateNetworkService.getNetwork(1L)).thenReturn(testNetwork);
            when(admitadSyncService.syncCouponsOnly("admitad")).thenReturn(syncResult);

            // When
            CommonResult<AffiliateNetworkController.SyncResult> result = controller.syncCouponsOnly("1", "other_code");

            // Then
            assertNotNull(result);
            assertTrue(result.getData().isSuccess());

            verify(affiliateNetworkService).getNetwork(1L);
            verify(admitadSyncService).syncCouponsOnly("admitad");
            verify(affiliateNetworkService, never()).getNetworkByCode("other_code");
        }
    }
}
