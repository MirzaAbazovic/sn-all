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

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.validation.groups.V1MeldungTv;

/**
 *
 */
@Test(groups = UNIT)
public class ValidateAbbmTvTest extends ValidateAbbmTest {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungTv.class;
    }


    @DataProvider
    public Object[][] abbmBegruendungTv() {
        return abbmBegruendung();
    }

    @Test(dataProvider = "abbmBegruendungTv")
    @Override
    public void testCheckAbbmBegruendung(MeldungsCode[] codes, String begruendung, int constraints) throws Exception {
        super.testCheckAbbmBegruendung(codes, begruendung, constraints);
    }

    @Test
    public void testAenderungsIdRefNotEmpty() throws Exception {
        Abbruchmeldung abbm = new AbbruchmeldungTestBuilder().buildValid(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 0);
        abbm.setAenderungsIdRef("");
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 1);
    }

    @Test
    public void testWechselterminNotNull() throws Exception {
        Abbruchmeldung abbm = new AbbruchmeldungTestBuilder().buildValid(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 0);
        abbm.setWechseltermin(null);
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 1);
    }

    @Test
    public void testWechselterminEquals() throws Exception {
        Abbruchmeldung abbm = new AbbruchmeldungTestBuilder().buildValid(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 0);
        abbm.setWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(-10).toLocalDate());
        abbm.getWbciGeschaeftsfall().setWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate());
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, abbm, getErrorGroup()), 1);
    }
}
