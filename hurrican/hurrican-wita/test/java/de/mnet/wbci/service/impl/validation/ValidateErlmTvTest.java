/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.14
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;

import org.testng.annotations.Test;

import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.validation.groups.V1MeldungTv;

/**
 *
 */
@Test(groups = UNIT)
public class ValidateErlmTvTest extends ValidateBase {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungTv.class;
    }

    @Override
    public Class<?> getWarnGroup() {
        return null;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VA_KUE_MRN;
    }

    @Test
    public void testAenderungsIdRefNotEmpty() throws Exception {
        Erledigtmeldung erlm = new ErledigtmeldungTestBuilder()
                .buildValidForTv(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, erlm, getErrorGroup()), 0);
        erlm.setAenderungsIdRef("");
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, erlm, getErrorGroup()), 1);
    }

    @Test
    public void testWechselterminNotNull() throws Exception {
        ErledigtmeldungTerminverschiebung erlm = new ErledigtmeldungTestBuilder()
                .buildValidForTv(WbciCdmVersion.V1, getGeschaeftsfallTyp());
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, erlm, getErrorGroup()), 0);
        erlm.setWechseltermin(null);
        assertConstraintViolationSet(checkMessageForErrors(WbciCdmVersion.V1, erlm, getErrorGroup()), 1);
    }
}
