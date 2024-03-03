/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2007 10:50:32
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Stellt eine View für ein IAD bereit
 *
 *
 */
public class IADView extends AbstractCCModel {

    // TODO EG-HANDLING: IADView entfernen, wenn Versand ueber Taifun

    private Long iadId = null;
    private String seriennummer = null;
    private String macAdresse = null;
    private String ipAdresse = null;
    private Long auftragId = null;
    private Long verlaufId = null;
    private Long egId = null;
    private String egName = null;
    private String egBeschreibung = null;
    private Long egTyp = null;

    /**
     * @return auftragId
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId Festzulegender auftragId
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return egBeschreibung
     */
    public String getEgBeschreibung() {
        return egBeschreibung;
    }

    /**
     * @param egBeschreibung Festzulegender egBeschreibung
     */
    public void setEgBeschreibung(String egBeschreibung) {
        this.egBeschreibung = egBeschreibung;
    }

    /**
     * @return egId
     */
    public Long getEgId() {
        return egId;
    }

    /**
     * @param egId Festzulegender egId
     */
    public void setEgId(Long egId) {
        this.egId = egId;
    }

    /**
     * @return egName
     */
    public String getEgName() {
        return egName;
    }

    /**
     * @param egName Festzulegender egName
     */
    public void setEgName(String egName) {
        this.egName = egName;
    }

    /**
     * @return egTyp
     */
    public Long getEgTyp() {
        return egTyp;
    }

    /**
     * @param egTyp Festzulegender egTyp
     */
    public void setEgTyp(Long egTyp) {
        this.egTyp = egTyp;
    }

    /**
     * @return ipAdresse
     */
    public String getIpAdresse() {
        return ipAdresse;
    }

    /**
     * @param ipAdresse Festzulegender ipAdresse
     */
    public void setIpAdresse(String ipAdresse) {
        this.ipAdresse = ipAdresse;
    }

    /**
     * @return macAdresse
     */
    public String getMacAdresse() {
        return macAdresse;
    }

    /**
     * @param macAdresse Festzulegender macAdresse
     */
    public void setMacAdresse(String macAdresse) {
        this.macAdresse = macAdresse;
    }

    /**
     * @return seriennummer
     */
    public String getSeriennummer() {
        return seriennummer;
    }

    /**
     * @param seriennummer Festzulegender seriennummer
     */
    public void setSeriennummer(String seriennummer) {
        this.seriennummer = seriennummer;
    }

    /**
     * @return verlaufId
     */
    public Long getVerlaufId() {
        return verlaufId;
    }

    /**
     * @param verlaufId Festzulegender verlaufId
     */
    public void setVerlaufId(Long verlaufId) {
        this.verlaufId = verlaufId;
    }

    /**
     * @return iadId
     */
    public Long getIadId() {
        return iadId;
    }

    /**
     * @param iadId Festzulegender iadId
     */
    public void setIadId(Long iadId) {
        this.iadId = iadId;
    }


}


