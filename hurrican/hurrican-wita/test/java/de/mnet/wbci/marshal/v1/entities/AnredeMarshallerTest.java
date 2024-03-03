/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.Anrede;

@Test(groups = BaseTest.UNIT)
public class AnredeMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private AnredeMarshaller testling;

    @Test
    public void testApply() throws Exception {
        // check anrede == null
        assertEquals("9", testling.apply(null));
        for (Anrede anrede : Anrede.values()) {
            String value = testling.apply(anrede);
            switch (anrede) {
                case HERR:
                    assertEquals("1", value);
                    break;
                case FRAU:
                    assertEquals("2", value);
                    break;
                case FIRMA:
                    assertEquals("4", value);
                    break;
                case UNBEKANNT:
                    assertEquals("9", value);
                    break;
                default:
                    assertEquals("9", value);
                    break;
            }
        }
    }
}
