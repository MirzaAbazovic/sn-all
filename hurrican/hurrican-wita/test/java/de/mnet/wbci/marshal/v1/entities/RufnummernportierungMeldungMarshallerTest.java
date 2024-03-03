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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnTestBuilder;

@Test(groups = BaseTest.UNIT)
public class RufnummernportierungMeldungMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private RufnummernportierungMeldungMarshaller testling;

    @Test
    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyEinzeln() throws Exception {
        RufnummernportierungEinzeln input = new RufnummernportierungEinzelnTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        RufnummernportierungMeldungType rufnummernportierungMeldungType = testling.apply(input);

        Assert.assertNull(rufnummernportierungMeldungType.getPortierungRufnummernbloecke());
        Assert.assertNotNull(rufnummernportierungMeldungType.getPortierungRufnummern());
        Assert.assertEquals(rufnummernportierungMeldungType.getPortierungRufnummern().getZuPortierendeOnkzRnr().size(), input.getRufnummernOnkz().size());
    }

    @Test
    public void testApplyAnlage() throws Exception {
        RufnummernportierungAnlage input = new RufnummernportierungAnlageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        RufnummernportierungMeldungType rufnummernportierungMeldungType = testling.apply(input);

        Assert.assertNull(rufnummernportierungMeldungType.getPortierungRufnummern());
        Assert.assertNotNull(rufnummernportierungMeldungType.getPortierungRufnummernbloecke());
        Assert.assertEquals(rufnummernportierungMeldungType.getPortierungRufnummernbloecke().getZuPortierenderRufnummernblock().size(), input.getRufnummernbloecke().size());
    }
}
