/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.09.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.FirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonType;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.PersonOderFirmaTyp;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

@Test(groups = BaseTest.UNIT)
public class PersonOderFirmaUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private PersonOderFirmaUnmarshaller testling;

    public void testApplyNull() {
        Assert.assertNull(testling.apply(null));
    }

    public void testApplyPerson() throws Exception {
        PersonType person = new PersonType();
        person.setAnrede("1");
        person.setNachname("Nachname");
        person.setVorname("Vorname");

        PersonOderFirmaType type = new PersonOderFirmaType();
        type.setPerson(person);

        PersonOderFirma result = testling.apply(type);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getAnrede(), Anrede.HERR);
        Assert.assertEquals(result.getTyp(), PersonOderFirmaTyp.PERSON);
        Assert.assertEquals(((Person) result).getNachname(), person.getNachname());
        Assert.assertEquals(((Person) result).getVorname(), person.getVorname());
    }

    public void testApplyFirma() throws Exception {
        FirmaType firma = new FirmaType();
        firma.setAnrede("4");
        firma.setFirmenname("Firma");
        firma.setFirmennameZweiterTeil("Zusatz");

        PersonOderFirmaType type = new PersonOderFirmaType();
        type.setFirma(firma);

        PersonOderFirma result = testling.apply(type);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getAnrede(), Anrede.FIRMA);
        Assert.assertEquals(result.getTyp(), PersonOderFirmaTyp.FIRMA);
        Assert.assertEquals(((Firma) result).getFirmenname(), firma.getFirmenname());
        Assert.assertEquals(((Firma) result).getFirmennamenZusatz(), firma.getFirmennameZweiterTeil());
    }


}
