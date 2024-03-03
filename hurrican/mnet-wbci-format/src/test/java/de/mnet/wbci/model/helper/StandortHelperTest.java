/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.14
 */
package de.mnet.wbci.model.helper;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.Strasse;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;

/**
 *
 */
public class StandortHelperTest {

    @DataProvider
    public Object[][] standort() {
        final Strasse emptyStrasse = new StrasseBuilder().build();
        final Strasse strasseWithHausnummer = new StrasseBuilder().withHausnummer("4").build();
        final Strasse strasseWithStrassenname = new StrasseBuilder().withStrassenname("Strasse").build();
        return new Object[][] {
                // [valid,  MeldungsCode , ort, plz, strasse]
                { true, MeldungsCode.ADAORT, "Ort", "", null },
                { false, MeldungsCode.ADAORT, "", "   ", null },
                { false, MeldungsCode.ADAORT, "Ort", "81673", null },
                { false, MeldungsCode.ADAORT, "Ort", "", emptyStrasse },
                { true, MeldungsCode.ADAPLZ, "", "81673", null },
                { false, MeldungsCode.ADAPLZ, "", "", null },
                { false, MeldungsCode.ADAPLZ, "Ort", "81673", null },
                { false, MeldungsCode.ADAPLZ, "", "81673", emptyStrasse },
                { true, MeldungsCode.ADAHSNR, "", "", strasseWithHausnummer },
                { false, MeldungsCode.ADAHSNR, "", "", null },
                { false, MeldungsCode.ADAHSNR, "", "", emptyStrasse },
                { false, MeldungsCode.ADAHSNR, "", "", strasseWithStrassenname },
                { true, MeldungsCode.ADASTR, "", "", strasseWithStrassenname },
                { false, MeldungsCode.ADASTR, "", "", null },
                { false, MeldungsCode.ADASTR, "", "", emptyStrasse },
                { false, MeldungsCode.ADASTR, "", "", strasseWithHausnummer },
        };
    }

    @Test(dataProvider = "standort")
    public void testIsStandortValidForMeldungscode(boolean valid, MeldungsCode meldungsCode, String ort, String plz,
            Strasse str) throws Exception {
        Standort standort = new StandortBuilder()
                .withOrt(ort)
                .withPostleitzahl(plz)
                .withStrasse(str)
                .build();

        Assert.assertEquals(valid, StandortHelper.isStandortValidForMeldungscode(standort, meldungsCode));
    }
}
