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

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;

@Test(groups = UNIT)
public class CheckRuemVaMeldungscodeTest extends AbstractValidatorTest<CheckRuemVaMeldungscodes.MeldungscodesValidator> {

    @Override
    protected CheckRuemVaMeldungscodes.MeldungscodesValidator createTestling() {
        return new CheckRuemVaMeldungscodes.MeldungscodesValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s";
    }

    @DataProvider(name = "meldungPos")
    public Object[][] validationErrors() {
        return new Object[][] {
                { new MeldungsCode[] { MeldungsCode.NAT, MeldungsCode.ZWA }, false },
                { new MeldungsCode[] { MeldungsCode.ADAORT, MeldungsCode.ADFHSNR }, false },
                { new MeldungsCode[] { MeldungsCode.NAT, MeldungsCode.ADFHSNR }, true },
                { new MeldungsCode[] { MeldungsCode.ADFHSNR, MeldungsCode.ZWA }, true },
        };
    }

    @Test(dataProvider = "meldungPos")
    public void testCheckKundenwunschtermin(MeldungsCode[] codes, boolean valid) throws Exception {
        RueckmeldungVorabstimmungTestBuilder builder = new RueckmeldungVorabstimmungTestBuilder();
        for (MeldungsCode code : codes) {
            builder.addMeldungPosition(new MeldungPositionRueckmeldungVaTestBuilder().withMeldungsCode(code).build());
        }

        Assert.assertEquals(testling.isValid(builder.buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), contextMock), valid);
        assertErrorMessageSet(valid);
    }

}
