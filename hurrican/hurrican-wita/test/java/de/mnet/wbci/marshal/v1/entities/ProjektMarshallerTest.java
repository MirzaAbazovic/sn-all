/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ProjektIDType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.ProjektTestBuilder;

@Test(groups = BaseTest.UNIT)
public class ProjektMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private ProjektMarshaller testling;

    @Test
    public void testApply() throws Exception {
        Projekt testProjekt = new ProjektTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        ProjektIDType generatedProjekt = testling.apply(testProjekt);

        assertEquals(testProjekt.getKopplungsKenner(), generatedProjekt.getKopplungskenner());
        assertEquals(testProjekt.getProjektKenner(), generatedProjekt.getProjektkenner());

        // check nulls
        assertNull(testling.apply(null));
        testProjekt.setKopplungsKenner("");
        assertEquals(testling.apply(testProjekt).getKopplungskenner(), "");
        testProjekt.setKopplungsKenner(null);
        assertNull(testling.apply(testProjekt).getKopplungskenner());

    }
}
