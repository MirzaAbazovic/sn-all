/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.EKPType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

@Test(groups = BaseTest.UNIT)
public class EKPGeschaeftsfallMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private EKPGeschaeftsfallMarshaller testling;

    @Test
    public void testApply() throws Exception {
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_MRN);
        EKPType ekp = testling.apply(wbciGeschaeftsfall);
        Assert.assertEquals(ekp.getEKPabg().getCarrierCode(), "DEU." + wbciGeschaeftsfall.getAbgebenderEKP().name());
        Assert.assertEquals(ekp.getEKPauf().getCarrierCode(), "DEU." + wbciGeschaeftsfall.getAufnehmenderEKP().name());
    }
}
