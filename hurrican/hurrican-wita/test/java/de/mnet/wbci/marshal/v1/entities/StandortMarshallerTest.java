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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.StandortTestBuilder;

@Test(groups = BaseTest.UNIT)
public class StandortMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private StandortMarshaller testling;

    @Test
    public void testGenerate() throws Exception {
        Standort jpaStandort = new StandortTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        StandortType generatedStandort = testling.apply(jpaStandort);

        assertEquals(jpaStandort.getOrt(), generatedStandort.getOrt());
        assertEquals(jpaStandort.getPostleitzahl(), generatedStandort.getPostleitzahl());
        assertEquals(jpaStandort.getStrasse().getHausnummer(), generatedStandort.getStrasse().getHausnummer());
        assertEquals(jpaStandort.getStrasse().getHausnummernZusatz(), generatedStandort.getStrasse()
                .getHausnummernZusatz());
        assertEquals(jpaStandort.getStrasse().getStrassenname(), generatedStandort.getStrasse().getStrassenname());

        // Check if nulls are okay
        jpaStandort.getStrasse().setHausnummernZusatz(null);
        StandortType generatedStandortWithNull = testling.apply(jpaStandort);
        assertNull(generatedStandortWithNull.getStrasse().getHausnummernZusatz());
    }
}
