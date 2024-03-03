/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 11:15:11
 */
package de.augustakom.hurrican.model.billing;

import javax.persistence.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * Abbildung einer Adresse aus dem Billing-System.
 *
 *
 */
public class Adresse extends AbstractBillingModel implements AddressModel, KundenModel, DebugModel {

    private static final long serialVersionUID = 7698393616072539140L;

    private static final String FORMAT_NAME_BUSINESS = "BUSINESS";
    private static final String FORMAT_NAME_GROSSKUNDEN = "Großkunden-Adresse";

    private Long adresseNo = null;
    private Long kundeNo = null;
    private String formatName = null;
    private String anrede = null;
    private String format = null;
    private String name = null;
    private String name2 = null;
    private String vorname = null;
    private String vorname2 = null;
    private String vorsatzwort = null;
    private String titel = null;
    private String titel2 = null;
    private String strasse = null;
    private String floor = null;
    private String nummer = null;
    private String hausnummerZusatz = null;
    private String postfach = null;
    private String plz = null;
    private String ort = null;
    private String ortsteil = null;
    private String ansprechpartner = null;
    private String nameAdd = null;
    private String landId = null;
    private Boolean active = null;
    private Long geoId = null;

    @Transient
    public boolean isBusinessAddress() {
        return StringTools.isIn(getFormatName(), new String[] { FORMAT_NAME_BUSINESS, FORMAT_NAME_GROSSKUNDEN });
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.AddressModel#getCombinedStreetData()
     */
    @Override
    public String getCombinedStreetData() {
        return getCombinedStreetData(getStrasse());
    }

    /**
     * @param streetName Strassenname, der verwendet werden soll (z.B. von GeoStreet)
     *
     */
    public String getCombinedStreetData(String streetName) {
        String esText = StringTools.join(new String[] { streetName, getNummer(), getHausnummerZusatz() }, " ", true);
        return StringTools.join(new String[] { esText, getFloor() }, ", ", true);
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.AddressModel#getCombinedNameData()
     */
    @Override
    public String getCombinedNameData() {
        if (getFormatName().equals(CCAddress.ADDRESS_FORMAT_BUSINESS)) {
            return StringTools.join(new String[] { getName(), getName2(), getVorname(), getVorname2() }, " ", true);
        }
        else {
            String tmp = null;
            tmp = StringTools.join(new String[] { getName(), getVorname() }, " ", true);
            if (StringUtils.isNotBlank(getName2())) {
                return StringTools.join(new String[] { tmp, " und ", getName2(), getVorname2() }, " ", true);
            }
            else {
                return tmp;
            }
        }
    }

    public Long getAdresseNo() {
        return adresseNo;
    }

    public void setAdresseNo(Long adresseNo) {
        this.adresseNo = adresseNo;
    }

    /**
     * Gibt eine Kennzeichnung fuer die Art der Anrede an.
     *
     * @return Returns the anrede.
     */
    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    public String getAnsprechpartner() {
        return ansprechpartner;
    }

    public void setAnsprechpartner(String ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
    }

    /**
     * Gibt eine Kennzeichnung fuer das Format der Adresse an:
     *
     * @return Returns the format.
     */
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getHausnummerZusatz() {
        return hausnummerZusatz;
    }

    @Override
    public void setHausnummerZusatz(String hausnummerZusatz) {
        this.hausnummerZusatz = hausnummerZusatz;
    }

    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName2() {
        return name2;
    }

    @Override
    public void setName2(String name2) {
        this.name2 = name2;
    }

    @Override
    public String getNummer() {
        return nummer;
    }

    @Override
    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    @Override
    public String getOrt() {
        return ort;
    }

    @Override
    public void setOrt(String ort) {
        this.ort = ort;
    }

    @Override
    public String getOrtsteil() {
        return ortsteil;
    }

    @Override
    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

    @Override
    public String getCombinedOrtOrtsteil() {
        return StringTools.join(new String[] { ort, ortsteil }, " - ", true);
    }

    @Override
    public String getPlz() {
        return plz;
    }

    @Override
    public String getPlzTrimmed() {
        String tmp = getPlz();
        return StringUtils.trimToEmpty(tmp);
    }

    @Override
    public void setPlz(String plz) {
        this.plz = plz;
    }

    @Override
    public String getPostfach() {
        return postfach;
    }

    @Override
    public void setPostfach(String postfach) {
        this.postfach = postfach;
    }

    @Override
    public String getStrasse() {
        return strasse;
    }

    @Override
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    @Override
    public String getStrasseAdd() {
        return getFloor();
    }

    @Override
    public void setStrasseAdd(String strasseAdd) {
        setFloor(strasseAdd);
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getTitel2() {
        return titel2;
    }

    public void setTitel2(String titel2) {
        this.titel2 = titel2;
    }

    @Override
    public String getVorname() {
        return vorname;
    }

    @Override
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    @Override
    public String getVorname2() {
        return vorname2;
    }

    @Override
    public void setVorname2(String vorname2) {
        this.vorname2 = vorname2;
    }

    public String getNameAdd() {
        return nameAdd;
    }

    public void setNameAdd(String anschriftErgaenzung) {
        this.nameAdd = anschriftErgaenzung;
    }

    @Override
    public String getFormatName() {
        return this.formatName;
    }

    @Override
    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public String getVorsatzwort() {
        return this.vorsatzwort;
    }

    public void setVorsatzwort(String vorsatzwort) {
        this.vorsatzwort = vorsatzwort;
    }

    @Override
    public String getLandId() {
        return landId;
    }

    @Override
    public void setLandId(String landId) {
        this.landId = landId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Enthaelt den Adress-Zusatz fuer "Lage TAE"
     */
    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    @Override
    public void debugModel(Logger logger) {
        if (logger.isDebugEnabled()) {
            logger.debug("Properties zu " + Adresse.class.getName());
            logger.debug("  No         : " + getKundeNo());
            logger.debug("  Name       : " + getName());
            logger.debug("  Vorname    : " + getVorname());
            logger.debug("  Street     : " + getStrasse());
            logger.debug("  House-Num  : " + getNummer());
        }
    }

}
