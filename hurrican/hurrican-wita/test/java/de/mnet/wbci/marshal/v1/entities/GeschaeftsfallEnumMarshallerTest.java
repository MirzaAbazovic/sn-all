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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;

@Test(groups = BaseTest.UNIT)
public class GeschaeftsfallEnumMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private GeschaeftsfallEnumMarshaller testling;

    @Test
    public void testApply() throws Exception {
        Assert.assertEquals(testling.apply(GeschaeftsfallTyp.VA_KUE_MRN), GeschaeftsfallEnumType.VA_KUE_MRN);
        Assert.assertEquals(testling.apply(GeschaeftsfallTyp.VA_KUE_ORN), GeschaeftsfallEnumType.VA_KUE_ORN);
        Assert.assertEquals(testling.apply(GeschaeftsfallTyp.VA_RRNP), GeschaeftsfallEnumType.VA_RRNP);
        Assert.assertNull(testling.apply(GeschaeftsfallTyp.VA_UNBEKANNT));
    }
}
