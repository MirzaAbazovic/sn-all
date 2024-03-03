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
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.FirmaBuilder;
import de.mnet.wbci.model.builder.PersonBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public abstract class ValidateRequestVa<T extends WbciGeschaeftsfall> extends ValidateBase {

    @Test
    public void testCheckRequestForErrorsWithValidRequest() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder<T>().buildValid(V1, getGeschaeftsfallTyp());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 0);
    }

    @Test
    public void testCheckRequestForWarningsWithValidRequest() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder<T>().buildValid(V1, getGeschaeftsfallTyp());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForWarnings(V1, wbciRequest, getWarnGroup());

        Assert.assertEquals(violations.size(), 0);
    }

    @Test
    public void testCheckRequestForErrorsWithMissingKundenwunschtermin() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder<T>().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setKundenwunschtermin(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "kundenwunschtermin");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingEndkunde() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "endkunde");
    }


    @Test
    public void testCheckRequestForErrorsWithMissingPersonAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(new PersonBuilder()
                .withVorname(null)
                .withNachname(null)
                .withAnrede(null)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 2);
        assertNotEmptyViolation(violations, "nachname");
        assertNotNullViolation(violations, "anrede");
    }


    @Test
    public void testCheckRequestForErrorsWithTooLongPersonAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(new PersonBuilder()
                .withVorname(getStringOfLength(31))
                .withNachname(getStringOfLength(31))
                .withAnrede(Anrede.HERR)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 2);
        assertSizeViolation(violations, "nachname", "vorname");
    }


    @Test
    public void testCheckRequestForErrorsWithTooShortPersonAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(new PersonBuilder()
                .withVorname("")
                .withNachname("")
                .withAnrede(Anrede.HERR)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotEmptyViolation(violations, "nachname");
        assertNull((((Person) wbciRequest.getWbciGeschaeftsfall().getEndkunde()).getVorname()));
    }


    @Test
    public void testCheckRequestForErrorsWithTooShortFirmaAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(new FirmaBuilder()
                .withFirmename(" ")
                .withFirmennamenZusatz(" ")
                .withAnrede(Anrede.FIRMA)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotEmptyViolation(violations, "firmenname");
        assertNull((((Firma) wbciRequest.getWbciGeschaeftsfall().getEndkunde()).getFirmennamenZusatz()));
    }

    @Test
    public void testCheckRequestForErrorsWithMissingFirmaAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(new FirmaBuilder()
                .withFirmename(null)
                .withFirmennamenZusatz(null)
                .withAnrede(null)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 2);
        assertNotEmptyViolation(violations, "firmenname");
        assertNotNullViolation(violations, "anrede");
    }

    @Test
    public void testCheckRequestForErrorsWithTooLongFirmenAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(new FirmaBuilder()
                .withFirmename(getStringOfLength(31))
                .withFirmennamenZusatz(getStringOfLength(31))
                .withAnrede(Anrede.FIRMA)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 2);
        assertSizeViolation(violations, "firmenname", "firmennamenZusatz");
    }

    @Test
    public void testCheckRequestForErrorsWithTooShortFirmenAttributes() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setEndkunde(new FirmaBuilder()
                .withFirmename("")
                .withFirmennamenZusatz("")
                .withAnrede(Anrede.FIRMA)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotEmptyViolation(violations, "firmenname");
    }

    @Test
    public void testCheckRequestForErrorsWithTooLongProjektkenner() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().getProjekt().setProjektKenner(getStringOfLength(31));

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertSizeViolation(violations, "projektKenner");
    }

    @Test
    public void testCheckRequestForErrorsWithNoVaKundenwunschtermin() throws Exception {
        VorabstimmungsAnfrage<T> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.setVaKundenwunschtermin(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage<T>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "vaKundenwunschtermin");
    }
}
