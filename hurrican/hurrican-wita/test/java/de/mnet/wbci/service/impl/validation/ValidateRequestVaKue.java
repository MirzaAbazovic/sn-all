/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.13
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
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallKue;
import de.mnet.wbci.model.builder.StandortBuilder;
import de.mnet.wbci.model.builder.StrasseBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public abstract class ValidateRequestVaKue<T extends WbciGeschaeftsfallKue> extends ValidateRequestVa<T> {

    @Test
    public void testCheckRequestForErrorsWithMissingStandort() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder<T>().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setStandort(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "standort");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingStandortAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setStandort(new StandortBuilder()
                .withOrt(null)
                .withPostleitzahl(null)
                .withStrasse(null)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 3);
        assertNotEmptyViolation(violations, "ort", "postleitzahl");
        assertNotNullViolation(violations, "strasse");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingStandortStrasseAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().getStandort().setStrasse(new StrasseBuilder()
                .withHausnummer(null)
                .withHausnummernZusatz(null)
                .withStrassenname(null)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 2);
        assertNotEmptyViolation(violations, "strassenname", "hausnummer");
    }

    @Test
    public void testCheckRequestForErrorsWithTooLongStandortAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setStandort(new StandortBuilder()
                .withOrt(getStringOfLength(41))
                .withPostleitzahl(getStringOfLength(6))
                .withStrasse(new StrasseBuilder()
                        .withHausnummer(getIntegerOfLength(5).toString())
                        .withHausnummernZusatz(getStringOfLength(7))
                        .withStrassenname(getStringOfLength(41))
                        .build())
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 5);
        assertPatternViolation(violations, "postleitzahl");
        assertSizeViolation(violations, "ort", "strassenname", "hausnummernZusatz");
        assertPatternViolation(violations, "hausnummer");
    }

    @Test
    public void testCheckRequestForErrorsWithTooShortStandortAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setStandort(new StandortBuilder()
                .withOrt(" ")
                .withPostleitzahl(" ")
                .withStrasse(new StrasseBuilder()
                        .withHausnummer(" ")
                        .withHausnummernZusatz(" ")
                        .withStrassenname(" ")
                        .build())
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 4);
        assertNotEmptyViolation(violations, "ort", "strassenname", "postleitzahl", "hausnummer");
        Assert.assertNull(wbciRequest.getWbciGeschaeftsfall().getStandort().getStrasse().getHausnummernZusatz());
    }

    @Test
    public void testCheckRequestForErrorsWithKundenwunschterminIn6Days() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setKundenwunschtermin(DateCalculationHelper.getDateInWorkingDaysFromNow(6).toLocalDate()); // tomorrow plus 6

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertKundenwunschterminViolation(violations, "kundenwunschtermin");
    }

    @Test
    public void testCheckRequestForWarningsWithKundenwunschterminWithin11Days() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setKundenwunschtermin(DateCalculationHelper.getDateInWorkingDaysFromNow(11).toLocalDate());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForWarnings(V1, wbciRequest, getWarnGroup());

        Assert.assertEquals(violations.size(), 0);
    }

}
