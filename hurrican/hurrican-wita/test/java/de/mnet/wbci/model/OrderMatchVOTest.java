/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2014
 */
package de.mnet.wbci.model;

import java.util.*;
import com.google.common.collect.Sets;
import junit.framework.TestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class OrderMatchVOTest extends TestCase {
    public void testCreateOrderMatches() throws Exception {
        Collection<OrderMatchVO> result = OrderMatchVO.createOrderMatches(Sets.newHashSet(1L),
                OrderMatchVO.BasicSearch.DN);
        Assert.assertEquals(result.size(), 1);
        OrderMatchVO res = result.iterator().next();
        Assert.assertEquals(res.getOrderNoOrig().longValue(), 1L);
        Assert.assertEquals(res.getBasicSearch(), OrderMatchVO.BasicSearch.DN);
        Assert.assertEquals(res.getMatchViolations(), null);
    }

    public void testCreateOrderMatchesEmptySet() throws Exception {
        Collection<OrderMatchVO> result = OrderMatchVO.createOrderMatches(Sets.<Long>newHashSet(),
                OrderMatchVO.BasicSearch.DN);
        Assert.assertEquals(result.size(), 0);
        Assert.assertNotNull(result);
    }

    public void testAddNewOrderMatches() throws Exception {
        Collection<OrderMatchVO> dnMatches = OrderMatchVO.createOrderMatches(Sets.newHashSet(1L),
                OrderMatchVO.BasicSearch.DN);
        Collection<OrderMatchVO> locationMatches = OrderMatchVO.createOrderMatches(Sets.newHashSet(1L, 2L),
                OrderMatchVO.BasicSearch.LOCATION);
        Collection<OrderMatchVO> result = OrderMatchVO.addNewOrderMatches(dnMatches, locationMatches);
        Assert.assertEquals(result.size(), 2);
        for (OrderMatchVO res : result) {
            if (res.getOrderNoOrig() == 1L) {
                Assert.assertEquals(res.getBasicSearch(), OrderMatchVO.BasicSearch.DN);
            }
            else if (res.getOrderNoOrig() == 2L) {
                Assert.assertEquals(res.getBasicSearch(), OrderMatchVO.BasicSearch.LOCATION);
            }
            else {
                Assert.assertTrue(false, "unexpected element in list!");
            }
        }
    }

    public void testAddNewOrderMatchesEmptyBase() throws Exception {
        Collection<OrderMatchVO> locationMatches = OrderMatchVO.createOrderMatches(Sets.newHashSet(1L, 2L),
                OrderMatchVO.BasicSearch.LOCATION);
        Collection<OrderMatchVO> result = OrderMatchVO.addNewOrderMatches(new HashSet<OrderMatchVO>(), locationMatches);
        Assert.assertEquals(result.size(), 2);
        for (OrderMatchVO res : result) {
            Assert.assertEquals(res.getBasicSearch(), OrderMatchVO.BasicSearch.LOCATION);
        }
    }

    public void testViolates() throws Exception {
        Collection<OrderMatchVO> locationMatches = OrderMatchVO.createOrderMatches(Sets.newHashSet(1L, 2L, 3L),
                OrderMatchVO.BasicSearch.LOCATION);
        OrderMatchVO.violates(locationMatches, Sets.newHashSet(1L, 2L), OrderMatchVO.MatchViolation.NAME);
        Assert.assertEquals(locationMatches.size(), 3);
        for (OrderMatchVO res : locationMatches) {
            if (res.getOrderNoOrig() == 3L) {
                Assert.assertEquals(res.getMatchViolations().iterator().next(), OrderMatchVO.MatchViolation.NAME);
            }
            else {
                Assert.assertNull(res.getMatchViolations());
            }
        }
    }

    public void testGetOrderNoOrigs() throws Exception {
        Set<Long> orderNos = Sets.newHashSet(1L, 2L, 3L);
        Collection<OrderMatchVO> locationMatches = OrderMatchVO.createOrderMatches(orderNos, OrderMatchVO.BasicSearch.LOCATION);
        Assert.assertTrue(OrderMatchVO.getOrderNoOrigs(locationMatches).containsAll(orderNos));
    }
}
