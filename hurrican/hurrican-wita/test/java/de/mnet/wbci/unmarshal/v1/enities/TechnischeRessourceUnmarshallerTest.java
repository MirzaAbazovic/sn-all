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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.TechnischeRessourceTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class TechnischeRessourceUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private TechnischeRessourceUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        TechnischeRessourceType testdata = new TechnischeRessourceTypeTestBuilder().buildValid(
                GeschaeftsfallEnumType.VA_KUE_MRN);
        TechnischeRessource result = testling.apply(testdata);

        Assert.assertEquals(result.getIdentifizierer(), testdata.getIdentifizierer());
        Assert.assertEquals(result.getLineId(), testdata.getLineID());
        Assert.assertEquals(result.getTnbKennungAbg(), testdata.getKennungTNBabg());
        Assert.assertEquals(result.getVertragsnummer(), testdata.getVertragsnummer());
    }
}
