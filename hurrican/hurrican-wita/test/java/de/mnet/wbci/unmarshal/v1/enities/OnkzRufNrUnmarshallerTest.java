/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrPortierungskennerType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.OnkzRufNrPortierungskennerTypeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.OnkzRufNrTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class OnkzRufNrUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private OnkzRufNrUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        OnkzRufNrType testdata = new OnkzRufNrTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        RufnummerOnkz result = testling.apply(testdata);
        Assert.assertEquals(result.getOnkz(), testdata.getONKZ());
        Assert.assertEquals(result.getRufnummer(), testdata.getRufnummer());
        Assert.assertNull(result.getPortierungskennungPKIabg());
    }

    @Test
    public void testApplyPortierungskennung() throws Exception {
        OnkzRufNrPortierungskennerType testdata = new OnkzRufNrPortierungskennerTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        RufnummerOnkz result = testling.apply(testdata);
        Assert.assertEquals(result.getOnkz(), testdata.getONKZ());
        Assert.assertEquals(result.getRufnummer(), testdata.getRufnummer());
        Assert.assertEquals(result.getPortierungskennungPKIabg(), testdata.getPortierungskennungPKIabg());

    }
}
