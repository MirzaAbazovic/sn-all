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
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.FirmaTestBuilder;
import de.mnet.wbci.model.builder.PersonTestBuilder;

@Test(groups = BaseTest.UNIT)
public class PersonOderFirmaMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private PersonOderFirmaMarshaller testling;
    @Autowired
    private AnredeMarshaller anredeMarshaller;

    @Test
    public void testGenerateFirma() throws Exception {

        Firma jpaFirma = new FirmaTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        PersonOderFirmaType generatedFirma = testling.apply(jpaFirma);
        assertEquals(anredeMarshaller.ANREDE_FIRMA, generatedFirma.getFirma().getAnrede());
        assertEquals(jpaFirma.getFirmenname(), generatedFirma.getFirma().getFirmenname());
        assertEquals(jpaFirma.getFirmennamenZusatz(), generatedFirma.getFirma().getFirmennameZweiterTeil());
        assertNull(generatedFirma.getPerson());
    }

    @Test
    public void testGeneratePerson() throws Exception {
        Person jpaPerson = new PersonTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        PersonOderFirmaType generatedPerson = testling.apply(jpaPerson);
        assertEquals(anredeMarshaller.ANREDE_HERR, generatedPerson.getPerson().getAnrede());
        assertEquals(jpaPerson.getNachname(), generatedPerson.getPerson().getNachname());
        assertEquals(jpaPerson.getVorname(), generatedPerson.getPerson().getVorname());
        assertNull(generatedPerson.getFirma());
    }
}
