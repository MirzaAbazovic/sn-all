/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.10.2009 10:30:10
 */
package de.augustakom.hurrican.model.billing;

import de.augustakom.hurrican.model.cc.CCAddress;

@SuppressWarnings("unused")
public class AdresseBuilder extends BillingEntityBuilder<AdresseBuilder, Adresse> {
    private Long adresseNo = null;
    private Long kundeNo = null;
    private String formatName = CCAddress.ADDRESS_FORMAT_RESIDENTIAL;
    private String anrede = "Frau";
    private String format = null;
    private String name = "Huber";
    private String name2 = null;
    private String vorname = "Sepp";
    private String vorname2 = null;
    private String vorsatzwort = null;
    private String titel = "Dr. h.c. mult.";
    private String titel2 = null;
    private String strasse = "Komischweg";
    private String floor = "Keller";
    private String nummer = null;
    private String hausnummerZusatz = null;
    private String postfach = null;
    private String plz = "99293";
    private String ort = "Freierfunden";
    private String ortsteil = null;
    private String ansprechpartner = null;
    private String nameAdd = null;
    private String landId = null;
    private Boolean active = null;
    private Long geoId = null;
    private String gebaeudeteilName = "gebaeudeteil";
    private String gebaeudeteilZusatz = "gebaeudeteilZusatz";

    public AdresseBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public AdresseBuilder withRandomAdresseNo() {
        this.adresseNo = getLongId();
        return this;
    }

    public AdresseBuilder withAdresseNo(Long adresseNo) {
        this.adresseNo = adresseNo;
        return this;
    }

    public AdresseBuilder withGeoId(Long geoId) {
        this.geoId = geoId;
        return this;
    }

    public AdresseBuilder withName2(String name2) {
        this.name2 = name2;
        return this;
    }

    public AdresseBuilder withVorname2(String vorname2) {
        this.vorname2 = vorname2;
        return this;
    }

    public AdresseBuilder withGebaeudeteilName(String gebaeudeteilName) {
        this.gebaeudeteilName = gebaeudeteilName;
        return this;
    }

    public AdresseBuilder withGebaeudeteilZusatz(String gebaeudeteilZusatz) {
        this.gebaeudeteilZusatz = gebaeudeteilZusatz;
        return this;
    }

    public AdresseBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AdresseBuilder withVorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public AdresseBuilder withNameAdd(String nameAdd) {
        this.nameAdd = nameAdd;
        return this;
    }

    public AdresseBuilder withAnrede(String anrede) {
        this.anrede = anrede;
        return this;
    }

    public AdresseBuilder withOrt(String ort) {
        this.ort = ort;
        return this;
    }

    public AdresseBuilder withOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
        return this;
    }

    public AdresseBuilder withFloor(String floor) {
        this.floor = floor;
        return this;
    }

    public AdresseBuilder withPlz(String plz) {
        this.plz = plz;
        return this;
    }

    public AdresseBuilder withStrasse(String strasse) {
        this.strasse = strasse;
        return this;
    }

    public AdresseBuilder withNummer(String nummer) {
        this.nummer = nummer;
        return this;
    }

    public AdresseBuilder withHausnummerZusatz(String hausnummerZusatz) {
        this.hausnummerZusatz = hausnummerZusatz;
        return this;
    }

    public AdresseBuilder withFormatName(String formatName) {
        this.formatName = formatName;
        return this;
    }

    public AdresseBuilder withFormat(String format) {
        this.format = format;
        return this;
    }

    public AdresseBuilder withLandId(String landId) {
        this.landId = landId;
        return this;
    }

}
