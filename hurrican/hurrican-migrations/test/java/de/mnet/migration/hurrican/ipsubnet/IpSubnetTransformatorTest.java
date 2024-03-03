/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2012 09:44:20
 */
package de.mnet.migration.hurrican.ipsubnet;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class IpSubnetTransformatorTest extends BaseTest {

    private IpSubnetTransformator cut;

    @BeforeMethod
    public void setUp() {
        cut = new IpSubnetTransformator();
    }

    @DataProvider(name = "prefixMatchesIfExistDataProvider")
    protected Object[][] prefixMatchesIfExistDataProvider() {
        // @formatter:off
        return new Object[][] {
                { "188.174.236.1/29", 29, true },
                { "188.174.236.1/29", 30, false },
                { "188.174.236.1",    30, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "prefixMatchesIfExistDataProvider")
    public void testPrefixMatchesIfExist(String ip, int prefix, boolean expectMatch) {
        assertEquals(cut.prefixMatchesIfExist(ip, prefix), expectMatch);
    }


    @DataProvider(name = "modifyIpAddressDataProvider")
    protected Object[][] modifyIpAddressDataProvider() {
        // @formatter:off
        return new Object[][] {
                { "188.174.236.1/29", 29, "188.174.236.1/29" },
                { "188.174.236.1/29", 30, "188.174.236.1/30" },
                { "188.174.236.1",    29, "188.174.236.1/29" },
        };
        // @formatter:on
    }


    @Test(dataProvider = "modifyIpAddressDataProvider")
    public void testModifyIpAddressIfNecessary(String ip, int prefix, String expected) {
        assertEquals(cut.modifyIpAddress(ip, prefix), expected);
    }


}
