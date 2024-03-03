/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.13
 */
package de.mnet.wbci.converter;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CarrierKennungBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.Firma;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.PersonOderFirmaTyp;
import de.mnet.wbci.model.Standort;

/**
 * Helper class for converting obejcts from the hurrican model into the wbci model.
 *
 *
 */
@Test(groups = BaseTest.UNIT)
public class HurricanToWbciConverterTest {

    @DataProvider
    public static Object[][] companyNames() {
        return new Object[][] {
                { "Firmenname", "Firmennamenzusatz", "Firmenname Firmennamenzusatz", null },
                { "Firmenname der über 30 Zeichen enthält", "Firmennamenzusatz", "Firmenname der über 30 Zeichen", "enthält Firmennamenzusatz" },
        };
    }

    @Test(dataProvider = "companyNames")
    public void testExtractFrima(String name1, String name2, String expectedName1, String expectedName2) throws Exception {
        final PersonOderFirma firma = HurricanToWbciConverter.extractPersonOderFirma(new AdresseBuilder()
                .withFormatName(CCAddress.ADDRESS_FORMAT_BUSINESS)
                .withName(name1)
                .withVorname(name2)
                .build());
        Assert.assertEquals(firma.getTyp(), PersonOderFirmaTyp.FIRMA);
        Assert.assertTrue(Firma.class.isAssignableFrom(firma.getClass()));
        Assert.assertEquals(firma.getAnrede(), Anrede.FIRMA);
        Assert.assertEquals(((Firma) firma).getFirmenname(), expectedName1);
        Assert.assertEquals(((Firma) firma).getFirmennamenZusatz(), expectedName2);
    }

    @Test
    public void testExtractPerson() throws Exception {
        final PersonOderFirma person = HurricanToWbciConverter.extractPersonOderFirma(new AdresseBuilder()
                .withFormatName(CCAddress.ADDRESS_FORMAT_RESIDENTIAL)
                .withAnrede("Herr")
                .withName("Mustermann")
                .withVorname("Max")
                .build());
        Assert.assertEquals(person.getTyp(), PersonOderFirmaTyp.PERSON);
        Assert.assertTrue(Person.class.isAssignableFrom(person.getClass()));
        Assert.assertEquals(person.getAnrede(), Anrede.HERR);
        Assert.assertEquals(((Person) person).getNachname(), "Mustermann");
        Assert.assertEquals(((Person) person).getVorname(), "Max");
    }

    @Test
    public void testExtractStandort() throws Exception {
        final String ort = "Muenchen";
        final String plz = "80000";
        final String strasse = "Musterstrasse";
        final String hausnummer = "1";
        final String hausnummerzusatz = "A";
        final Standort standort = HurricanToWbciConverter.extractStandort(new AdresseBuilder()
                .withOrt(ort)
                .withPlz(plz)
                .withStrasse(strasse)
                .withNummer(hausnummer)
                .withHausnummerZusatz(hausnummerzusatz)
                .build());
        Assert.assertEquals(standort.getOrt(), ort);
        Assert.assertEquals(standort.getPostleitzahl(), plz);
        Assert.assertEquals(standort.getStrasse().getStrassenname(), strasse);
        Assert.assertEquals(standort.getStrasse().getHausnummer(), hausnummer);
        Assert.assertEquals(standort.getStrasse().getHausnummernZusatz(), hausnummerzusatz);
    }

    @DataProvider(name = "invalidElTalAbsenderIds")
    public Object[][] createInvalidElTalAbsenderId() {
        return new Object[][] {
                { "123" }, // too short
                { "" },
                { null }
        };
    }

    @Test(expectedExceptions = { StringIndexOutOfBoundsException.class, NullPointerException.class }, dataProvider = "invalidElTalAbsenderIds")
    public void testExtractPKIaufFromElTalAbsenderIdWithInvalidId(String elTalAbsenderId) throws FindException {
        HurricanToWbciConverter.extractPKIaufFromElTalAbsenderId(new CarrierKennungBuilder().withElTalAbsenderId(elTalAbsenderId).build());
    }

    @Test
    public void testExtractPKIaufFromElTalAbsenderIdWithValidId() {
        Assert.assertEquals(HurricanToWbciConverter.extractPKIaufFromElTalAbsenderId(
                new CarrierKennungBuilder().withElTalAbsenderId("1234").build()), "1234");
        Assert.assertEquals(HurricanToWbciConverter.extractPKIaufFromElTalAbsenderId(
                new CarrierKennungBuilder().withElTalAbsenderId("12345").build()), "1234");
        Assert.assertNull(HurricanToWbciConverter.extractPKIaufFromElTalAbsenderId(null));
    }

}
