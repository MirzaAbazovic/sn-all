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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernbloeckeMeldungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class PortierungRufnummernBloeckeMeldungMarschallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private PortierungRufnummernBloeckeMeldungMarschaller testling;

    @Test
    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        RufnummernportierungAnlage input = new RufnummernportierungAnlageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        PortierungRufnummernbloeckeMeldungType rufnummernbloeckeMeldungType = testling.apply(input);

        Assert.assertNotNull(rufnummernbloeckeMeldungType.getOnkzDurchwahlAbfragestelle());
        Assert.assertEquals(rufnummernbloeckeMeldungType.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer(), input.getDurchwahlnummer());
        Assert.assertEquals(rufnummernbloeckeMeldungType.getOnkzDurchwahlAbfragestelle().getONKZ(), input.getOnkz());
        Assert.assertEquals(rufnummernbloeckeMeldungType.getOnkzDurchwahlAbfragestelle().getAbfragestelle(), input.getAbfragestelle());

        Assert.assertEquals(rufnummernbloeckeMeldungType.getZuPortierenderRufnummernblock().size(), input.getRufnummernbloecke().size());
    }
}
