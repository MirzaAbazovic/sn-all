/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2010 14:28:54
 */
package de.augustakom.hurrican.model.cc.equipment;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 * TestNG Klasse fuer {@link HWBaugruppenChangeDlu}
 */
@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeDluTest extends BaseTest {

    private HWBaugruppenChangeDlu cut;

    @BeforeMethod
    public void setUp() {
        cut = new HWBaugruppenChangeDlu();
    }

    @DataProvider
    public Object[][] dataProviderIsValid() {
        List<HWBaugruppenChangeDluV5> v5Mappings = new ArrayList<HWBaugruppenChangeDluV5>();
        v5Mappings.add(new HWBaugruppenChangeDluV5());

        List<HWBaugruppenChangeDluV5> emptyV5Mappings = new ArrayList<HWBaugruppenChangeDluV5>();

        return new Object[][] {
                { "mg", "ac", v5Mappings, true },   // alle Werte gesetzt - ok
                { "mg", "ac", null, false },   // MG+AC gesetzt, kein Mapping - Fehler
                { "mg", null, v5Mappings, false },  // MG+Mapping, kein AC - Fehler
                { null, "ac", v5Mappings, false },  // AC+Mapping, kein MG - Fehler
                { null, null, null, true },   // kein Wert gesetzt - ok
                { "mg", "ac", emptyV5Mappings, false },  // MG+AC gesetzt, Mapping leer - Fehler
                { null, null, emptyV5Mappings, true },   // MG+AC nicht gesetzt, Mapping leer - ok
        };
    }

    @Test(dataProvider = "dataProviderIsValid")
    public void testIsValid(String mediaGateway, String accessController, List<HWBaugruppenChangeDluV5> v5Mappings, boolean isValid) {
        cut.setDluMediaGatewayNew(mediaGateway);
        cut.setDluAccessControllerNew(accessController);
        boolean result = cut.isValid(v5Mappings);

        assertEquals(result, isValid);
    }

}


