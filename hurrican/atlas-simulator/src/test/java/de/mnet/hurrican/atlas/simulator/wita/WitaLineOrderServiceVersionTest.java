/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.14
 */
package de.mnet.hurrican.atlas.simulator.wita;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WitaLineOrderServiceVersionTest {

    @Test
    public void testFromNamespace() {
        Assert.assertEquals(WitaLineOrderServiceVersion.fromNamespace(WitaLineOrderServiceVersion.V1.getNamespace()), WitaLineOrderServiceVersion.V1);
        Assert.assertNull(WitaLineOrderServiceVersion.fromNamespace("http://unknown"));
    }
}
