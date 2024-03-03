/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2004 16:29:44
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell bildet eine HVT-Technik ab.
 */
public class HVTTechnik extends AbstractCCIDModel {

    public static final Long SIEMENS = Long.valueOf(1);
    public static final Long HUAWEI = Long.valueOf(2);
    public static final Long ALCATEL = Long.valueOf(6);

    public static final String HERSTELLER = "hersteller";
    private String hersteller = null;
    private String beschreibung = null;
    private String cpsName = null;

    /**
     * Prueft, ob fuer die Technik eine IP-Adresse benoetigt wird. Dies ist dann der Fall, wenn es sich bei dem
     * Hersteller um 'Huawei' handelt.
     */
    public static boolean isIpRequired(Long hvtTechnikId) {
        return NumberTools.equal(hvtTechnikId, HVTTechnik.HUAWEI);
    }

    /**
     * Prueft, ob es sich bei der Technik um einen Hersteller fuer die Niederlassung Augsburg handelt.
     */
    public static boolean isHVTTechnikAGB(Long hvtTechnikId) {
        return NumberTools.isIn(hvtTechnikId, new Number[] { HVTTechnik.HUAWEI, HVTTechnik.SIEMENS });
    }

    /**
     * Prueft, ob es sich bei der Technik um einen Hersteller fuer die Niederlassung Muenchen handelt.
     */
    public static boolean isHVTTechnikMUC(Long hvtTechnikId) {
        return NumberTools.isIn(hvtTechnikId, new Number[] { HVTTechnik.ALCATEL, HVTTechnik.SIEMENS });
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getCpsName() {
        return cpsName;
    }

    public void setCpsName(String cpsName) {
        this.cpsName = cpsName;
    }
}


