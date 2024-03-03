/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;

@Test(groups = BaseTest.UNIT)
public class AbbruchmeldungMarshallerTest extends AbstractMeldungMarshallerTest {

    @Autowired
    private AbbruchmeldungVaMarshaller abbruchmeldungMarshaller;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(abbruchmeldungMarshaller.apply(null));
    }

    @Test
    public void testApplyABBM() throws Exception {
        Abbruchmeldung meldung = new AbbruchmeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        MeldungABBMType abbmType = abbruchmeldungMarshaller.apply(meldung);

        assertEquals(abbmType, meldung);

        Assert.assertEquals(abbmType.getAenderungsIdRef(), meldung.getAenderungsIdRef());
        Assert.assertEquals(abbmType.getBegruendung(), meldung.getBegruendung());
        Assert.assertEquals(abbmType.getStornoIdRef(), meldung.getStornoIdRef());
        Assert.assertEquals(abbmType.getWechseltermin(), DateConverterUtils.toXmlGregorianCalendar(meldung.getWechseltermin()));
        Assert.assertEquals(abbmType.getPosition().size(), meldung.getMeldungsPositionen().size());
        if (abbmType.getPosition().size() > 0) {
            Assert.assertNotNull(abbmType.getPosition().get(0).getRufnummer());
        }
    }

}
