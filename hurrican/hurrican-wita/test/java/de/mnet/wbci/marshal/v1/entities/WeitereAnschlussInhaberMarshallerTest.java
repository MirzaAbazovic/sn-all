/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.FirmaTestBuilder;
import de.mnet.wbci.model.builder.PersonTestBuilder;

@Test(groups = BaseTest.UNIT)
public class WeitereAnschlussInhaberMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private WeitereAnschlussInhaberMarshaller testling;

    @Test
    public void testAplly() throws Exception {
        Assert.assertNull(new WeitereAnschlussInhaberMarshaller().apply(null));

        List<PersonOderFirmaType> weitereAnschluesse = new WeitereAnschlussInhaberMarshaller()
                .apply(new ArrayList<>());
        assertEquals(weitereAnschluesse.size(), 0);

        List<PersonOderFirma> jpaList = new ArrayList<>();
        jpaList.add(new PersonTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));
        jpaList.add(new FirmaTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));

        List<PersonOderFirmaType> generatedList = testling.apply(jpaList);
        Assert.assertNotNull(generatedList.get(0));
        Assert.assertNotNull(generatedList.get(1));
        Assert.assertEquals(generatedList.size(), 2);
    }

    public void testApplyMax() {
        // create the maximal valid array of maxAnschlussinhaber (MAX = 99)
        List<PersonOderFirma> maxAnschlussinhaber = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            if (i % 2 == 0) {
                maxAnschlussinhaber
                        .add(new PersonTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));
            }
            else {
                maxAnschlussinhaber.add(new FirmaTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));
            }
        }
        List<PersonOderFirmaType> generatedList = testling.apply(maxAnschlussinhaber);
        Assert.assertEquals(generatedList.size(), 99);
        for (PersonOderFirmaType p : generatedList) {
            Assert.assertNotNull(p);
        }
    }
}
