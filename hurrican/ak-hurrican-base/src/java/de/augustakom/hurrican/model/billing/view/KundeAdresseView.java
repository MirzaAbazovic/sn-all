/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2004 09:51:36
 */
package de.augustakom.hurrican.model.billing.view;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.billing.AbstractBillingModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * View-Modell, um wichtige Kunden- und Adressdaten abzubilden.
 */
public class KundeAdresseView extends AbstractBillingModel implements KundenModel, DebugModel {

    private Long kundeNo = null;
    private Long oldKundeNo = null;
    private Long hauptKundenNo = null;
    private Long areaNo = null;
    private String name = null;
    private String vorname = null;
    private String strasse = null;
    private String nummer = null;
    private String hausnummerZusatz = null;
    private String plz = null;
    private String ort = null;
    private String ortsteil;
    private String kundenTyp = null;
    private String bonitaetId = null;
    private String kundenbetreuer = null;
    private String areaName = null;

    /**
     * @return Gibt die Strasse + Hausnummer + Hausnummerzusatz zurueck.
     */
    public String getStrasseWithNumber() {
        StringBuilder strasse = new StringBuilder();
        strasse.append(getStrasse());
        if (getNummer() != null) {
            strasse.append(" ");
            strasse.append(StringUtils.trimToEmpty(getNummer()));
            if (StringUtils.isNotBlank(getHausnummerZusatz())) {
                strasse.append(" ");
                strasse.append(StringUtils.trimToEmpty(getHausnummerZusatz()));
            }
        }
        return strasse.toString();
    }

    public String getBonitaetId() {
        return bonitaetId;
    }

    public void setBonitaetId(String bonitaetId) {
        this.bonitaetId = bonitaetId;
    }

    public Long getHauptKundenNo() {
        return hauptKundenNo;
    }

    public void setHauptKundenNo(Long hauptKundenNo) {
        this.hauptKundenNo = hauptKundenNo;
    }

    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public Long getOldKundeNo() {
        return oldKundeNo;
    }

    public void setOldKundeNo(Long oldKundeNo) {
        this.oldKundeNo = oldKundeNo;
    }

    public String getKundenTyp() {
        return kundenTyp;
    }

    public void setKundenTyp(String kundenTyp) {
        this.kundenTyp = kundenTyp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNummer() {
        return nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public String getHausnummerZusatz() {
        return hausnummerZusatz;
    }

    public void setHausnummerZusatz(String hausnummerZusatz) {
        this.hausnummerZusatz = hausnummerZusatz;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String org) {
        this.ort = org;
    }

    public String getOrtsteil() {
        return ortsteil;
    }

    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

    public String getCombinedOrtOrtsteil() {
        return StringTools.join(new String[] { ort, ortsteil }, " - ", true);
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getKundenbetreuer() {
        return kundenbetreuer;
    }

    public void setKundenbetreuer(String kundenbetreuer) {
        this.kundenbetreuer = kundenbetreuer;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Long getAreaNo() {
        return this.areaNo;
    }

    public void setAreaNo(Long areaNo) {
        this.areaNo = areaNo;
    }

    @Override
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Properties von " + KundeAdresseView.class.getName());
            logger.debug("  Kunde-No      : " + getKundeNo());
            logger.debug("  Alte Kunde-No : " + getOldKundeNo());
            logger.debug("  Hauptkunden-No: " + getHauptKundenNo());
            logger.debug("  Name          : " + getName());
            logger.debug("  Vorname       : " + getVorname());
            logger.debug("  Strasse       : " + getStrasse());
            logger.debug("  Ort           : " + getOrt());
            logger.debug("  Kunden-Betr.  : " + getKundenbetreuer());
        }
    }
}
