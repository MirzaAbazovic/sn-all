/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.12.2010 09:27:51
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * TestNG Klasse fuer {@link CPSRadiusRoutesData}.
 */
@Test(groups = BaseTest.UNIT)
public class CPSRadiusRoutesDataTest extends BaseTest {

    @Test
    public void createIPv4_AddressOnly() {
        final String ipV4Address = "192.168.1.1";
        CPSRadiusRoutesData result = CPSRadiusRoutesData.createIPv4(ipV4Address);
        assertNotNull(result);
        assertEquals(result.getIpV4Address(), ipV4Address);
        assertNull(result.getIpV6Address());
        assertEquals(result.getPrefixLength(), Long.valueOf(0));
        assertEquals(result.getMetric(), Long.valueOf(0));
    }

    @Test
    public void createIPv4_AddressWithPrefixLength() {
        final String ipV4Address = "10.97.196.215/29";
        final Long prefixLength = Long.valueOf(29);
        CPSRadiusRoutesData result = CPSRadiusRoutesData.createIPv4(ipV4Address);
        assertNotNull(result);
        assertEquals(result.getIpV4Address(), ipV4Address);
        assertNull(result.getIpV6Address());
        assertEquals(result.getPrefixLength(), prefixLength);
        assertEquals(result.getMetric(), Long.valueOf(0));
    }

    @Test
    public void createIPv4_AddressWithPrefixLengthAndMetric() {
        final String ipV4Address = "10.97.196.215/29";
        final Long prefixLength = Long.valueOf(29);
        final Long metric = Long.valueOf(12);
        CPSRadiusRoutesData result = CPSRadiusRoutesData.createIPv4(ipV4Address).withMetric(metric);
        assertNotNull(result);
        assertEquals(result.getIpV4Address(), ipV4Address);
        assertNull(result.getIpV6Address());
        assertEquals(result.getPrefixLength(), prefixLength);
        assertEquals(result.getMetric(), metric);
    }

    @Test
    public void createIPv6_AddressOnly() {
        final String ipV6Address = "2000:DA01::";
        CPSRadiusRoutesData result = CPSRadiusRoutesData.createIPv6(ipV6Address);
        assertNotNull(result);
        assertEquals(result.getIpV6Address(), ipV6Address);
        assertNull(result.getIpV4Address());
        assertEquals(result.getPrefixLength(), Long.valueOf(0));
        assertEquals(result.getMetric(), Long.valueOf(0));
    }

}

