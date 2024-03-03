/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTestBuilder;

@Test(groups = UNIT)
public class AbbruchmeldungTest {
    @Test
    public void testIsAbbruchmeldungForVorabstimmung() throws Exception {
        Assert.assertTrue(new Abbruchmeldung().isAbbruchmeldungForVorabstimmung());
    }

    @Test
    public void testIsNotAbbruchmeldungForVorabstimmung() throws Exception {
        Assert.assertFalse(new AbbruchmeldungStornoAen().isAbbruchmeldungForVorabstimmung());
        Assert.assertFalse(new AbbruchmeldungStornoAuf().isAbbruchmeldungForVorabstimmung());
        Assert.assertFalse(new AbbruchmeldungTerminverschiebung().isAbbruchmeldungForVorabstimmung());
    }

    @DataProvider
    public Object[][] isMeldungWithOnlyADFCodesDataProvider() {
        return new Object[][] {
                { true, Arrays.asList(MeldungsCode.ADFORT) },
                { false, Arrays.asList(MeldungsCode.ADFORT, MeldungsCode.NAT) },
                { false, Arrays.asList(MeldungsCode.NAT) },
        };
    }

    @Test(dataProvider = "isMeldungWithOnlyADFCodesDataProvider")
    public void testIsMeldungWithOnlyADFCodes(boolean expected, List<MeldungsCode> meldungsCodes) throws Exception {
        Abbruchmeldung abbm = create((MeldungsCode[]) meldungsCodes.toArray());
        assertEquals(abbm.isMeldungWithOnlyADFCodes(), expected);
    }

    private static Abbruchmeldung create(MeldungsCode ... meldungsCode) {
        AbbruchmeldungBuilder abbmBuilder = new AbbruchmeldungBuilder();
        for (MeldungsCode code :meldungsCode) {
            MeldungPositionAbbruchmeldung mp = new MeldungPositionAbbruchmeldungTestBuilder()
                    .withMeldungsCode(code)
                    .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
            abbmBuilder.addMeldungPosition(mp);
        }
        return abbmBuilder.build();
    }
}
