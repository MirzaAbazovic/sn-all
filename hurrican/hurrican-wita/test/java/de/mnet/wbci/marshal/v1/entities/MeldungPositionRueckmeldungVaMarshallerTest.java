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
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;

@Test(groups = BaseTest.UNIT)
public class MeldungPositionRueckmeldungVaMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private MeldungPositionRueckmeldungVaMarshaller testling;

    @Test
    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }
}
