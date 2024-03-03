/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StrasseType;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

@Test(groups = BaseTest.UNIT)
public class StandortUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private StandortUnmarshaller testling;

    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    public void testApplyStandort() {
        StrasseType strasse = new StrasseType();
        strasse.setStrassenname("strasse");
        strasse.setHausnummer("999");
        strasse.setHausnummernZusatz("abc");

        StandortType type = new StandortType();
        type.setStrasse(strasse);
        type.setPostleitzahl("plz");
        type.setOrt("ort");

        Standort result = testling.apply(type);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getOrt(), type.getOrt());
        Assert.assertEquals(result.getPostleitzahl(), type.getPostleitzahl());
        Assert.assertEquals(result.getStrasse().getStrassenname(), strasse.getStrassenname());
        Assert.assertEquals(result.getStrasse().getHausnummer(), strasse.getHausnummer());
        Assert.assertEquals(result.getStrasse().getHausnummernZusatz(), strasse.getHausnummernZusatz());
    }

}
