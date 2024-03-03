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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ProjektIDType;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

@Test(groups = BaseTest.UNIT)
public class ProjektUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private ProjektUnmarshaller testling;

    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    public void testApplyProjekt() {
        ProjektIDType type = new ProjektIDType();
        type.setKopplungskenner("kopplung");
        type.setProjektkenner("projekt");

        Projekt result = testling.apply(type);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getKopplungsKenner(), type.getKopplungskenner());
        Assert.assertEquals(result.getProjektKenner(), type.getProjektkenner());
    }

}
