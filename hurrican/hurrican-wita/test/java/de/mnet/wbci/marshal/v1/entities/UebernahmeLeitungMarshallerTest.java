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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UebernahmeLeitungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.LeitungTestBuilder;

@Test(groups = BaseTest.UNIT)
public class UebernahmeLeitungMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private UebernahmeLeitungMarshaller testling;

    @Test
    public void testApply() throws Exception {
        Leitung leitung = new LeitungTestBuilder().buildValidLineId(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        UebernahmeLeitungType leitungType = testling.apply(leitung);
        Assert.assertEquals(leitungType.getLineID(), leitung.getLineId());
        Assert.assertNull(leitungType.getVertragsnummer());

        leitung = new LeitungTestBuilder().buildValidVertragsnummer(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        leitungType = testling.apply(leitung);
        Assert.assertEquals(leitungType.getVertragsnummer(), leitung.getVertragsnummer());
        Assert.assertNull(leitungType.getLineID());
    }
}
