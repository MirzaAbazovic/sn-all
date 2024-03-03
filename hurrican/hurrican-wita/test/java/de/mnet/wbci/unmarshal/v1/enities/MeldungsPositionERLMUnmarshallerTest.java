/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionERLMType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungsPositionERLMTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionERLMUnmarshallerTest {

    private MeldungsPositionERLMUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        testling = new MeldungsPositionERLMUnmarshaller();
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        testling = new MeldungsPositionERLMUnmarshaller();
        MeldungsPositionERLMType testdata = new MeldungsPositionERLMTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        MeldungPosition result = testling.apply(testdata);
        Assert.assertEquals(result.getMeldungsText(), testdata.getMeldungstext());
        Assert.assertEquals(result.getMeldungsCode().getCode(), testdata.getMeldungscode());
    }
}
