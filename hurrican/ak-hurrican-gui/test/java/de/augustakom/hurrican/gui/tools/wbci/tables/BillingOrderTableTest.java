/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.03.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import static org.mockito.Mockito.*;

import java.util.*;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wbci.model.OrderMatchVO;

/**
 *
 */
public class BillingOrderTableTest {
    private BillingOrderTable testling;

    @BeforeMethod
    public void setUp() throws Exception {
        BillingOrderTableModel tableMock = mock(BillingOrderTableModel.class);
        when(tableMock.getColumnDimensions()).thenReturn(new int[] { });
        testling = new BillingOrderTable(tableMock);
    }

    @Test
    public void testGetColorForOrderMatchDN() throws Exception {
        OrderMatchVO orderMatchVoMock = mock(OrderMatchVO.class);
        when(orderMatchVoMock.getBasicSearch()).thenReturn(OrderMatchVO.BasicSearch.DN);
        when(orderMatchVoMock.getMatchViolations()).thenReturn(Sets.newHashSet(OrderMatchVO.MatchViolation.LOCATION));
        Assert.assertEquals(testling.BG_COLOR_GREEN, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.DN, OrderMatchVO.BasicSearch.DN));

        when(orderMatchVoMock.getBasicSearch()).thenReturn(OrderMatchVO.BasicSearch.LOCATION);
        Assert.assertEquals(testling.BG_COLOR_GREY, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.DN, OrderMatchVO.BasicSearch.DN));

        when(orderMatchVoMock.getMatchViolations()).thenReturn(Sets.newHashSet(OrderMatchVO.MatchViolation.DN));
        Assert.assertEquals(testling.BG_COLOR_RED, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.DN, OrderMatchVO.BasicSearch.DN));
    }

    @Test
    public void testGetColorForOrderMatchLocation() throws Exception {
        OrderMatchVO orderMatchVoMock = mock(OrderMatchVO.class);
        when(orderMatchVoMock.getBasicSearch()).thenReturn(OrderMatchVO.BasicSearch.LOCATION);
        when(orderMatchVoMock.getMatchViolations()).thenReturn(null);
        Assert.assertEquals(testling.BG_COLOR_GREEN, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.LOCATION, OrderMatchVO.BasicSearch.LOCATION));

        when(orderMatchVoMock.getBasicSearch()).thenReturn(OrderMatchVO.BasicSearch.DN);
        Assert.assertEquals(testling.BG_COLOR_GREY, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.LOCATION, OrderMatchVO.BasicSearch.LOCATION));

        when(orderMatchVoMock.getMatchViolations()).thenReturn(Sets.newHashSet(OrderMatchVO.MatchViolation.LOCATION));
        Assert.assertEquals(testling.BG_COLOR_RED, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.LOCATION, OrderMatchVO.BasicSearch.LOCATION));
    }

    @Test
    public void testGetColorForOrderMatchName() throws Exception {
        OrderMatchVO orderMatchVoMock = mock(OrderMatchVO.class);
        when(orderMatchVoMock.getBasicSearch()).thenReturn(OrderMatchVO.BasicSearch.LOCATION);
        when(orderMatchVoMock.getMatchViolations()).thenReturn(new HashSet<OrderMatchVO.MatchViolation>());
        Assert.assertEquals(testling.BG_COLOR_GREEN, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.NAME, OrderMatchVO.BasicSearch.LOCATION, OrderMatchVO.BasicSearch.DN));
        when(orderMatchVoMock.getBasicSearch()).thenReturn(OrderMatchVO.BasicSearch.DN);
        Assert.assertEquals(testling.BG_COLOR_GREEN, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.NAME, OrderMatchVO.BasicSearch.LOCATION, OrderMatchVO.BasicSearch.DN));

        when(orderMatchVoMock.getBasicSearch()).thenReturn(null);
        Assert.assertEquals(testling.BG_COLOR_GREY, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.NAME, OrderMatchVO.BasicSearch.LOCATION, OrderMatchVO.BasicSearch.DN));

        when(orderMatchVoMock.getMatchViolations()).thenReturn(Sets.newHashSet(OrderMatchVO.MatchViolation.NAME));
        Assert.assertEquals(testling.BG_COLOR_RED, testling.getColorForOrderMatch(orderMatchVoMock, OrderMatchVO.MatchViolation.NAME, OrderMatchVO.BasicSearch.LOCATION, OrderMatchVO.BasicSearch.DN));
    }
}
