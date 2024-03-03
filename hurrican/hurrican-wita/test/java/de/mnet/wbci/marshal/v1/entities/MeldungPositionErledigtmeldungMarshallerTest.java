/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionErledigtmeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.MeldungPositionErledigtmeldungTestBuilder;

@Test(groups = BaseTest.UNIT)
public class MeldungPositionErledigtmeldungMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private MeldungPositionErledigtmeldungMarshaller testling;

    @Test
    public void testNullable() throws Exception {
        Assert.assertNull(null);

    }

    @Test
    public void testApply() throws Exception {
        MeldungPositionErledigtmeldung testdata = new MeldungPositionErledigtmeldungTestBuilder().buildValid(
                WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        MeldungsPositionERLMType result = testling.apply(testdata);
        Assert.assertEquals(result.getMeldungscode(), testdata.getMeldungsCode().getCode());
        Assert.assertEquals(result.getMeldungstext(), testdata.getMeldungsCode().getStandardText());
    }
}
