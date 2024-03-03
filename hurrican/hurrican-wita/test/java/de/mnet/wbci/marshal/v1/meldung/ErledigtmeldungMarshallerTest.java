/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class ErledigtmeldungMarshallerTest extends AbstractMeldungMarshallerTest {

    @Autowired
    private ErledigtmeldungVaMarshaller erledigtmeldungMarshaller;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(erledigtmeldungMarshaller.apply(null));
    }

    @Test
    public void testApplyERLM() throws Exception {
        Erledigtmeldung testdata = new ErledigtmeldungTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        MeldungERLMType result = erledigtmeldungMarshaller.apply(testdata);
        assertEquals(result, testdata);
        Assert.assertEquals(result.getPosition().size(), testdata.getMeldungsPositionen().size());
        Assert.assertEquals(result.getAenderungsIdRef(), testdata.getAenderungsIdRef());
        Assert.assertEquals(result.getStornoIdRef(), testdata.getStornoIdRef());
        Assert.assertEquals(result.getWechseltermin(),
                DateConverterUtils.toXmlGregorianCalendar(testdata.getWechseltermin()));
    }

    @Test
    public void testApplyERLMWithoutWechseltermin() throws Exception {
        Erledigtmeldung testdata = new ErledigtmeldungTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        testdata.setWechseltermin(null);
        MeldungERLMType result = erledigtmeldungMarshaller.apply(testdata);
        assertEquals(result, testdata);
        Assert.assertEquals(result.getPosition().size(), testdata.getMeldungsPositionen().size());
        Assert.assertEquals(result.getAenderungsIdRef(), testdata.getAenderungsIdRef());
        Assert.assertEquals(result.getStornoIdRef(), testdata.getStornoIdRef());
        Assert.assertNull(result.getWechseltermin());
    }

}
