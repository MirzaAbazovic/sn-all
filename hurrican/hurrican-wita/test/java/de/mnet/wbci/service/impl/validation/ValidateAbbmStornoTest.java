/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.14
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.validation.groups.V1MeldungStorno;

/**
 *
 */
@Test(groups = UNIT)
public class ValidateAbbmStornoTest extends ValidateAbbmTest {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungStorno.class;
    }

    @DataProvider
    public Object[][] abbmBegruendungStorno() {
        // @formatter:off
        // [MeldungsCodeArray, valid]
        return new Object[][] {
                {new MeldungsCode[]{MeldungsCode.SONST, MeldungsCode.ADAHSNR},null, 2},
                {new MeldungsCode[]{MeldungsCode.SONST, MeldungsCode.ADFORT},null, 1},
                {new MeldungsCode[]{MeldungsCode.SONST, MeldungsCode.ADFORT},"BAL",0},
                {new MeldungsCode[]{MeldungsCode.SONST},"BAL",0},
                {new MeldungsCode[]{MeldungsCode.ADFORT}, null, 1}  ,
                {new MeldungsCode[]{MeldungsCode.ADFORT}, "", 1}  ,
                {new MeldungsCode[]{MeldungsCode.ADFORT},"BAL",0},
        };
        // @formatter:on
    }

    @Test(dataProvider = "abbmBegruendungStorno")
    @Override
    public void testCheckAbbmBegruendung(MeldungsCode[] codes, String begruendung, int constraints) throws Exception {
        super.testCheckAbbmBegruendung(codes, begruendung, constraints);
    }

    @Test
    public void testStronoIdRefNotEmpty() throws Exception {
        Abbruchmeldung abbm = new AbbruchmeldungTestBuilder().buildValid(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 0);
        abbm.setStornoIdRef("");
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 1);
    }
}
