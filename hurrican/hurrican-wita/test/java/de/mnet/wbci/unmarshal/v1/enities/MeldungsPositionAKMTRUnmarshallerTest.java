/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionAKMTRType;
import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungsPositionAKMTRTypeTestBuilder;

/**
 *
 */
public class MeldungsPositionAKMTRUnmarshallerTest {

    private MeldungsPositionAKMTRUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        testling = new MeldungsPositionAKMTRUnmarshaller();
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        testling = new MeldungsPositionAKMTRUnmarshaller();
        MeldungsPositionAKMTRType testdata = new MeldungsPositionAKMTRTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        MeldungPosition result = testling.apply(testdata);
        Assert.assertEquals(result.getMeldungsText(), testdata.getMeldungstext());
        Assert.assertEquals(result.getMeldungsCode().getCode(), testdata.getMeldungscode());
    }

}
