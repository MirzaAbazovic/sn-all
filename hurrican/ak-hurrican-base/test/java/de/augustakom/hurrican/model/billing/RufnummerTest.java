/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.11.2011 14:23:35
 */
package de.augustakom.hurrican.model.billing;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class RufnummerTest extends BaseTest {

    public void toStringEinzelrufnummer() {
        Rufnummer r = new RufnummerBuilder().withDnNoOrig(55L).withOnKz("0821").withDnBase("123212")
                .withDirectDial(null).build();
        String stringRepresentation = r.toString();
        assertTrue(stringRepresentation.contains("55"));
        assertTrue(stringRepresentation.contains("0821"));
        assertTrue(stringRepresentation.contains("123212"));
        assertFalse(stringRepresentation.contains("Durchwahl"));
    }

    public void toStringAnlagenanschluss() {
        Rufnummer r = new RufnummerBuilder().withDnNoOrig(55L).withDnBase("123212").withDirectDial("5456").build();
        String stringRepresentation = r.toString();
        assertTrue(stringRepresentation.contains("55"));
        assertTrue(stringRepresentation.contains("123212"));
        assertTrue(stringRepresentation.contains("5456"));
    }

    public void testEqualRufnummern() {
        Rufnummer r1 = new RufnummerBuilder().withOnKz("0821").withDnBase("12").withDirectDial("34").withRangeFrom("6")
                .withRangeTo("8").withMainNumber(true).withFutureCarrier("ABC", "D001").build();
        Rufnummer r2 = new RufnummerBuilder().withOnKz("0821").withDnBase("12").withDirectDial("34").withRangeFrom("6")
                .withRangeTo("8").withMainNumber(false).withFutureCarrier("DEF", "D002").build();

        assertTrue(r1.isRufnummerEqual(r1));
        assertTrue(r1.isRufnummerEqual(r2));
    }

    public void testUnequalRufnummern() {
        Rufnummer r1 = new RufnummerBuilder().withOnKz("0821").withDnBase("12").build();
        Rufnummer r2 = new RufnummerBuilder().withOnKz("0812").withDnBase("12").build();

        assertFalse(r1.isRufnummerEqual(r2));
        assertFalse(r1.isRufnummerEqual(null));
    }

    public void rufnummerLengthEinzelrufnummer() {
        Rufnummer r = new RufnummerBuilder().withDnNoOrig(55L).withOnKz("0821").withDnBase("123212")
                .withDirectDial(null).build();
        assertEquals(r.getRufnummerLength(), 10L);
    }

    public void rufnummerLengthAnlagenanschluss() {
        Rufnummer r = new RufnummerBuilder().withDnNoOrig(55L).withOnKz("089").withDnBase("123212")
                .withDirectDial("5").build();
        assertEquals(r.getRufnummerLength(), 10L);

        r = new RufnummerBuilder().withDnNoOrig(55L).withOnKz("089").withDnBase("123212")
                .withDirectDial("1").withRangeFrom("00").withRangeTo("99").build();
        assertEquals(r.getRufnummerLength(), 11L);
    }
}
