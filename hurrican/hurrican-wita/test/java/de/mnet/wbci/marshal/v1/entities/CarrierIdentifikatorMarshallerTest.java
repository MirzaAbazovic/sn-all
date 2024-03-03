/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.CarrierCode;

@Test(groups = BaseTest.UNIT)
public class CarrierIdentifikatorMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    CarrierIdentifikatorMarshaller testling;

    @Test
    public void testApply() throws Exception {
        CarrierIdentifikatorType ci = testling.apply(CarrierCode.DTAG);
        Assert.assertEquals(ci.getCarrierCode(), "DEU." + CarrierCode.DTAG.name());

        CarrierIdentifikatorType ciMNet = testling.apply(CarrierCode.MNET);
        Assert.assertEquals(ciMNet.getCarrierCode(), "DEU." + CarrierCode.MNET.name());

    }
}
