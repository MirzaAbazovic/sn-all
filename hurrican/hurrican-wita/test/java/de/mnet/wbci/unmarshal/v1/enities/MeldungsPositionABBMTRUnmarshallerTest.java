/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionABBMTRType;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldungTechnRessource;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungsPositionABBMTRTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionABBMTRUnmarshallerTest {

    private MeldungsPositionABBMTRUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        testling = new MeldungsPositionABBMTRUnmarshaller();
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        testling = new MeldungsPositionABBMTRUnmarshaller();
        MeldungsPositionABBMTRType testdata = new MeldungsPositionABBMTRTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        MeldungPositionAbbruchmeldungTechnRessource result = testling.apply(testdata);
        Assert.assertEquals(result.getMeldungsText(), testdata.getMeldungstext());
        Assert.assertEquals(result.getMeldungsCode().getCode(), testdata.getMeldungscode());
    }
}
