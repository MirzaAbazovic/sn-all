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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungRufnummernMeldungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;

@Test(groups = BaseTest.UNIT)
public class PortierungRufnummernEinzelnMeldungMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private PortierungRufnummernEinzelnMeldungMarshaller testling;

    @Test
    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        RufnummernportierungEinzeln input = new RufnummernportierungEinzelnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        PortierungRufnummernMeldungType portierungRufnummernMeldungType = testling.apply(input);

        Assert.assertEquals(portierungRufnummernMeldungType.getZuPortierendeOnkzRnr().size(), input.getRufnummernOnkz().size());
    }
}
