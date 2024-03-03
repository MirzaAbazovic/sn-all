/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.04.14
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.MeldungsCode.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static java.util.Arrays.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceTestBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceTestBuilder;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class MeldungTest {

    @DataProvider
    public Object[][] containsMeldungsCodesDataProvider() {
        return new Object[][] {
                { asList(UETN_NM, ADAHSNR, ADAORT), asList(UETN_NM, ADAHSNR, ADAORT), true },
                { asList(UETN_NM, ADAHSNR), asList(UETN_NM, ADAHSNR, ADAORT), false },
                { asList(UETN_NM, ADAHSNR, ADAORT), asList(UETN_NM), true },
                { asList(UETN_NM, ADAHSNR, ADAORT), asList(UETN_NM, ADFPLZ), false },
                { Collections.EMPTY_LIST, asList(UETN_NM, ADFPLZ), false },
        };
    }

    @Test(dataProvider = "containsMeldungsCodesDataProvider")
    public void testContainsMeldungsCodes(List<MeldungsCode> currentMeldungCodes, List<MeldungsCode> meldungCodes,
            boolean expected) {
        Set<MeldungPositionAbbruchmeldungTechnRessource> positions = new HashSet<>();
        for (MeldungsCode currentMeldungCode : currentMeldungCodes) {
            positions.add(
                    new MeldungPositionAbbruchmeldungTechnRessourceTestBuilder()
                            .withMeldungsCode(currentMeldungCode)
                            .buildValid(V1, VA_KUE_MRN)
            );
        }
        final AbbruchmeldungTechnRessource abbmtr = new AbbruchmeldungTechnRessourceTestBuilder()
                .withMeldungsPositionen(positions)
                .buildValid(V1, VA_KUE_MRN);
        assertEquals(abbmtr.containsMeldungsCodes((MeldungsCode[]) meldungCodes.toArray()), expected);
    }

    @DataProvider
    public Object[][] testHasADAMeldungsCodeDataProvider() {
        return new Object[][] {
                { asList(UETN_NM, ADAHSNR), true },
                { asList(UETN_NM), false },
                { asList(ADAHSNR), true },
                { Collections.EMPTY_LIST, false },
        };
    }

    @Test(dataProvider = "testHasADAMeldungsCodeDataProvider")
    public void testHasADAMeldungsCode(List<MeldungsCode> currentMeldungCodes, boolean expected) {
        Set<MeldungPositionRueckmeldungVa> positions = new HashSet<>();
        for (MeldungsCode currentMeldungCode : currentMeldungCodes) {
            positions.add(
                    new MeldungPositionRueckmeldungVaTestBuilder()
                            .withMeldungsCode(currentMeldungCode)
                            .buildValid(V1, VA_KUE_MRN)
            );
        }
        final RueckmeldungVorabstimmung abbmtr = new RueckmeldungVorabstimmungTestBuilder()
                .withMeldungsPositionen(positions)
                .buildValid(V1, VA_KUE_MRN);
        assertEquals(abbmtr.hasADAMeldungsCode(), expected);
    }

}
