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

import java.time.*;
import java.util.*;
import javax.validation.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.builder.PersonTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.validation.groups.V1RequestTvVaRrnp;
import de.mnet.wbci.validation.groups.V1RequestTvVaRrnpWarn;


@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateRequestTerminverschiebungVaRrnpTest extends ValidateBase {

    public Class<?> getErrorGroup() {
        return V1RequestTvVaRrnp.class;
    }

    public Class<?> getWarnGroup() {
        return V1RequestTvVaRrnpWarn.class;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VA_RRNP;
    }

    @Test
    public void testCheckRequestForErrorsWithValidRequest() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder<>()
                .withTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate())
                .buildValid(V1, getGeschaeftsfallTyp());

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 0);
    }

    @Test
    public void testCheckRequestForErrorsTvWithEndkunde() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder<>()
                .withEndkunde(new PersonTestBuilder().buildValid(V1, getGeschaeftsfallTyp()))
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
    public void testCheckRequestForErrorsWithKundenwunschterminIn0Days() throws Exception {
        TerminverschiebungsAnfrage wbciRequest = new TerminverschiebungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.setTvTermin(LocalDate.now()); // today

        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertKundenwunschterminIgnoringNextDayViolation(violations, "tvTermin");
    }
}
