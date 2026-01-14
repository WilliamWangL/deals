package com.river.module.affiliate.service;

import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.affiliate.dal.dataobject.MerchantDO;
import com.river.module.affiliate.dal.mysql.MerchantMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link MerchantServiceImpl} 的单元测试
 */
class MerchantServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private MerchantServiceImpl merchantService;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private AffiliateNetworkService networkService;

    @Test
    void testCreateMerchant_Success() {
        // Given
        MerchantDO merchant = new MerchantDO();
        merchant.setName("Test Merchant");
        merchant.setNetworkId(1L);
        doNothing().when(networkService).validateNetworkExists(eq(1L));
        when(merchantMapper.insert(any(MerchantDO.class))).thenReturn(1);

        // When
        Long result = merchantService.createMerchant(merchant);

        // Then
        verify(networkService).validateNetworkExists(eq(1L));
        verify(merchantMapper).insert(any(MerchantDO.class));
    }

    @Test
    void testGetMerchant_Success() {
        // Given
        Long merchantId = 1L;
        MerchantDO expectedMerchant = new MerchantDO();
        expectedMerchant.setId(merchantId);
        expectedMerchant.setName("Test Merchant");
        when(merchantMapper.selectById(eq(merchantId))).thenReturn(expectedMerchant);

        // When
        MerchantDO result = merchantService.getMerchant(merchantId);

        // Then
        assertNotNull(result);
        assertEquals(merchantId, result.getId());
        assertEquals("Test Merchant", result.getName());
        verify(merchantMapper).selectById(eq(merchantId));
    }

    @Test
    void testGetMerchant_NotFound() {
        // Given
        Long merchantId = 999L;
        when(merchantMapper.selectById(eq(merchantId))).thenReturn(null);

        // When
        MerchantDO result = merchantService.getMerchant(merchantId);

        // Then
        assertNull(result);
        verify(merchantMapper).selectById(eq(merchantId));
    }

    @Test
    void testDeleteMerchant_Success() {
        // Given
        Long merchantId = 1L;
        MerchantDO existingMerchant = new MerchantDO();
        existingMerchant.setId(merchantId);
        when(merchantMapper.selectById(eq(merchantId))).thenReturn(existingMerchant);
        when(merchantMapper.deleteById(eq(merchantId))).thenReturn(1);

        // When
        merchantService.deleteMerchant(merchantId);

        // Then
        verify(merchantMapper).selectById(eq(merchantId));
        verify(merchantMapper).deleteById(eq(merchantId));
    }

}
