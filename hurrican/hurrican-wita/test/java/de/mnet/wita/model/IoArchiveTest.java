/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 10:52:24
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.IOArchiveProperties.IOType;

@Test(groups = UNIT)
public class IoArchiveTest extends BaseTest {

    private IoArchive ioArchive;

    @BeforeMethod
    public void createIoArchive() {
        ioArchive = new IoArchive();
    }

    public void setIoType() {
        ioArchive.setIoType("IN");
        assertEquals(ioArchive.getIoType(), IOType.IN);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void setIllegalIoType() {
        ioArchive.setIoType("BLA BLUB");
    }

    public void setIoTypeUndefined() {
        ioArchive.setIoType("");
        assertEquals(ioArchive.getIoType(), IOType.UNDEFINED);

        ioArchive.setIoType((String) null);
        assertEquals(ioArchive.getIoType(), IOType.UNDEFINED);
    }

    public void testToString() {
        assertNotNull(ioArchive.toString());
    }
}
