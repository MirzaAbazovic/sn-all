/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2015
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static de.augustakom.hurrican.service.cc.impl.command.ImportHvtUmzugDetailsCommand.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = { BaseTest.UNIT })
public class ImportHvtUmzugDetailsCommandTest {

    public void testUevtStiftIsValid() {
        assertTrue(uevtStiftIsValid("1234-1-2"));
        assertTrue(uevtStiftIsValid("9234-13-2"));
        assertTrue(uevtStiftIsValid("6234-3-24"));
        assertTrue(uevtStiftIsValid("1434-13-24"));

        assertFalse(uevtStiftIsValid("1434-13-24-"));
        assertFalse(uevtStiftIsValid("14324-13-24"));
        assertFalse(uevtStiftIsValid("324-13-24"));
        assertFalse(uevtStiftIsValid("4324-137-24"));
        assertFalse(uevtStiftIsValid("a324-13-24"));
    }
}
