/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2004 09:32:52
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Modell bildet Daten fuer einen Mehrwertdienst ab.
 *
 *
 */
public class AK0800 extends AbstractCCHistoryModel implements CCAuftragModel {

    private Long auftragId = null;
    private Date auftragsEingang = null;
    private Boolean uebernahme = null;
    private Boolean neuschaltung = null;
    private Date wunschtermin = null;
    private String codeNummer = null;
    private Date kuendigung = null;
    private Date inbetriebnahme = null;
    private Date wirksamAb = null;
    private Date regAn = null;

    /**
     * @return Returns the auftragId.
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the auftragsEingang.
     */
    public Date getAuftragsEingang() {
        return auftragsEingang;
    }

    /**
     * @param auftragsEingang The auftragsEingang to set.
     */
    public void setAuftragsEingang(Date auftragsEingang) {
        this.auftragsEingang = auftragsEingang;
    }

    /**
     * @return Returns the codeNummer.
     */
    public String getCodeNummer() {
        return codeNummer;
    }

    /**
     * @param codeNummer The codeNummer to set.
     */
    public void setCodeNummer(String codeNummer) {
        this.codeNummer = codeNummer;
    }

    /**
     * @return Returns the inbetriebnahme.
     */
    public Date getInbetriebnahme() {
        return inbetriebnahme;
    }

    /**
     * @param inbetriebnahme The inbetriebnahme to set.
     */
    public void setInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
    }

    /**
     * @return Returns the kuendigung.
     */
    public Date getKuendigung() {
        return kuendigung;
    }

    /**
     * @param kuendigung The kuendigung to set.
     */
    public void setKuendigung(Date kuendigung) {
        this.kuendigung = kuendigung;
    }

    /**
     * @return Returns the neuschaltung as boolean
     */
    public boolean isNeuschaltung() {
        return (neuschaltung != null) ? neuschaltung.booleanValue() : false;
    }

    /**
     * @param neuschaltung The neuschaltung to set.
     */
    public void setNeuschaltung(Boolean neuschaltung) {
        this.neuschaltung = neuschaltung;
    }

    /**
     * @param neuschaltung The neuschaltung to set.
     */
    public void setNeuschaltung(boolean neuschaltung) {
        this.neuschaltung = Boolean.valueOf(neuschaltung);
    }

    /**
     * @return Returns the regAn.
     */
    public Date getRegAn() {
        return regAn;
    }

    /**
     * @param regAn The regAn to set.
     */
    public void setRegAn(Date regAn) {
        this.regAn = regAn;
    }

    /**
     * @return Returns the uebernahme as boolean
     */
    public boolean isUebernahme() {
        return (uebernahme != null) ? uebernahme.booleanValue() : false;
    }

    /**
     * @param uebernahme The uebernahme to set.
     */
    public void setUebernahme(Boolean uebernahme) {
        this.uebernahme = uebernahme;
    }

    /**
     * @param uebernahme The uebernahme to set.
     */
    public void setUebernahme(boolean uebernahme) {
        this.uebernahme = Boolean.valueOf(uebernahme);
    }

    /**
     * @return Returns the wirksamAb.
     */
    public Date getWirksamAb() {
        return wirksamAb;
    }

    /**
     * @param wirksamAb The wirksamAb to set.
     */
    public void setWirksamAb(Date wirksamAb) {
        this.wirksamAb = wirksamAb;
    }

    /**
     * @return Returns the wunschtermin.
     */
    public Date getWunschtermin() {
        return wunschtermin;
    }

    /**
     * @param wunschtermin The wunschtermin to set.
     */
    public void setWunschtermin(Date wunschtermin) {
        this.wunschtermin = wunschtermin;
    }

}
