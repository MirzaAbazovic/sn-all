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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummerOnkzTestBuilder;

@Test(groups = BaseTest.UNIT)
public class RufnummerOnkzMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private RufnummerOnkzMarshaller testling;

    @Test
    public void testApply() throws Exception {
        Assert.assertNull(new RufnummerOnkzMarshaller().apply(null));

        RufnummerOnkz rufnummerOnkz = new RufnummerOnkzTestBuilder().buildValid(WbciCdmVersion.V1,
                GeschaeftsfallTyp.VA_KUE_ORN);
        OnkzRufNrType onkzRufNrType = testling.apply(rufnummerOnkz);
        Assert.assertEquals(rufnummerOnkz.getOnkz(), onkzRufNrType.getONKZ());
        Assert.assertEquals(rufnummerOnkz.getRufnummer(), onkzRufNrType.getRufnummer());

    }
}
