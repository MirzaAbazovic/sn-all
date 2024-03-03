/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrPortierungskennerType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;

@Test(groups = BaseTest.UNIT)
public class RufnummerOnkzPortierungMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private RufnummerOnkzPortierungMarshaller testling;

    @Test
    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        RufnummerOnkz input = new RufnummerOnkzTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        OnkzRufNrPortierungskennerType onkzType = testling.apply(input);

        Assert.assertEquals(onkzType.getPortierungskennungPKIabg(), input.getPortierungskennungPKIabg());
        Assert.assertEquals(onkzType.getONKZ(), input.getOnkz());
        Assert.assertEquals(onkzType.getRufnummer(), input.getRufnummer());
    }
}
