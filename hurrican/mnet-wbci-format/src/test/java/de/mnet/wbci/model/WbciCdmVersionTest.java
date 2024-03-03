/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.08.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
@Test(groups = UNIT)
public class WbciCdmVersionTest {
    @Test
    public void testGetVersion() throws Exception {
        Assert.assertEquals(WbciCdmVersion.V1.getVersion(), "1");
    }

    @Test
    public void testGetDefault() throws Exception {
        Assert.assertEquals(WbciCdmVersion.getDefault(), WbciCdmVersion.V1);

    }

    @Test
    public void testGetCdmVersion() throws Exception {
        Assert.assertEquals(WbciCdmVersion.getCdmVersion("1"), WbciCdmVersion.V1);

    }

    @Test
    public void testIsGreaterOrEqualThan() throws Exception {
        Assert.assertTrue(WbciCdmVersion.V1.isGreaterOrEqualThan(WbciCdmVersion.V1));
    }
}
