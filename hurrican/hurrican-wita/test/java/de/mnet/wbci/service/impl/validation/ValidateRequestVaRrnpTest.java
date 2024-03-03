/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static de.mnet.wbci.service.impl.validation.ValidationTestUtil.*;

import java.util.*;
import javax.validation.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;
import de.mnet.wbci.model.builder.RufnummernblockTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.validation.groups.V1RequestVaRrnp;
import de.mnet.wbci.validation.groups.V1RequestVaRrnpWarn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateRequestVaRrnpTest extends ValidateRequestVa<WbciGeschaeftsfallRrnp> {
    private static final String VALID_PKI_AUF = "D012";

    @Override
    public Class<?> getErrorGroup() {
        return V1RequestVaRrnp.class;
    }

    @Override
    public Class<?> getWarnGroup() {
        return V1RequestVaRrnpWarn.class;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return VA_RRNP;
    }

    public Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> checkRequestForErrors(WbciCdmVersion wbciCdmVersion, VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest) {
        return super.checkMessageForErrors(wbciCdmVersion, wbciRequest, getErrorGroup());
    }

    public Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> checkRequestForWarnings(WbciCdmVersion wbciCdmVersion, VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest) {
        return super.checkMessageForWarnings(wbciCdmVersion, wbciRequest, getWarnGroup());
    }

    @Test
    public void testCheckRequestForErrorsWithMissingPortierungskennungPKIauf() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().getRufnummernportierung().setPortierungskennungPKIauf(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "portierungskennungPKIauf");
    }

    @Test
    public void testCheckRequestForErrorsWithWronglyFormattedPortierungskennungPKIauf() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().getRufnummernportierung().setPortierungskennungPKIauf("A01AB");

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 1);
        assertPatternViolation(violations, "portierungskennungPKIauf");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingZeitfenster() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().getRufnummernportierung().setPortierungszeitfenster(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "portierungszeitfenster");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingRufnummernportierung() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(null);

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "rufnummernportierung");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingAnlagenanschlussAttributes() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungAnlageBuilder()
                .withAbfragestelle(null)
                .withDurchwahlnummer(null)
                .withOnkz(null)
                .withRufnummernbloecke(null)
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 4);
        assertNotNullViolation(violations, "abfragestelle", "durchwahlnummer", "onkz", "rufnummernbloecke");
    }

    @Test
    public void testCheckRequestForErrorsWithTooLongAnlagenanschlussAttributes() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungAnlageBuilder()
                .withAbfragestelle(getIntegerOfLength(7).toString())
                .withDurchwahlnummer(getIntegerOfLength(9).toString())
                .withOnkz(getIntegerOfLength(6).toString())
                .withRufnummernbloecke(new RufnummernblockTestBuilder()
                        .withRnrBlockVon(getIntegerOfLength(7).toString())
                        .withRnrBlockBis(getIntegerOfLength(7).toString())
                        .buildValidList(V1, getGeschaeftsfallTyp(), 1))
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 5);
        assertPatternViolation(violations, "abfragestelle", "durchwahlnummer", "onkz", "rnrBlockVon", "rnrBlockBis");
    }

    @Test
    public void testCheckRequestForErrorsWithTooShortAnlagenanschlussAttributes() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungAnlageBuilder()
                .withAbfragestelle("")
                .withDurchwahlnummer("")
                .withOnkz(getIntegerOfLength(1).toString())
                .withRufnummernbloecke(new RufnummernblockTestBuilder()
                        .withRnrBlockVon("")
                        .withRnrBlockBis("")
                        .buildValidList(V1, getGeschaeftsfallTyp(), 1))
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 5);
        assertPatternViolation(violations, "abfragestelle", "durchwahlnummer", "onkz", "rnrBlockVon", "rnrBlockBis");
    }

    @Test
    public void testCheckRequestForErrorsWithTooManyRufnummernbloecke() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungAnlageBuilder()
                .withAbfragestelle(getIntegerOfLength(6).toString())
                .withDurchwahlnummer(getIntegerOfLength(8).toString())
                .withOnkz(getIntegerOfLength(5).toString())
                .withRufnummernbloecke(new RufnummernblockTestBuilder()
                        .withRnrBlockVon(getIntegerOfLength(6).toString())
                        .withRnrBlockBis(getIntegerOfLength(6).toString())
                        .buildValidList(V1, getGeschaeftsfallTyp(), 6))
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 1);
        assertSizeViolation(violations, "rufnummernbloecke");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingEinzelanschluss() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 1);
        assertNotNullViolation(violations, "rufnummernOnkz");
    }

    @Test
    public void testCheckRequestForErrorsWithMissingEinzelanschlussAttributes() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                .addRufnummer(new RufnummerOnkzTestBuilder()
                        .withRufnummer(null)
                        .withOnkz(null)
                        .build())
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 2);
        assertNotNullViolation(violations, "rufnummer", "onkz");
    }

    @Test
    public void testCheckRequestForErrorsWithTooLongEinzelanschlussAttributes() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                .addRufnummer(new RufnummerOnkzTestBuilder()
                        .withRufnummer(getLongOfLength(15).toString())
                        .withOnkz(getIntegerOfLength(6).toString())
                        .build())
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 2);
        assertPatternViolation(violations, "rufnummer", "onkz");
    }

    @Test
    public void testCheckRequestForErrorsWithTooShortEinzelanschlussAttributes() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                .addRufnummer(new RufnummerOnkzTestBuilder()
                        .withRufnummer("")
                        .withOnkz("9")
                        .build())
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 2);
        assertPatternViolation(violations, "rufnummer", "onkz");
    }

    @Test
    public void testCheckRequestForErrorsWithTooManyEinzelanschluss() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setRufnummernportierung(new RufnummernportierungEinzelnBuilder()
                .withRufnummerOnkzs(new RufnummerOnkzTestBuilder().buildValidList(V1, getGeschaeftsfallTyp(), 11))
                .withPortierungszeitfenster(Portierungszeitfenster.ZF1)
                .withPortierungskennungPKIauf(VALID_PKI_AUF)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 1);
        assertSizeViolation(violations, "rufnummernOnkz");
    }

    @Test
    public void testCheckRequestForErrorsWithKundenwunschterminIn0Days() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setKundenwunschtermin(DateCalculationHelper.getDateInWorkingDaysFromNow(0).toLocalDate());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp>>> violations = checkMessageForErrors(V1, wbciRequest, getErrorGroup());

        Assert.assertEquals(violations.size(), 1);
        assertKundenwunschterminIgnoringNextDayViolation(violations, "kundenwunschtermin");
    }
}
