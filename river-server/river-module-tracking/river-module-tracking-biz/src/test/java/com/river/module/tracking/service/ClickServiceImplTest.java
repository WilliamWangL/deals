package com.river.module.tracking.service;

import com.river.framework.test.core.ut.BaseMockitoUnitTest;
import com.river.module.affiliate.service.OfferService;
import com.river.module.tracking.dal.dataobject.ClickDO;
import com.river.module.tracking.dal.mysql.ClickMapper;
import com.river.module.tracking.dal.mysql.TrackingLinkMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link ClickServiceImpl} 的单元测试
 */
class ClickServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ClickServiceImpl clickService;

    @Mock
    private ClickMapper clickMapper;

    @Mock
    private TrackingLinkMapper trackingLinkMapper;

    @Mock
    private OfferService offerService;

    @Test
    void testGetClick_Success() {
        // Given
        String clickId = "ABC123456789";
        ClickDO expectedClick = new ClickDO();
        expectedClick.setClickId(clickId);
        expectedClick.setOfferId(1L);
        when(clickMapper.selectByClickId(eq(clickId))).thenReturn(expectedClick);

        // When
        ClickDO result = clickService.getClick(clickId);

        // Then
        assertNotNull(result);
        assertEquals(clickId, result.getClickId());
        assertEquals(1L, result.getOfferId());
        verify(clickMapper).selectByClickId(eq(clickId));
    }

    @Test
    void testGetClick_NotFound() {
        // Given
        String clickId = "NOTEXIST";
        when(clickMapper.selectByClickId(eq(clickId))).thenReturn(null);

        // When
        ClickDO result = clickService.getClick(clickId);

        // Then
        assertNull(result);
        verify(clickMapper).selectByClickId(eq(clickId));
    }

    @Test
    void testCreateClick_Success() {
        // Given
        ClickDO click = new ClickDO();
        click.setClickId("ABC123456789");
        click.setOfferId(1L);
        when(clickMapper.insert(any(ClickDO.class))).thenReturn(1);

        // When
        String result = clickService.createClick(click);

        // Then
        assertEquals("ABC123456789", result);
        verify(clickMapper).insert(any(ClickDO.class));
    }

    @Test
    void testValidateClickExists_NotFound() {
        // Given
        String clickId = "NOTEXIST";
        when(clickMapper.selectByClickId(eq(clickId))).thenReturn(null);

        // When & Then
        assertThrows(Exception.class, () -> clickService.validateClickExists(clickId));
        verify(clickMapper).selectByClickId(eq(clickId));
    }

}
