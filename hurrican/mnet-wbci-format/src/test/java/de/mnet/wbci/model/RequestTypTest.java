/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.13
 */
package de.mnet.wbci.model;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = UNIT)
public class RequestTypTest {
    @Test
    public void testBuildFromName() throws Exception {
        String[] names = {
                RequestTyp.VA.name(),
                RequestTyp.STR_AUFH_ABG.name(),
                RequestTyp.STR_AUFH_AUF.name(),
                RequestTyp.STR_AEN_ABG.name(),
                RequestTyp.STR_AEN_AUF.name(),
                RequestTyp.TV.name()
        };

        for (String name : names) {
            RequestTyp requestTyp = RequestTyp.buildFromName(name);
            Assert.assertNotNull(requestTyp);
            Assert.assertFalse(RequestTyp.UNBEKANNT.equals(requestTyp));
        }
    }

    @Test
    public void testBuildFromShortName() throws Exception {
        String[] names = {
                RequestTyp.VA.getShortName(),
                RequestTyp.STR_AUFH_ABG.getShortName(),
                RequestTyp.STR_AUFH_AUF.getShortName(),
                RequestTyp.STR_AEN_ABG.getShortName(),
                RequestTyp.STR_AEN_AUF.getShortName(),
                RequestTyp.TV_NAME
        };

        for (String name : names) {
            RequestTyp requestTyp = RequestTyp.buildFromShortName(name);
            Assert.assertNotNull(requestTyp);
            Assert.assertFalse(RequestTyp.UNBEKANNT.equals(requestTyp));
        }
    }

    @Test
    public void testBuildFromEmptyNullOrUnknownNames() throws Exception {
        String[] names = {
                null,
                "",
                "SomeInvalidName"
        };

        for (String name : names) {
            Assert.assertNotNull(RequestTyp.buildFromShortName(name));
            Assert.assertEquals(RequestTyp.UNBEKANNT, RequestTyp.buildFromShortName(name));

            Assert.assertNotNull(RequestTyp.buildFromName(name));
            Assert.assertEquals(RequestTyp.UNBEKANNT, RequestTyp.buildFromName(name));

        }
    }

    @DataProvider
    public Object[][] stornoDataProvider() {
        // @formatter:off
        return new Object[][] {
                { RequestTyp.STR_AUFH_AUF, true,  false, true},
                { RequestTyp.STR_AUFH_ABG, true,  false, true},
                { RequestTyp.STR_AEN_AUF,  true,  true,  false},
                { RequestTyp.STR_AEN_ABG,  true,  true,  false},
                { RequestTyp.TV,           false, false, false},
                { RequestTyp.VA,           false, false, false},
                { RequestTyp.UNBEKANNT,    false, false, false},
        };
        // @formatter:on
    }

    @Test(dataProvider = "stornoDataProvider")
    public void testIsStorno(RequestTyp typ, boolean isStorno, boolean isStornoAen, boolean isStornoAuf) throws Exception {
        Assert.assertEquals(typ.isStorno(), isStorno);
        Assert.assertEquals(typ.isStornoAenderung(), isStornoAen);
        Assert.assertEquals(typ.isStornoAufhebung(), isStornoAuf);
    }


}
