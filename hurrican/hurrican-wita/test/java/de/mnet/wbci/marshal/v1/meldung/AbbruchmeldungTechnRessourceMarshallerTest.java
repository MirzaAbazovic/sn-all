/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceTestBuilder;

@Test(groups = BaseTest.UNIT)
public class AbbruchmeldungTechnRessourceMarshallerTest extends AbstractMeldungMarshallerTest {

    @Autowired
    private AbbruchmeldungTechnRessourceMarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyABBMTR() throws Exception {
        AbbruchmeldungTechnRessource meldung = new AbbruchmeldungTechnRessourceTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        MeldungABBMTRType abbmType = testling.apply(meldung);
        assertEquals(abbmType, meldung);
        Assert.assertEquals(abbmType.getPosition().size(), meldung.getMeldungsPositionen().size());
        if (abbmType.getPosition().size() > 0) {
            Assert.assertNotNull(abbmType.getPosition().get(0).getMeldungscode());
            Assert.assertNotNull(abbmType.getPosition().get(0).getMeldungstext());
        }
    }

}
