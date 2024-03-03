/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.10.2010 12:13:18
 */
package de.augustakom.hurrican.model.cc;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


@Test(groups = BaseTest.UNIT)
public class RangierungTest extends BaseTest {

    public void testSetBemerkung() {
        Rangierung rangierung = new Rangierung();
        rangierung.setBemerkung("test");
        assertEquals(rangierung.getBemerkung(), "test");
    }

    public void testSetBemerkungDoNetSet() {
        Rangierung rangierung = new Rangierung();
        rangierung.setBemerkung("Querverbindung: 123");
        rangierung.setBemerkung("change");
        assertEquals(rangierung.getBemerkung(), "Querverbindung: 123");
    }


    public void testSetBemerkungDoSet() {
        Rangierung rangierung = new Rangierung();
        rangierung.setBemerkung("Querverbindung: 123");
        rangierung.setBemerkung("Querverbindung: 456");
        assertEquals(rangierung.getBemerkung(), "Querverbindung: 456");
    }

    public void testSetBemerkungClear() {
        Rangierung rangierung = new Rangierung();
        rangierung.setBemerkung("abc");
        rangierung.setBemerkung(null);
        assertEquals(rangierung.getBemerkung(), null);
    }

    public void testSetBemerkungDoNotClear() {
        Rangierung rangierung = new Rangierung();
        rangierung.setBemerkung("Querverbindung: 123");
        rangierung.setBemerkung(null);
        assertEquals(rangierung.getBemerkung(), "Querverbindung: 123");
    }

    public void testRemoveBemerkung() {
        Rangierung rangierung = new Rangierung();
        rangierung.setBemerkung("Querverbindung: 123");
        rangierung.removeBemerkung();
        assertEquals(rangierung.getBemerkung(), null);
    }

}


