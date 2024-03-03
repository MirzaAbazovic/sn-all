/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.10.2009 10:49:35
 */
package de.augustakom.hurrican.model.cc;


/**
 *
 */
@SuppressWarnings("unused")
public class CCAddressBuilder extends AbstractCCIDModelBuilder<CCAddressBuilder, CCAddress> {
    private Long kundeNo = null;
    private Long addressType = CCAddress.ADDRESS_TYPE_VARIOUS;
    private String anrede = "Frau";
    private String titel = "Dr. h.c. mult.";
    private String formatName = CCAddress.ADDRESS_FORMAT_RESIDENTIAL;
    private String name = "Huber";
    private String name2 = null;
    private String vorname = "Sepp";
    private String vorname2 = null;
    private String strasse = "Komischweg";
    private String strasseAdd = null;
    private String nummer = "23";
    private String hausnummerZusatz = null;
    private String postfach = null;
    private String plz = "99293";
    private String ort = "Freierfunden";
    private String ortsteil = null;
    private String landId = "DE";
    private String email = "ncuhwniodvasdfhjn@mailinator.com";
    private String telefon = "+49 89 123";
    private String fax = "+49 89 321";
    private String handy = "+49 mobil 123";
    private String bemerkung = "Test Ansprechpartner";

    public CCAddressBuilder withAddressType(Long addressType) {
        this.addressType = addressType;
        return this;
    }

    public CCAddressBuilder withStrasse(String strasse) {
        this.strasse = strasse;
        return this;
    }

    public CCAddressBuilder withStrasseAdd(String strasseAdd) {
        this.strasseAdd = strasseAdd;
        return this;
    }

    public CCAddressBuilder withPostfach(String postfach) {
        this.postfach = postfach;
        return this;
    }

    public CCAddressBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CCAddressBuilder withFax(String fax) {
        this.fax = fax;
        return this;
    }

    public CCAddressBuilder withNummer(String nummer) {
        this.nummer = nummer;
        return this;
    }

    public CCAddressBuilder withHausnummerZusatz(String hausnummerZusatz) {
        this.hausnummerZusatz = hausnummerZusatz;
        return this;
    }

    public CCAddressBuilder withOrt(String ort) {
        this.ort = ort;
        return this;
    }

    public CCAddressBuilder withPlz(String plz) {
        this.plz = plz;
        return this;
    }

    public CCAddressBuilder withLandId(String landId) {
        this.landId = landId;
        return this;
    }

    public CCAddressBuilder withFormatName(String formatName) {
        this.formatName = formatName;
        return this;
    }

    public CCAddressBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
}
