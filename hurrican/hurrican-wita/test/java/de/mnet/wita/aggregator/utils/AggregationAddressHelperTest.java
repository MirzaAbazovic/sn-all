/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2011 08:33:25
 */
package de.mnet.wita.aggregator.utils;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.AddressFormat;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.common.Firmenname;

@Test(groups = UNIT)
public class AggregationAddressHelperTest extends BaseTest {

    @DataProvider
    public Object[][] anredeForAddress() {
        // @formatter:off
        return new Object[][] {
                {AddressFormat.ADDRESS_FORMAT_NAME_BUSINESS, null, true, Anrede.FIRMA},
                { AddressFormat.ADDRESS_FORMAT_NAME_RESIDENTIAL, AddressFormat.SALUTATION_HERR, true, Anrede.HERR},
                { AddressFormat.ADDRESS_FORMAT_NAME_RESIDENTIAL, AddressFormat.SALUTATION_HERRFRAU, true, Anrede.HERR},
                { AddressFormat.ADDRESS_FORMAT_NAME_RESIDENTIAL, AddressFormat.SALUTATION_FRAU, true, Anrede.FRAU},
                { AddressFormat.ADDRESS_FORMAT_NAME_RESIDENTIAL, AddressFormat.SALUTATION_FRAUHERR, true, Anrede.FRAU},
                { "Herr", null, false, Anrede.HERR}, { "Mr.", null, false, Anrede.HERR},
                { "Frau", null, false, Anrede.FRAU}, { "Mrs.", null, false, Anrede.FRAU},
                { "Herr und Frau", null, false, Anrede.HERR},
                { "Frau und Herr", null, false, Anrede.FRAU},
                { AddressFormat.ADDRESS_FORMAT_NAME_RESIDENTIAL, null, false, Anrede.UNBEKANNT}
        };
        // @formatter:on
    }

    @Test(dataProvider = "anredeForAddress")
    public void getAnredeForAddress(String formatName, String salutation, boolean billingAddress, Anrede expectedResult) {
        AddressModel address = (billingAddress) ? new Adresse() : new CCAddress();
        address.setFormatName(formatName);
        if (billingAddress) {
            ((Adresse) address).setAnrede(salutation);
        }

        Anrede anrede = AggregationAddressHelper.getAnredeForAddress(address);
        assertEquals(anrede, expectedResult);
    }


    @DataProvider(name = "testFirmennameLaengerAls30ZeichenDP")
    public Object[][] testFirmennameLaengerAls30ZeichenDP() {
        return new Object[][] {
                { "Langer Name mit ueber 30 Zeichen,", "Zusatz", "Langer Name mit ueber 30", "Zeichen, Zusatz" },
                { "Langer Name mit ueber 30 Zeic-hen,", "Zusatz", "Langer Name mit ueber 30 Zeic-", "hen, Zusatz" },
                { "Langer Name mit ueber 30 Zeic hen,", "Zusatz", "Langer Name mit ueber 30 Zeic", "hen, Zusatz" },
        };
    }

    @Test(dataProvider = "testFirmennameLaengerAls30ZeichenDP")
    public void testFirmennameLaengerAls30Zeichen(String name, String name2, String expectedName, String expectedName2) {
        CCAddress address = new CCAddress();
        address.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
        address.setFormatName(CCAddress.ADDRESS_FORMAT_BUSINESS);
        address.setName(name);
        address.setName2(name2);

        Firmenname firmenname = (Firmenname) AggregationAddressHelper.getKundenname(address);
        assertEquals(firmenname.getAnrede(), Anrede.FIRMA);
        assertEquals(firmenname.getErsterTeil(), expectedName);
        assertEquals(firmenname.getZweiterTeil(), expectedName2);
    }


    @DataProvider(name = "testCuttenVonSehrLangenFirmennamenDP")
    public Object[][] testCuttenVonSehrLangenFirmennamenDP() {
        return new Object[][] {
                { "Langer Name mit ueber 30 Zeichen,", "ebenfalls laenger als 30 Zeichen", "Langer Name mit ueber 30", "Zeichen, ebenfalls laenger als" },
                { "Langer Name mit ueber 30 Zeic-hen,", "ebenfalls laenger als 30 Zeichen", "Langer Name mit ueber 30 Zeic-", "hen, ebenfalls laenger als 30" },
                { "Langer Name mit ueber 30 Zeich-en,", "ebenfalls laenger als 30 Zeichen", "Langer Name mit ueber 30 Zeich", "-en, ebenfalls laenger als 30" },
                { "Langer Name mit ueber 30 Zeic hen,", "ebenfalls laenger als 30 Zeichen", "Langer Name mit ueber 30 Zeic", "hen, ebenfalls laenger als 30" },
        };
    }


    @Test(dataProvider = "testCuttenVonSehrLangenFirmennamenDP")
    public void testCuttenVonSehrLangenFirmennamen(String name, String name2, String expectedName, String expectedName2) {
        CCAddress address = new CCAddress();
        address.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
        address.setFormatName(CCAddress.ADDRESS_FORMAT_BUSINESS);
        address.setName(name);
        address.setName2(name2);

        Firmenname firmenname = (Firmenname) AggregationAddressHelper.getKundenname(address);
        assertEquals(firmenname.getErsterTeil(), expectedName);
        assertEquals(firmenname.getZweiterTeil(), expectedName2);
    }

    public void testKurzerFirmennamen() {
        CCAddress address = new CCAddress();
        address.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
        address.setFormatName(CCAddress.ADDRESS_FORMAT_BUSINESS);
        address.setName("Bla");
        address.setName2("Foo");

        Firmenname firmenname = (Firmenname) AggregationAddressHelper.getKundenname(address);
        assertEquals(firmenname.getErsterTeil(), "Bla Foo");
        assertNull(firmenname.getZweiterTeil());
    }

    public void testeNameWithoutName2() {
        CCAddress address = new CCAddress();
        address.setAddressType(CCAddress.ADDRESS_TYPE_ACCESSPOINT);
        address.setFormatName(CCAddress.ADDRESS_FORMAT_BUSINESS);
        address.setName("Bla");

        Firmenname firmenname = (Firmenname) AggregationAddressHelper.getKundenname(address);
        assertEquals(firmenname.getErsterTeil(), "Bla");
        assertNull(firmenname.getZweiterTeil());
    }

    @DataProvider
    public Object[][] addressNamen() {
        // @formatter:off
        return new Object[][] {
                {"foo", null, "foo"},
                {"", null, ""},
                {null, "", ""},
                {"foo", "", "foo"},
                {"Gerd", "Stefanie", "Gerd und Stefanie"},
                {"foo", "bar", "foo und bar"},
                {" foo ", "bar  ", "foo und bar"},
                {" foo ", "  bar  ", "foo und bar"},
                {" bar very long with lots of text that is too long ", "  foo  ", "bar very long with lots of tex"},
                {null, null, ""},
                {"foo ", "bar very long with lots of text that is too long", "foo und bar very long with lot"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "addressNamen")
    public void getNameWithName2(String name, String name2, String expectedNameAndName2) {
        AddressModel address = createCCAddressWithNames(name, name2);
        assertEquals(AggregationAddressHelper.getNameWithName2(address), expectedNameAndName2);
    }

    @Test(dataProvider = "addressNamen")
    public void getVornameWithVorname2(String vorname, String vorname2, String expectedVornameWithVorname2) {
        AddressModel address = createCCAddressWithVornamen(vorname, vorname2);
        assertEquals(AggregationAddressHelper.getVornameWithVorname2(address), expectedVornameWithVorname2);
    }

    private static AddressModel createCCAddressWithNames(String name, String name2) {
        AddressModel address = new CCAddress();
        address.setName(name);
        address.setName2(name2);
        return address;
    }

    private static AddressModel createCCAddressWithVornamen(String vorname, String vorname2) {
        AddressModel address = new CCAddress();
        address.setVorname(vorname);
        address.setVorname2(vorname2);
        return address;
    }

}
