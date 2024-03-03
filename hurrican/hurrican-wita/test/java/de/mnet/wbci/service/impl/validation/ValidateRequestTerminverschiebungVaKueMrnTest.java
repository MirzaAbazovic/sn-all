/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static de.mnet.wbci.service.impl.validation.ValidationTestUtil.*;

import java.util.*;
import javax.validation.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrnWarn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateRequestTerminverschiebungVaKueMrnTest extends ValidateBase {

    public Class<?> getErrorGroup() {
        return V1RequestTvVaKueMrn.class;
    }

    public Class<?> getWarnGroup() {
        return V1RequestTvVaKueMrnWarn.class;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VA_KUE_MRN;
    }

    @Test
    public void testCheckRequestForErrorsWithValidRequest() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder<>()
                .withTvTermin(
                        DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(10))
                .buildValid(V1, getGeschaeftsfallTyp());

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 0);
    }

    @Test
    public void testCheckRequestForErrorsTvWithEndkunde() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder<>()
                .withEndkunde(new PersonTestBuilder().buildValid(V1, getGeschaeftsfallTyp()))
                .withTvTermin(
                        DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(10))
                .buildValid(V1, getGeschaeftsfallTyp());

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNullViolation(violations, "endkunde");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingKundenwunschtermin() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder<>().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.setTvTermin(null);

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "tvTermin");
    }

    @Test
    public void testCheckRequestForErrorsWithKundenwunschterminIn4Days() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.setTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNow(4).toLocalDate()); // tomorrow plus 4

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertKundenwunschterminViolation(violations, "tvTermin");
    }

    @Test
    public void testCheckRequestForWarningsWithKundenwunschterminWithin5Days() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.setTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNow(5).toLocalDate()); // tomorrow plus 5

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForWarnings(V1, wbciRequest, getWarnGroup());

        Assert.assertEquals(violations.size(), 1);
        assertKundenwunschterminNotInRangeViolation(violations, "tvTermin");
    }

    @Test
    public void testCheckRequestForWarningsWithKundenwunschterminWithin8Days() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.setTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNow(8).toLocalDate());

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForWarnings(V1, wbciRequest, getWarnGroup());

        Assert.assertEquals(violations.size(), 0);
    }
}
