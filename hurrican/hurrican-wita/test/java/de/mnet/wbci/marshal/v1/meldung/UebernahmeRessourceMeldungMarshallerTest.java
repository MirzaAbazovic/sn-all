/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class UebernahmeRessourceMeldungMarshallerTest extends AbstractMeldungMarshallerTest {

    @Autowired
    private UebernahmeRessourceMeldungMarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyAkmTR() throws Exception {
        UebernahmeRessourceMeldung meldung = new UebernahmeRessourceMeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        MeldungAKMTRType akmtrType = testling.apply(meldung);

        assertEquals(akmtrType, meldung);

        Assert.assertEquals(akmtrType.getUebernahmeLeitung().size(), meldung.getLeitungen().size());
        Assert.assertEquals(akmtrType.getPosition().size(), meldung.getMeldungsPositionen().size());
    }
}
