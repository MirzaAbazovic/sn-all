/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockPortierungskennerType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RufnummernblockPortierungskennerMeldungTypeTestBuilder;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.RufnummernblockTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RufnummernblockUnmarshallerTest extends AbstractWbciUnmarshallerContext {
    @Autowired
    private RufnummernblockUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        RufnummernblockType rufnummernblockType = new RufnummernblockTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        Rufnummernblock result = testling.apply(rufnummernblockType);
        Assert.assertEquals(result.getRnrBlockBis(), rufnummernblockType.getRnrBlockBis());
        Assert.assertEquals(result.getRnrBlockVon(), rufnummernblockType.getRnrBlockVon());
        Assert.assertNull(result.getPortierungskennungPKIabg());
    }

    @Test
    public void testApplyRufnummernblockPortierungskenner() throws Exception {
        RufnummernblockPortierungskennerType rufnummernblockType = new RufnummernblockPortierungskennerMeldungTypeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
        Rufnummernblock result = testling.apply(rufnummernblockType);
        Assert.assertEquals(result.getRnrBlockBis(), rufnummernblockType.getRnrBlockBis());
        Assert.assertEquals(result.getRnrBlockVon(), rufnummernblockType.getRnrBlockVon());
        Assert.assertEquals(result.getPortierungskennungPKIabg(), rufnummernblockType.getPortierungskennungPKIabg());
    }
}
