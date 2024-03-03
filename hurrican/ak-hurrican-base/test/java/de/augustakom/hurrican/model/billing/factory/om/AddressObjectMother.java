/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;

import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.mnet.test.generator.model.Address;
import de.mnet.test.generator.model.Customer;


@SuppressWarnings("unused")
public class AddressObjectMother extends AbstractTaifunObjectMother {

    public static final String FORMAT_HERR = "HERR";
    public static final String FORMAT_FRAU = "FRAU";
    public static final String FORMAT_FIRMA = "FIRMA";
    public static final String FORMAT_FRAUHERR = "FRAUHERR";
    public static final String FORMAT_HERRFRAU = "HERRFRAU";

    public static final String FORMATNAME_RESIDENTIAL = "RESIDENTIAL";
    public static final String FORMATNAME_BUSINESS = "BUSINESS";
    public static final String FORMATNAME_HERRFRAU = "Herr und Frau";
    public static final String FORMATNAME_FRAUHERR = "Frau und Herr";

    public static final String SALUTATION_HERR = "HERR";
    public static final String SALUTATION_FIRMA = "FIRMA";
    public static final String SALUTATION_FRAU = "FRAU";
    public static final String SALUTATION_HERRFRAU = "HERRFRAU";
    public static final String SALUTATION_FRAUHERR = "FRAUHERR";
    public static final String SALUTATION_KEINE = "KEINE";

    public static AdresseBuilder createDefault() {
        Customer randomCustomer = randomCustomer();
        Address randomAddress = randomCustomer.getPrimaryAddress();

        return new AdresseBuilder()
                .withFormatName(CCAddress.ADDRESS_FORMAT_RESIDENTIAL)
                .withFormat(FORMAT_HERR)
                .withAnrede(SALUTATION_HERR)
                .withName(randomCustomer.getLastName())
                .withVorname(randomCustomer.getFirstName())
                .withStrasse(randomAddress.getStreet())
                .withNummer(String.format("%s", randomInt(1, 999)))
                .withHausnummerZusatz(randomString(1))
                .withPlz(randomAddress.getZip())
                .withOrt(randomAddress.getCity())
                .withOrtsteil("Ortsteil")
                .withGeoId(randomLong(0, 999999))
                .withLandId("DE");
    }

    public static AdresseBuilder herr() {
        return createDefault()
                .withFormat(AddressObjectMother.FORMAT_HERR)
                .withFormatName(AddressObjectMother.FORMATNAME_RESIDENTIAL)
                .withAnrede(AddressObjectMother.SALUTATION_HERR);
    }

    public static AdresseBuilder firma() {
        return createDefault()
                .withFormat(AddressObjectMother.FORMAT_FIRMA)
                .withFormatName(AddressObjectMother.FORMATNAME_BUSINESS)
                .withAnrede(AddressObjectMother.SALUTATION_FIRMA);
    }

}
