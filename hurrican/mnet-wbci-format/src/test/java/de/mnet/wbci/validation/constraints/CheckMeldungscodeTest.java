/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceBuilder;
import de.mnet.wbci.model.builder.MeldungPositionBuilder;
import de.mnet.wbci.model.builder.MeldungPositionErledigtmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaBuilder;
import de.mnet.wbci.model.builder.MeldungPositionUebernahmeRessourceMeldungTestBuilder;

@Test(groups = UNIT)
public class CheckMeldungscodeTest extends AbstractValidatorTest<CheckMeldungscode.MeldungsCodeValidator> {

    @Override
    protected CheckMeldungscode.MeldungsCodeValidator createTestling() {
        return new CheckMeldungscode.MeldungsCodeValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @DataProvider(name = "meldungPos")
    public Object[][] validationErrors() {
        return new Object[][] {
                { new MeldungPositionAbbruchmeldungBuilder(), MeldungsCode.RNG, true },
                { new MeldungPositionAbbruchmeldungBuilder(), MeldungsCode.ZWA, false },
                { new MeldungPositionAbbruchmeldungTechnRessourceBuilder(), MeldungsCode.ZWA, false },
                { new MeldungPositionAbbruchmeldungTechnRessourceBuilder(), MeldungsCode.VAID_ABG, true },
                { new MeldungPositionUebernahmeRessourceMeldungTestBuilder(), MeldungsCode.AKMTR_CODE, true },
                { new MeldungPositionUebernahmeRessourceMeldungTestBuilder(), MeldungsCode.NAT, false },
                { new MeldungPositionRueckmeldungVaBuilder(), MeldungsCode.ZWA, true },
                { new MeldungPositionRueckmeldungVaBuilder(), MeldungsCode.STORNO_OK, false },
                { new MeldungPositionErledigtmeldungBuilder(), MeldungsCode.STORNO_OK, true },
                { new MeldungPositionErledigtmeldungBuilder(), MeldungsCode.ZWA, false },

        };
    }

    @Test(dataProvider = "meldungPos")
    public void testCheckKundenwunschtermin(MeldungPositionBuilder<?> builder, MeldungsCode code, boolean valid) throws Exception {
        testling.defaultMsg = "%s";

        MeldungPosition pos = builder.withMeldungsCode(code).build();
        Assert.assertEquals(testling.isValid(pos, contextMock), valid);
        assertErrorMessageSet(valid);
    }
}
