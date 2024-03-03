/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2011 09:27:07
 */
package de.mnet.wita;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class EncodingTest {
    public void assertUmlautsStillSame() {
        String umlauts = new EncodingGeneratorTest().getUmlauts();
        assertEquals(umlauts, "üäöó");
    }
}


