/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2005 09:53:49
 */
package de.augustakom.hurrican.model.cc.dn;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;

/**
 * Modell stellt die Paremter zu den Rufnummernleistungen dar
 *
 *
 */
public class LeistungParameter extends AbstractCCIDModel {

    public static final Long ID_NUMMER_DER_SPERRKLASSE = 6L;

    private String leistungParameterBeschreibung = null;
    private Integer leistungParameterMehrfach = null;
    private Integer leistungParameterMehrfachIms;
    private Integer leistungParameterTyp = null;

    /* Konstanten zur Steuerung der Anzeige der Parametereingabe 'DNParameter2LeistungDialog' */
    /**
     * Erfordert die Eingabe der Rufnummer und der Nebenstelle als Parameter
     */
    public static final Integer PARAMETER_TYP_NS_RUFNUMMER = 1;

    /**
     * Erfordert die Eingabe der Rufnummer als Parameter
     */
    public static final Integer PARAMETER_TYP_RUFNUMMER = 2;

    /**
     * @return Returns the leistungParameterBeschreibung.
     */
    public String getLeistungParameterBeschreibung() {
        return leistungParameterBeschreibung;
    }

    /**
     * @param leistungParameterBeschreibung The leistungParameterBeschreibung to set.
     */
    public void setLeistungParameterBeschreibung(
            String leistungParameterBeschreibung) {
        this.leistungParameterBeschreibung = leistungParameterBeschreibung;
    }

    /**
     * @param hwSwitchType
     * @return Returns the leistungParameterMehrfach.
     */
    public Integer getLeistungParameterMehrfach(HWSwitchType hwSwitchType) {
        if (HWSwitchType.isImsOrNsp(hwSwitchType)) {
            return getLeistungParameterMehrfachIms();
        }
        return getLeistungParameterMehrfach();
    }

    public Integer getLeistungParameterMehrfach() {
        return leistungParameterMehrfach;
    }

    /**
     * @param leistungParameterMehrfach The leistungParameterMehrfach to set.
     */
    public void setLeistungParameterMehrfach(Integer leistungParameterMehrfach) {
        this.leistungParameterMehrfach = leistungParameterMehrfach;
    }

    public Integer getLeistungParameterMehrfachIms() {
        return leistungParameterMehrfachIms;
    }

    public void setLeistungParameterMehrfachIms(Integer leistungParameterMehrfachIms) {
        this.leistungParameterMehrfachIms = leistungParameterMehrfachIms;
    }

    /**
     * @return Returns the leistungParameterTyp.
     */
    public Integer getLeistungParameterTyp() {
        return leistungParameterTyp;
    }


    /**
     * @param leistungParameterTyp The leistungParameterTyp to set.
     */
    public void setLeistungParameterTyp(Integer leistungParameterTyp) {
        this.leistungParameterTyp = leistungParameterTyp;
    }

    /**
     * Gibt eine Bezeichnung fuer den Parameter zurueck Die Bezeichnung setzt sich aus 'leistungParameterBeschreibung' ,
     * 'leistungParameterMehrfach' und der Id zusammen.
     *
     * @param hwSwitchType
     * @return
     */
    public String getBezeichnung(HWSwitchType hwSwitchType) {
        if (getLeistungParameterBeschreibung() == null) {
            return null;
        }

        return String.format("%s (mögliche Anzahl: %s) - %s", getLeistungParameterBeschreibung(),
                getLeistungParameterMehrfach(hwSwitchType), getId());
    }

}
