/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2011 13:22:56
 */
package de.augustakom.common.tools.net;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;

/**
 * TestNG-Test fuer {@link AbstractIPTools}.
 */
@Test(groups = { "unit" })
public class AbstractIPToolsTest extends BaseTest {

    private AbstractIPTools cutV4;
    private AbstractIPTools cutV6;

    @BeforeMethod
    public void setUp() {
        cutV4 = IPToolsV4.instance();
        cutV6 = IPToolsV6.instance();
    }

    @DataProvider(name = "dataProviderGetPrefixLength4ValidAddressV4")
    protected Object[][] dataProviderGetPrefixLength4ValidAddressV4() {
        return new Object[][] {
                { "", -1 },
                { null, -1 },
                { "192.168.1.1", 32 },
                { "192.168.1.1/0", 0 },
                { "192.168.1.1/1", 1 },
                { "192.168.1.1/2", 2 },
                { "192.168.1.1/3", 3 },
                { "192.168.1.1/08", 8 },
                { "192.168.1.1/16", 16 },
                { "192.168.1.1/32", 32 },
                { "192.168.1.1/33", -1 },
        };
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderGetPrefixLength4ValidAddressV4")
    public void testGetPrefixLength4ValidAddressV4(String address, int prefixLength) {
        assertEquals(cutV4.getPrefixLength4ValidAddress(address), prefixLength);
    }

    @DataProvider(name = "dataProviderGetPrefixLength4ValidAddressV6")
    protected Object[][] dataProviderGetPrefixLength4ValidAddressV6() {
        return new Object[][] {
                { "", -1 },
                { null, -1 },
                { "2001:DB8:a001::", 128 },
                { "2001:DB8:a001::/0", 0 },
                { "2001:DB8:a001::/1", 1 },
                { "2001:DB8:a001::/02", 2 },
                { "2001:DB8:a001::/003", 3 },
                { "2001:DB8:a001::/8", 8 },
                { "2001:DB8:a001::/64", 64 },
                { "2001:DB8:a001::/128", 128 },
                { "2001:DB8:a001::/129", -1 },
        };
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderGetPrefixLength4ValidAddressV6")
    public void testGetPrefixLength4ValidAddressV6(String address, int prefixLength) {
        assertEquals(cutV6.getPrefixLength4ValidAddress(address), prefixLength);
    }

    @DataProvider(name = "dataProviderCheckPrefixMaskV4")
    protected Object[][] dataProviderCheckPrefixMaskV4() {
        return new Object[][] {
                { -1, "255.1.0.0" },
                { -1, "255.0.1.0" },
                { -1, "255.0.1.1" },
                { -1, "251.0.0.0" },
                { -1, "255.251.0.0" },
                { 0, "0.0.0.0" },
                { 8, "255.0.0.0" },
                { 9, "255.128.0.0" },
        };
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderCheckPrefixMaskV4")
    public void testCheckPrefixMaskV4(int prefixLength, String netmask) {
        Pair<String[], String[]> parts = cutV4.splitAddress(netmask);
        if (parts == null) {
            fail("Netzmaske nicht korrekt!");
            return;
        }
        String[] segmentParts = parts.getSecond();
        IPToolsBinaryAddress binaryNetmask = IPToolsBinaryAddressV4.create();
        binaryNetmask.setSegments(segmentParts);
        assertEquals(cutV4.checkPrefixMask4Netmask(binaryNetmask), prefixLength);
    }

    @DataProvider(name = "dataProviderCheckPrefixMaskV6")
    protected Object[][] dataProviderCheckPrefixMaskV6() {
        return new Object[][] {
                { -1, "ffff:1::" },
                { -1, "ffff:0:1::" },
                { -1, "ffff:0:1:1::" },
                { -1, "ffef::" },
                { -1, "ffff:ffef::" },
                { 0, "::" },
                { 8, "ff00::" },
                { 9, "ff80::" },
                { 16, "ffff::" },
                { 17, "ffff:8000::" },
        };
    }

    @Test(groups = { "unit" }, dataProvider = "dataProviderCheckPrefixMaskV6")
    public void testCheckPrefixMaskV6(int prefixLength, String netmask) {
        Pair<String[], String[]> parts = cutV6.splitAddress(netmask);
        if (parts == null) {
            fail("Netzmaske nicht korrekt!");
            return;
        }
        String[] segmentParts = parts.getSecond();
        IPToolsBinaryAddress binaryNetmask = IPToolsBinaryAddressV6.create();
        binaryNetmask.setSegments(segmentParts);
        assertEquals(cutV6.checkPrefixMask4Netmask(binaryNetmask), prefixLength);
    }

    @DataProvider(name = "dataProviderNetmaskSizeV4")
    public Object[][] dataProviderNetmaskSizeV4() {
        return new Object[][] {
                { "212.18.0.188/30", 30 },
                { "62.245.151.113", 0 },
                { "invalidAddress", -1 }
        };
    }

    @Test(groups = { BaseTest.UNIT }, dataProvider = "dataProviderNetmaskSizeV4")
    public void testNetmaskSizeV4(String address, int expectedSize) {
        assertEquals(cutV4.netmaskSize(address), expectedSize);
    }

    @DataProvider(name = "dataProviderNetmaskSizeV6")
    public Object[][] dataProviderNetmaskSizeV6() {
        return new Object[][] {
                { "2001:0db8:85a3:08d3:1319:8a2e:0370:7347/64", 64 },
                { "2001:0db8:85a3:08d3:1319:8a2e:0370:7344", 0 },
                { "2001:0db8:85a3:08d3::/64", 64 },
                { "invalidAddress", -1 }
        };
    }

    @Test(groups = { BaseTest.UNIT }, dataProvider = "dataProviderNetmaskSizeV6")
    public void testNetmaskSizeV6(String address, int expectedSize) {
        assertEquals(cutV6.netmaskSize(address), expectedSize);
    }
} // end
