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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.TechnischeRessourceTestBuilder;

@Test(groups = BaseTest.UNIT)
public class TechnischeRessourceMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private TechnischeRessourceMarshaller testling;

    @Test
    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApply() throws Exception {
        TechnischeRessource input = new TechnischeRessourceTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);

        TechnischeRessourceType technischeRessourceType = testling.apply(input);

        Assert.assertEquals(technischeRessourceType.getVertragsnummer(), input.getVertragsnummer());
        Assert.assertEquals(technischeRessourceType.getIdentifizierer(), input.getIdentifizierer());
        Assert.assertEquals(technischeRessourceType.getKennungTNBabg(), input.getTnbKennungAbg());
        Assert.assertEquals(technischeRessourceType.getLineID(), input.getLineId());
    }
}
