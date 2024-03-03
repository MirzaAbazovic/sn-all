/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.CarrierIdentifikatorType;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CarrierIdentifikatorUnmarshallerTest extends AbstractWbciUnmarshallerContext {
    @Autowired
    private CarrierIdentifikatorUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        for (CarrierCode carrier : CarrierCode.values()) {
            CarrierIdentifikatorType carrierIdentifikatorType = new CarrierIdentifikatorType();
            carrierIdentifikatorType.setCarrierCode(carrier.getITUCarrierCode());
            Assert.assertEquals(carrier, testling.apply(carrierIdentifikatorType));
        }
    }
}
