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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.StandortTestBuilder;

@Test(groups = BaseTest.UNIT)
public class AdresseAbweichendMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private AdresseAbweichendMarshaller testling;

    @Test
    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        Standort input = new StandortTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        AdresseAbweichendType adresseAbweichendType = testling.apply(input);

        Assert.assertEquals(adresseAbweichendType.getStrassenname(), input.getStrasse().getStrassenname());
        Assert.assertEquals(adresseAbweichendType.getHausnummer(), input.getStrasse().getHausnummer());
        Assert.assertEquals(adresseAbweichendType.getOrt(), input.getOrt());
        Assert.assertEquals(adresseAbweichendType.getPostleitzahl(), input.getPostleitzahl());
    }
}
