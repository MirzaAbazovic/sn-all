/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.MeldungPositionUebernahmeRessourceMeldungTestBuilder;

@Test(groups = BaseTest.UNIT)
public class MeldungPositionUebernahmeRessourceMeldungMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private MeldungPositionUebernahmeRessourceMeldungMarshaller testling;

    @Test
    public void testNullable() throws Exception {
        Assert.assertNull(null);

    }

    @Test
    public void testApply() throws Exception {
        MeldungPositionUebernahmeRessourceMeldung testdata = new MeldungPositionUebernahmeRessourceMeldungTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        MeldungsPositionAKMTRType result = testling.apply(testdata);
        Assert.assertEquals(result.getMeldungscode(), testdata.getMeldungsCode().getCode());
        Assert.assertEquals(result.getMeldungstext(), testdata.getMeldungsCode().getStandardText());
    }
}
