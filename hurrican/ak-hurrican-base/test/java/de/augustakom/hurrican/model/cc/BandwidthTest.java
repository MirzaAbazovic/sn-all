/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.cc;

import static org.junit.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class BandwidthTest extends BaseTest {

    public void testIsWithin() {
        assertTrue(Bandwidth.create(50, 50).isWithin(Bandwidth.create(100, 100)));
        assertTrue(Bandwidth.create(50, 100).isWithin(Bandwidth.create(100, 100)));
        assertTrue(Bandwidth.create(50, null).isWithin(Bandwidth.create(100, 100)));
        assertTrue(Bandwidth.create(50, 50).isWithin(Bandwidth.create(100, null)));
        assertTrue(Bandwidth.create(50, 50).isWithin(null));
        assertTrue(Bandwidth.create(50, null).isWithin(null));

        assertFalse(Bandwidth.create(500, 50).isWithin(Bandwidth.create(100, null)));
        assertFalse(Bandwidth.create(500, 50).isWithin(Bandwidth.create(100, 100)));
        assertFalse(Bandwidth.create(500, null).isWithin(Bandwidth.create(100, 100)));
        assertFalse(Bandwidth.create(500, 500).isWithin(Bandwidth.create(100, 100)));
    }
}
