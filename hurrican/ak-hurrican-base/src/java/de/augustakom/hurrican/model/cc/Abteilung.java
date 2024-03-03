/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:10:37
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.ArrayUtils;

import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell bildet eine Abteilung ab.
 *
 *
 */
public class Abteilung extends AbstractCCIDModel {

    public static final Long ST_CONNECT = Long.valueOf(1);
    public static final Long ST_ONLINE = Long.valueOf(2);
    public static final Long ST_VOICE = Long.valueOf(3);
    public static final Long DISPO = Long.valueOf(4);
    public static final Long FIELD_SERVICE = Long.valueOf(5);
    public static final Long AM = Long.valueOf(7);
    public static final Long PM = Long.valueOf(8);
    public static final Long FIBU = Long.valueOf(9);
    public static final Long ITS = Long.valueOf(10);
    public static final Long NP = Long.valueOf(11);
    public static final Long EXTERN = Long.valueOf(12);
    public static final Long MQUEUE = Long.valueOf(13);
    public static final Long FFM = Long.valueOf(14);
    public static final Long ZP_ZENTRALE_INFRASTRUKTUR = Long.valueOf(15);
    public static final Long ZP_TECHNIKCENTER = Long.valueOf(16);
    public static final Long ZP_ZENTRALE_IP_SYSTEME = Long.valueOf(17);
    public static final Long ZP_TECHNOLOGIE = Long.valueOf(18);
    public static final Long ZP_RESSOURCEN_DOKU = Long.valueOf(19);

    private static final Number[] ABTEILUNGEN_DISPO_AND_NP = new Number[] { DISPO, NP };

    private String name = null;
    private Long niederlassungId = null;
    private Boolean relevant4Proj = null;
    private Boolean relevant4Ba = null;
    private Boolean valid4UniversalGui = null;

    /**
     * Gibt die Abteilungs-IDs der Abteilungen Dispo + Netzplanung in einem Array zurueck.
     */
    public static Number[] getDispoAndNP() {
        return (Number[]) ArrayUtils.clone(ABTEILUNGEN_DISPO_AND_NP);
    }

    /**
     * Ueberprueft, ob es sich bei der Abteilung mit der ID 'abtId' um Dispo oder Netzplanung handelt.
     */
    public static boolean isDispoOrNP(Long abtId) {
        return NumberTools.isIn(abtId, ABTEILUNGEN_DISPO_AND_NP);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public Boolean getRelevant4Ba() {
        return relevant4Ba;
    }

    public void setRelevant4Ba(Boolean relevant4BA) {
        this.relevant4Ba = relevant4BA;
    }

    public Boolean getRelevant4Proj() {
        return relevant4Proj;
    }

    public void setRelevant4Proj(Boolean relevant4Proj) {
        this.relevant4Proj = relevant4Proj;
    }

    public Boolean getValid4UniversalGui() {
        return valid4UniversalGui;
    }

    public void setValid4UniversalGui(Boolean valid4UniversalGui) {
        this.valid4UniversalGui = valid4UniversalGui;
    }
}
