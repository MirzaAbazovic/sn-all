/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
@Test(groups = UNIT)
public class MeldungsCodeTest {
    @Test
    public void testBuildFromCode() throws Exception {
        Assert.assertEquals(MeldungsCode.ZWA, MeldungsCode.buildFromCode("8001"));
        Assert.assertEquals(MeldungsCode.UNKNOWN, MeldungsCode.buildFromCode("WRONG CODE"));
        Assert.assertEquals(MeldungsCode.UNKNOWN, MeldungsCode.buildFromCode(null));
    }

    @Test
    public void testBuildFromName() throws Exception {
        Assert.assertEquals(MeldungsCode.ZWA, MeldungsCode.buildFromName("ZWA"));
        Assert.assertEquals(MeldungsCode.UNKNOWN, MeldungsCode.buildFromName("WRONG CODE"));
        Assert.assertEquals(MeldungsCode.UNKNOWN, MeldungsCode.buildFromName(null));
    }

    @Test
    public void testIsADACode() throws Exception {
        Assert.assertTrue(MeldungsCode.ADAHSNR.isADACode());
        Assert.assertTrue(MeldungsCode.ADAPLZ.isADACode());
        Assert.assertTrue(MeldungsCode.ADAORT.isADACode());
        Assert.assertTrue(MeldungsCode.ADASTR.isADACode());
        Assert.assertFalse(MeldungsCode.ADFHSNR.isADACode());
    }
}
