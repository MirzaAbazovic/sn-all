/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;

@Test(groups = BaseTest.UNIT)
public class EKPMeldungMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private EKPMeldungMarshaller testling;

    @Test
    public void testApply() throws Exception {
        RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        EKPType ekp = testling.apply(meldung);
        Assert.assertEquals(ekp.getEKPabg().getCarrierCode(), "DEU." + meldung.getWbciGeschaeftsfall().getAbgebenderEKP().name());
        Assert.assertEquals(ekp.getEKPauf().getCarrierCode(), "DEU." + meldung.getWbciGeschaeftsfall().getAufnehmenderEKP().name());
    }

}
