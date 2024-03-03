/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 09:41:08
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;


/**
 * Modell-Klasse fuer die unterschiedlichen Bauauftrags-Anlaesse.
 *
 *
 */
public class BAVerlaufAnlass extends AbstractCCIDModel {

    /** ID fuer den Verlaufs-Anlass 'Leistungsaenderungen'. */
    public static final Long LEISTUNGSAENDERUNGEN = 3L;
    /** ID fuer den Verlaufs-Anlass 'Kuendigung'. */
    public static final Long KUENDIGUNG = 13L;
    /** ID fuer den Verlaufs-Anlass 'Neuschaltung'. */
    public static final Long NEUSCHALTUNG = 27L;
    /** ID fuer den Verlaufs-Anlass 'Anbieterwechsel §46TKG Neuschaltung' */
    public static final Long ABW_TKG46_AENDERUNG = 71L;
    /** ID fuer den Verlaufs-Anlass 'Abw. TKG46 Neischaltung'. */
    public static final Long ABW_TKG46_NEUSCHALTUNG = 72L;
    /** ID fuer den Verlaufs-Aenderung 'Uebernahme'. Uebernahme eines DTAG-Anschlusses in das AKom-Netz. */
    public static final Long UEBERNAHME = 28L;
    /** ID fuer den Verlaufs-Anlass 'Kuendigung Anschlussuebernahme'. */
    public static final Long KUENDIGUNG_ANSCHLUSSUEBERNAHME = 41L;
    /** ID fuer den Verlaufs-Anlass 'Kuendigung Bandbreitenaenderung'. */
    public static final Long KUENDIGUNG_BANDBREITENAENDERUNG = 42L;
    /**
     * ID fuer den Verlaufs-Anlass 'Anschlussuebernahme'. Anschlussuebernahme: Uebernahme eines bestehenden
     * AK-Anschlusses auf einen neuen Kunden.
     */
    public static final Long ANSCHLUSSUEBERNAHME = 43L;
    /** ID fuer den Verlauf-Anlass 'automatischer Downgrade' (z.B. Deaktivierung der '100 MBit testen' Leistung) */
    public static final Long AUTO_DOWNGRADE = 45L;
    /** ID fuer den Verlaufs-Anlass 'Bandbreitenaenderung' */
    public static final Long BANDBREITENAENDERUNG = 50L;
    /** ID fuer den Verlaufs-Anlass 'Absage'. */
    public static final Long ABSAGE = 52L;
    /** ID fuer den Verlaufs-Anlass 'Projektierung'. */
    public static final Long PROJEKTIERUNG = 53L;
    /** ID fuer den Verlaufs-Anlass 'undefined' bzw. 'n/a'. */
    public static final Long UNDEFINED = 55L;
    /** ID fuer den Verlaufs-Anlass 'DSL-Kreuzung' */
    public static final Long DSL_KREUZUNG = 56L;
    /** ID fuer den Verlaufs-Anlass 'Aenderung Bandbreite' */
    public static final Long AENDERUNG_BANDBREITE = 62L;
    /** ID fuer den Verlaufs-Anlass 'interne Arbeit' */
    public static final Long INTERN_WORK = 70L;


    public static final String NAME = "name";
    private String name = null;
    private Long baVerlGruppe = null;
    private Boolean configurable = null;
    private Boolean auftragsart = null;
    private Integer positionNo = null;
    private Boolean akt = null;
    private Long cpsServiceOrderType = null;
    private FfmTyp ffmTyp;

    /**
     * Ueberprueft, ob es sich bei dem Anlass <code>anlass</code> um eine Kuendigungsart handelt.
     *
     * @param anlass zu pruefender Anlass
     * @return wenn der uebergebene Anlass einer Kuendigung entspricht
     */
    public static boolean isKuendigung(Long anlass) {
        return NumberTools.isIn(anlass, new Number[] { KUENDIGUNG, KUENDIGUNG_ANSCHLUSSUEBERNAHME,
                KUENDIGUNG_BANDBREITENAENDERUNG });
    }

    /**
     * @return Returns the akt.
     */
    public Boolean getAkt() {
        return this.akt;
    }

    /**
     * @param akt The akt to set.
     */
    public void setAkt(Boolean akt) {
        this.akt = akt;
    }

    /**
     * @return Returns the auftragsart.
     */
    public Boolean getAuftragsart() {
        return this.auftragsart;
    }

    /**
     * @param auftragsart The auftragsart to set.
     */
    public void setAuftragsart(Boolean auftragsart) {
        this.auftragsart = auftragsart;
    }

    /**
     * @return Returns the baVerlGruppe.
     */
    public Long getBaVerlGruppe() {
        return this.baVerlGruppe;
    }

    /**
     * @param baVerlGruppe The baVerlGruppe to set.
     */
    public void setBaVerlGruppe(Long baVerlGruppe) {
        this.baVerlGruppe = baVerlGruppe;
    }

    /**
     * @return Returns the configurable.
     */
    public Boolean getConfigurable() {
        return this.configurable;
    }

    /**
     * @param configurable The configurable to set.
     */
    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the positionNo.
     */
    public Integer getPositionNo() {
        return this.positionNo;
    }

    /**
     * @param positionNo The positionNo to set.
     */
    public void setPositionNo(Integer positionNo) {
        this.positionNo = positionNo;
    }

    /**
     * @return the cpsServiceOrderType
     */
    public Long getCpsServiceOrderType() {
        return cpsServiceOrderType;
    }

    /**
     * @param cpsServiceOrderType the cpsServiceOrderType to set
     */
    public void setCpsServiceOrderType(Long cpsServiceOrderType) {
        this.cpsServiceOrderType = cpsServiceOrderType;
    }

    public FfmTyp getFfmTyp() {
        return ffmTyp;
    }

    public void setFfmTyp(FfmTyp ffmTyp) {
        this.ffmTyp = ffmTyp;
    }

}


