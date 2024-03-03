/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.14
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class TechnologieTest {

    @DataProvider
    public Object[][] lookUpWbciTechnologieCodeDP() {
        return new Object[][] {
                { "001 TAL ISDN", Technologie.TAL_ISDN },
                { "002 TAL DSL", Technologie.TAL_DSL },
                { "003 TAL VDSL", Technologie.TAL_VDSL },
                { "017 Kupfer", Technologie.KUPFER },
                { "021 Sonstiges", Technologie.SONSTIGES },
                { null, null },
        };
    }

    @Test(dataProvider = "lookUpWbciTechnologieCodeDP")
    public void testLookUpWbciTechnologieCode(String technologie, Technologie expected) {
        assertEquals(Technologie.lookUpWbciTechnologieCode(technologie), expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLookUpWbciTechnologieCodeExceptionExpeced() {
        Technologie.lookUpWbciTechnologieCode("022 Sonstiges");
    }

    @DataProvider(name = "isRessourcenUebernahmePossibleDataProvider")
    public Object[][] isRessourcenUebernahmePossibleDataProvider() {
        return new Object[][] {
                // M-net ist aufnehmender Provider
                { Technologie.TAL_ISDN, IOType.OUT, null, true },
                { Technologie.TAL_DSL, IOType.OUT, null, true },
                { Technologie.TAL_VDSL, IOType.OUT, null, true },
                { Technologie.FTTC, IOType.OUT, null, true },
                { Technologie.KUPFER, IOType.OUT, CarrierCode.DTAG, true },
                { Technologie.KUPFER, IOType.OUT, CarrierCode.VODAFONE, false },
                { Technologie.KUPFER, IOType.OUT, null, false },
                { Technologie.KUPFER_MX, IOType.OUT, CarrierCode.DTAG, true },
                { Technologie.KUPFER_MX, IOType.OUT, CarrierCode.VODAFONE, false },
                { Technologie.FTTB, IOType.OUT, null, true },
                { Technologie.LTE, IOType.OUT, null, false },
                { Technologie.KOAX, IOType.OUT, null, false },
                { Technologie.KUPFER_GF, IOType.OUT, null, false },
                { Technologie.SONSTIGES, IOType.OUT, null, false },
                { Technologie.SONSTIGES, IOType.OUT, CarrierCode.DTAG, true },
                { Technologie.SONSTIGES, IOType.OUT, CarrierCode.VODAFONE, false },
                // M-net ist abgebender Provider
                { Technologie.TAL_ISDN, IOType.IN, null, true },
                { Technologie.TAL_DSL, IOType.IN, null, true },
                { Technologie.TAL_VDSL, IOType.IN, null, true },
                { Technologie.FTTC, IOType.IN, null, true },
                { Technologie.KUPFER, IOType.IN, null, false },
                { Technologie.KUPFER, IOType.IN, CarrierCode.DTAG, false },
                { Technologie.KUPFER, IOType.IN, CarrierCode.VODAFONE, false },
                { Technologie.KUPFER_MX, IOType.IN, null, false },
                { Technologie.KUPFER_MX, IOType.IN, CarrierCode.DTAG, false },
                { Technologie.KUPFER_MX, IOType.IN, CarrierCode.VODAFONE, false },
                { Technologie.FTTB, IOType.IN, null, true },
                { Technologie.LTE, IOType.IN, null, false },
                { Technologie.KOAX, IOType.IN, null, false },
                { Technologie.KUPFER_GF, IOType.IN, null, false },
                { Technologie.SONSTIGES, IOType.IN, null, false },
        };
    }


    @Test(dataProvider = "isRessourcenUebernahmePossibleDataProvider")
    public void testIsRessourcenUebernahmePossible(Technologie technologie, IOType ruemVaIoType, CarrierCode carrierCode, boolean expected) {
        assertEquals(technologie.isRessourcenUebernahmePossible(ruemVaIoType, carrierCode), expected);
    }


    @DataProvider(name = "isCompatibleToDataProvider")
    public Object[][] isCompatibleToDataProvider() {
        return new Object[][] {
                { Technologie.TAL_ISDN, Technologie.TAL_ISDN, true },
                { Technologie.TAL_ISDN, Technologie.TAL_DSL, true },
                { Technologie.TAL_ISDN, Technologie.TAL_VDSL, true },
                { Technologie.TAL_DSL, Technologie.TAL_ISDN, true },
                { Technologie.TAL_DSL, Technologie.TAL_DSL, true },
                { Technologie.TAL_DSL, Technologie.TAL_VDSL, true },
                { Technologie.TAL_VDSL, Technologie.TAL_ISDN, true },
                { Technologie.TAL_VDSL, Technologie.TAL_DSL, true },
                { Technologie.TAL_VDSL, Technologie.TAL_VDSL, true },
                { Technologie.KUPFER, Technologie.TAL_ISDN, true },
                { Technologie.KUPFER, Technologie.TAL_DSL, true },
                { Technologie.KUPFER, Technologie.TAL_VDSL, true },
                { Technologie.KUPFER, Technologie.FTTC, true },
                { Technologie.KUPFER_MX, Technologie.TAL_ISDN, true },
                { Technologie.KUPFER_MX, Technologie.TAL_DSL, true },
                { Technologie.KUPFER_MX, Technologie.TAL_VDSL, true },
                { Technologie.KUPFER_MX, Technologie.FTTC, true },
                { Technologie.FTTC, Technologie.FTTC, true },
                { Technologie.FTTC, Technologie.TAL_ISDN, true },
                { Technologie.FTTC, Technologie.KUPFER, false },
                { Technologie.SONSTIGES, Technologie.SONSTIGES, false },
        };
    }

    @Test(dataProvider = "isCompatibleToDataProvider")
    public void testIsCompatibleTo(Technologie technologieAbg, Technologie technologieMnet, boolean expected) {
        assertEquals(technologieAbg.isCompatibleTo(technologieMnet), expected);
    }

}
