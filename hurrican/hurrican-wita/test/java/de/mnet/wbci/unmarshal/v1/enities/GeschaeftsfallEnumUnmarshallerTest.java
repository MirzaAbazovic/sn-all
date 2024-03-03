/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class GeschaeftsfallEnumUnmarshallerTest extends AbstractWbciUnmarshallerContext {
    @Autowired
    private GeschaeftsfallEnumUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        for (GeschaeftsfallTyp geschaeftsfallTyp : GeschaeftsfallTyp.values()) {
            if (geschaeftsfallTyp != GeschaeftsfallTyp.VA_UNBEKANNT) {
                GeschaeftsfallEnumType geschaeftsfallEnumType = GeschaeftsfallEnumType.fromValue(geschaeftsfallTyp
                        .name().replace("_", "-"));
                Assert.assertEquals(geschaeftsfallTyp, testling.apply(geschaeftsfallEnumType));
            }
        }

    }
}
