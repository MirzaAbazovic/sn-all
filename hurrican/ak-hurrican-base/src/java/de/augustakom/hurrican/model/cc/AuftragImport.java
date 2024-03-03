/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2005 10:56:20
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Modell fuer die Abbildung eines Auftrag-Imports.
 *
 *
 */
public class AuftragImport extends AbstractCCIDModel implements KundenModel {

    /**
     * Date-Pattern fuer das Import-File.
     */
    public static final String AUFTRAG_IMPORT_DATE_PATTERN = "dd.MM.yyyy";

    /* Konstanten fuer die XML-Tags des Import-Files */
    public static final String AI_NODE_REF_ID = "ref";
    public static final String AI_NODE_AG_ANREDE = "ag_anrede";
    public static final String AI_NODE_AG_TITEL = "ag_titel";
    public static final String AI_NODE_AG_NAME = "ag_nachname";
    public static final String AI_NODE_AG_VORNAME = "ag_vorname";
    public static final String AI_NODE_AG_STRASSE = "ag_strasse";
    public static final String AI_NODE_AG_HAUS_NR = "ag_nr";
    public static final String AI_NODE_AG_PLZ = "ag_plz";
    public static final String AI_NODE_AG_ORT = "ag_ort";
    public static final String AI_NODE_AG_TEL_VORWAHL = "ag_tel_vor";
    public static final String AI_NODE_AG_TEL_DURCHWAHL = "ag_tel_durch";
    public static final String AI_NODE_AG_EMAIL = "ag_mail";
    public static final String AI_NODE_AG_GEBDATUM = "ag_gebdatum";
    public static final String AI_NODE_ANSCHLUSS_ANREDE = "a_anrede";
    public static final String AI_NODE_ANSCHLUSS_TITEL = "a_titel";
    public static final String AI_NODE_ANSCHLUSS_NAME = "a_nachname";
    public static final String AI_NODE_ANSCHLUSS_VORNAME = "a_vorname";
    public static final String AI_NODE_ANSCHLUSS_PLZ = "a_plz";
    public static final String AI_NODE_ANSCHLUSS_ORT = "a_ort";
    public static final String AI_NODE_ANSCHLUSS_STRASSE = "a_strasse";
    public static final String AI_NODE_ANSCHLUSS_HAUS_NR = "a_nr";
    public static final String AI_NODE_ANSCHLUSS_STOCK = "a_stock";
    public static final String AI_NODE_KONTO_BANK = "kontobank";
    public static final String AI_NODE_KONTO_INH = "kontoinh";
    public static final String AI_NODE_KONTO_NR = "kontonr";
    public static final String AI_NODE_KONTO_BLZ = "kontoblz";
    public static final String AI_NODE_RECHNUNG_ANREDE = "r_anrede";
    public static final String AI_NODE_RECHNUNG_TITEL = "r_titel";
    public static final String AI_NODE_RECHNUNG_NAME = "r_nachname";
    public static final String AI_NODE_RECHNUNG_VORNAME = "r_vorname";
    public static final String AI_NODE_RECHNUNG_STRASSE = "r_strasse";
    public static final String AI_NODE_RECHNUNG_HAUS_NR = "r_nr";
    public static final String AI_NODE_RECHNUNG_PLZ = "r_plz";
    public static final String AI_NODE_RECHNUNG_ORT = "r_ort";
    public static final String AI_NODE_VORWAHL = "vorwahl";
    public static final String AI_NODE_NUMMER01 = "nummer_01";
    public static final String AI_NODE_ANSCHLUSS = "anschluss";
    public static final String AI_NODE_MAXI_DSL = "maxidsl";
    public static final String AI_NODE_PRODNAME = "prodname";
    public static final String AI_NODE_ALT_NAME = "alt_nachname";
    public static final String AI_NODE_ALT_VORNAME = "alt_vorname";
    public static final String AI_NODE_ALT_STRASSE = "alt_strasse";
    public static final String AI_NODE_ALT_HAUS_NR = "alt_nr";
    public static final String AI_NODE_ALT_ORT = "alt_ort";
    public static final String AI_NODE_ALT_STOCK = "alt_stock";
    public static final String AI_NODE_WUNSCHTERMIN = "terminwunsch";
    public static final String AI_NODE_ALT_CARRIER = "telges";
    public static final String AI_NODE_PAPIERRECHNUNG = "paprech";
    public static final String AI_NODE_COUPON = "coupon";
    public static final String AI_NODE_AKTION = "aktion";
    public static final String AI_NODE_PARTNER = "partner";
    public static final String AI_NODE_PRICE = "price";
    public static final String AI_NODE_MOBIL_TARIF_CONTRACT1 = "mobil_tarif_contract1";
    public static final String AI_NODE_UPGRADE_18000 = "upgrade18000";
    public static final String AI_NODE_ISDN_ANSCHLUSS = "isdnanschluss";
    public static final String AI_NODE_TECHNOLOGY = "technology";
    public static final String AI_NODE_BANDWIDTH_SELLING_LIMIT = "bandwidthSellingLimitInKB";

    private Long kundeNo = null;
    private String xmlFile = null;
    private String refId = null;
    private String partnerId = null;
    private String agName = null;
    private String agVorname = null;
    private String agStrasse = null;
    private String agPlz = null;
    private String agOrt = null;
    private String anschlussName = null;
    private String anschlussVorname = null;
    private String anschlussStrasse = null;
    private String anschlussOrt = null;
    private String anschlussStock = null;
    private String altName = null;
    private String altVorname = null;
    private String altStrasse = null;
    private String altOrt = null;
    private String altStock = null;
    private String rufnummer = null;
    private String produktName = null;
    private Boolean portierung = null;
    private Boolean tarifwechsel = null;
    private Boolean umzug = null;
    private Date wunschtermin = null;
    private Date auftragsEingang = null;
    private Boolean active = null;
    private Long importStatus = null;
    private String bemerkung = null;
    private String bearbeiter = null;
    private Date lastModified = null;
    private String coupon = null;
    private String aktion = null;
    private Float preis = null;
    private Boolean mobile = null;
    private Boolean upgrade18000 = null;
    private Boolean isdnAnschluss = null;
    private String technology = null;
    private Long bandwidthSellingLimit = null;

    private String anschlussNameVorname = null;
    private String agNameVorname = null;
    private String altNameVorname = null;

    /*
     * Fuegt die Strings <code>name</code> und <code>vorname</code>
     * zu einem Namen/String zusammen.
     */
    private String combineNames(String name, String vorname) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.isNotBlank(name) ? name : "");
        if ((sb.length() > 0) && StringUtils.isNotBlank(vorname)) {
            sb.append(" ");
            sb.append(vorname);
        }
        return sb.toString();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAgName() {
        return agName;
    }

    public void setAgName(String agName) {
        this.agName = agName;
    }

    /**
     * Gibt den Namen+Vornamen des Auftraggebers zurueck.
     *
     * @return
     */
    public String getAgNameVorname() {
        if (agNameVorname == null) {
            agNameVorname = combineNames(getAgName(), getAgVorname());
        }
        return agNameVorname;
    }

    /**
     * @return Returns the agVorname.
     */
    public String getAgVorname() {
        return agVorname;
    }

    public void setAgVorname(String agVorname) {
        this.agVorname = agVorname;
    }

    public String getAgPlz() {
        return agPlz;
    }

    public void setAgPlz(String agPlz) {
        this.agPlz = agPlz;
    }

    public String getAgOrt() {
        return agOrt;
    }

    public void setAgOrt(String agOrt) {
        this.agOrt = agOrt;
    }

    public String getAgStrasse() {
        return agStrasse;
    }

    public void setAgStrasse(String agStrasse) {
        this.agStrasse = agStrasse;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    /**
     * Gibt den Namen+Vornamen des 'alten' Rufnummern-Inhabers zurueck.
     *
     * @return
     */
    public String getAltNameVorname() {
        if (altNameVorname == null) {
            altNameVorname = combineNames(getAltName(), getAltVorname());
        }
        return altNameVorname;
    }

    public String getAltVorname() {
        return altVorname;
    }

    public void setAltVorname(String altVorname) {
        this.altVorname = altVorname;
    }

    public String getAnschlussName() {
        return anschlussName;
    }

    /**
     * Gibt den Namen+Vornamen des Anschluss-Inhabers zurueck.
     *
     * @return
     */
    public String getAnschlussNameVorname() {
        if (anschlussNameVorname == null) {
            anschlussNameVorname = combineNames(getAnschlussName(), getAnschlussVorname());
        }
        return anschlussNameVorname;
    }

    public void setAnschlussName(String anschlussName) {
        this.anschlussName = anschlussName;
    }

    public String getAnschlussOrt() {
        return anschlussOrt;
    }

    public void setAnschlussOrt(String anschlussOrt) {
        this.anschlussOrt = anschlussOrt;
    }

    public String getAnschlussStrasse() {
        return anschlussStrasse;
    }

    public void setAnschlussStrasse(String anschlussStrasse) {
        this.anschlussStrasse = anschlussStrasse;
    }

    public String getAnschlussVorname() {
        return anschlussVorname;
    }

    public void setAnschlussVorname(String anschlussVorname) {
        this.anschlussVorname = anschlussVorname;
    }

    public Long getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(Long importStatus) {
        this.importStatus = importStatus;
    }

    public Boolean getPortierung() {
        return portierung;
    }

    public void setPortierung(Boolean portierung) {
        this.portierung = portierung;
    }

    public String getProduktName() {
        return produktName;
    }

    public void setProduktName(String produktName) {
        this.produktName = produktName;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRufnummer() {
        return rufnummer;
    }

    public void setRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
    }

    public Date getWunschtermin() {
        return wunschtermin;
    }

    public void setWunschtermin(Date wunschtermin) {
        this.wunschtermin = wunschtermin;
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    public String getAltOrt() {
        return altOrt;
    }

    public void setAltOrt(String altOrt) {
        this.altOrt = altOrt;
    }

    public String getAltStrasse() {
        return altStrasse;
    }

    public void setAltStrasse(String altStrasse) {
        this.altStrasse = altStrasse;
    }

    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public Boolean getTarifwechsel() {
        return tarifwechsel;
    }

    public void setTarifwechsel(Boolean tarifwechsel) {
        this.tarifwechsel = tarifwechsel;
    }

    public Boolean getUmzug() {
        return umzug;
    }

    public void setUmzug(Boolean umzug) {
        this.umzug = umzug;
    }

    public Date getAuftragsEingang() {
        return auftragsEingang;
    }

    public void setAuftragsEingang(Date auftragsEingang) {
        this.auftragsEingang = auftragsEingang;
    }

    public String getBearbeiter() {
        return bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getAltStock() {
        return altStock;
    }

    public void setAltStock(String altStock) {
        this.altStock = altStock;
    }

    public String getAnschlussStock() {
        return anschlussStock;
    }

    public void setAnschlussStock(String anschlussStock) {
        this.anschlussStock = anschlussStock;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getAktion() {
        return aktion;
    }

    public void setAktion(String aktion) {
        this.aktion = aktion;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public Float getPreis() {
        return this.preis;
    }

    public void setPreis(Float preis) {
        this.preis = preis;
    }

    public Boolean getMobile() {
        return mobile;
    }

    public void setMobile(Boolean mobile) {
        this.mobile = mobile;
    }

    public Boolean getUpgrade18000() {
        return upgrade18000;
    }

    public void setUpgrade18000(Boolean upgrade18000) {
        this.upgrade18000 = upgrade18000;
    }

    public Boolean getIsdnAnschluss() {
        return isdnAnschluss;
    }

    public void setIsdnAnschluss(Boolean isdnAnschluss) {
        this.isdnAnschluss = isdnAnschluss;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public Long getBandwidthSellingLimit() {
        return bandwidthSellingLimit;
    }

    public void setBandwidthSellingLimit(Long bandwidthSellingLimit) {
        this.bandwidthSellingLimit = bandwidthSellingLimit;
    }

}


