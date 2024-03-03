/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.01.14
 */
package de.mnet.wbci.service.impl.validation;

import java.util.*;
import javax.validation.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;

/**
 *
 */
public abstract class ValidateAkmTr extends ValidateBase {

    @DataProvider
    public Object[][] pkiAuf() {
        // @formatter:off
        // [MeldungsCodeArray, valid]
        return new Object[][] {
                {"PKI-AUF", 0},
                {"", 1},
                {null, 1},
        };
        // @formatter:on
    }

    @Test(dataProvider = "pkiAuf")
    public void testCheckPkiAufEmpty(String pkiAuf, int constraints) throws Exception {

        UebernahmeRessourceMeldung meldung = new UebernahmeRessourceMeldungTestBuilder().buildValid(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        meldung.setPortierungskennungPKIauf(pkiAuf);

        Set<ConstraintViolation<UebernahmeRessourceMeldung>> constraintViolationSet = checkMessageForErrors(
                WbciCdmVersion.V1,
                meldung,
                getErrorGroup());
        assertConstraintViolationSet(constraintViolationSet, constraints);
    }
}
