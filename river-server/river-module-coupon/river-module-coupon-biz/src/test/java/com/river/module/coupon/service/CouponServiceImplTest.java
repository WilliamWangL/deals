package com.river.module.coupon.service;

import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.coupon.dal.dataobject.CouponDO;
import com.river.module.coupon.dal.mysql.CouponMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link CouponServiceImpl} 的单元测试
 */
class CouponServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private CouponServiceImpl couponService;

    @Mock
    private CouponMapper couponMapper;

    @Test
    void testCreateCoupon_Success() {
        // Given
        CouponDO coupon = new CouponDO();
        coupon.setCode("TEST10");
        when(couponMapper.insert(any(CouponDO.class))).thenReturn(1);

        // When
        Long result = couponService.createCoupon(coupon);

        // Then
        verify(couponMapper).insert(any(CouponDO.class));
    }

    @Test
    void testGetCoupon_Success() {
        // Given
        Long couponId = 1L;
        CouponDO expectedCoupon = new CouponDO();
        expectedCoupon.setId(couponId);
        expectedCoupon.setCode("TEST10");
        when(couponMapper.selectById(eq(couponId))).thenReturn(expectedCoupon);

        // When
        CouponDO result = couponService.getCoupon(couponId);

        // Then
        assertNotNull(result);
        assertEquals(couponId, result.getId());
        assertEquals("TEST10", result.getCode());
        verify(couponMapper).selectById(eq(couponId));
    }

    @Test
    void testGetCoupon_NotFound() {
        // Given
        Long couponId = 999L;
        when(couponMapper.selectById(eq(couponId))).thenReturn(null);

        // When
        CouponDO result = couponService.getCoupon(couponId);

        // Then
        assertNull(result);
        verify(couponMapper).selectById(eq(couponId));
    }

    @Test
    void testDeleteCoupon_Success() {
        // Given
        Long couponId = 1L;
        CouponDO existingCoupon = new CouponDO();
        existingCoupon.setId(couponId);
        when(couponMapper.selectById(eq(couponId))).thenReturn(existingCoupon);
        when(couponMapper.deleteById(eq(couponId))).thenReturn(1);

        // When
        couponService.deleteCoupon(couponId);

        // Then
        verify(couponMapper).selectById(eq(couponId));
        verify(couponMapper).deleteById(eq(couponId));
    }

}
