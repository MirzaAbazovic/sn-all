/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
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

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrnWarn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateRequestVaKueOrnTest extends ValidateRequestVaKue<WbciGeschaeftsfallKueOrn> {

    public Class<?> getErrorGroup() {
        return V1RequestVaKueOrn.class;
    }

    public Class<?> getWarnGroup() {
        return V1RequestVaKueOrnWarn.class;
    }

    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return VA_KUE_ORN;
    }

    public Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn>>> checkRequestForErrors(WbciCdmVersion wbciCdmVersion, VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn> wbciRequest) {
        return super.checkMessageForErrors(wbciCdmVersion, wbciRequest, getErrorGroup());
    }

    public Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn>>> checkRequestForWarnings(WbciCdmVersion wbciCdmVersion, VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn> wbciRequest) {
        return super.checkMessageForWarnings(wbciCdmVersion, wbciRequest, getWarnGroup());
    }

    @Test
    public void testCheckRequestForErrorsWithMissingRufnummerOnkz() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setAnschlussIdentifikation(new RufnummerOnkzBuilder()
                .withOnkz(null)
                .withRufnummer(null)
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 2);
        assertNotNullViolation(violations, "rufnummer", "onkz");
    }

    @Test
    public void testCheckRequestForErrorsWithTooLongRufnummerOnkz() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setAnschlussIdentifikation(new RufnummerOnkzBuilder()
                .withRufnummer(getLongOfLength(15).toString())
                .withOnkz(getIntegerOfLength(6).toString())
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 2);
        assertPatternViolation(violations, "rufnummer", "onkz");
    }

    @Test
    public void testCheckRequestForErrorsWithTooShortRufnummerOnkz() throws Exception {
        VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn> wbciRequest = new VorabstimmungsAnfrageTestBuilder().buildValid(V1, getGeschaeftsfallTyp());
        wbciRequest.getWbciGeschaeftsfall().setAnschlussIdentifikation(new RufnummerOnkzBuilder()
                .withRufnummer("")
                .withOnkz("9")
                .build());

        Set<ConstraintViolation<VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn>>> violations = checkRequestForErrors(V1, wbciRequest);

        Assert.assertEquals(violations.size(), 2);
        assertPatternViolation(violations, "rufnummer", "onkz");
    }


}
