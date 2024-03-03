/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;

@Test(groups = UNIT)
public class CheckOutgoingAbbmBegruendungTest extends AbstractValidatorTest<CheckOutgoingAbbmBegruendung.AbbmValidator> {

    @Override
    protected CheckOutgoingAbbmBegruendung.AbbmValidator createTestling() {
        return new CheckOutgoingAbbmBegruendung.AbbmValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @DataProvider(name = "meldungPos")
    public Object[][] validationErrors() {
        return new Object[][] {
                { MeldungsCode.RNG, null, true },
                { MeldungsCode.ZWA, null, true },
                { MeldungsCode.SONST, null, false },
                { MeldungsCode.SONST, "", false },
                { MeldungsCode.SONST, " ", false },
                { MeldungsCode.SONST, "BLA", true },
                { MeldungsCode.TV_ABG, "BLA", true },
                { MeldungsCode.TV_ABG, null, true },
                { MeldungsCode.STORNO_ABG, "BLA", true },
                { MeldungsCode.STORNO_ABG, null, true },
                { MeldungsCode.BVID, "BLA", true },
                { MeldungsCode.BVID, null, true },
                { MeldungsCode.NAT, "BLA", false },
        };
    }

    @Test(dataProvider = "meldungPos")
    public void testCheckKundenwunschtermin(MeldungsCode code, String begruendung, boolean valid) throws Exception {
        testling.defaultMsg = "%s";
        testling.msgMcSet = "%s";

        Abbruchmeldung abbm = new AbbruchmeldungTestBuilder()
                .withBegruendung(begruendung)
                .withMeldungsPositionen(new HashSet<>(Arrays.asList(
                        new MeldungPositionAbbruchmeldungBuilder().withMeldungsCode(code).build()
                ))).build();

        assertEquals(testling.isValid(abbm, contextMock), valid);
        assertErrorMessageSet(valid);

        //check for incoming messages
        reset(contextMock);
        abbm.setIoType(IOType.IN);
        assertTrue(testling.isValid(abbm, contextMock));
        assertErrorMessageSet(0);
    }

}
