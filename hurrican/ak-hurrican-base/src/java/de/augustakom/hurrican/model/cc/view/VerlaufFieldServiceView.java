/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2005 14:40:21
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;


/**
 * View-Klasse fuer den el. Verlauf der Abteilung FieldService.
 *
 *
 */
public class VerlaufFieldServiceView extends AbstractBauauftragView implements TimeSlotAware {

    private String schnittstelle = null;
    private String endstelleOrtB = null;
    private String leitungA = null;
    private String leitungB = null;
    private Date carrierBereitstellung = null;
    private String carrier = null;
    private Long vpnNr = null;
    private String bearbeiter = null;
    private Boolean kreuzung = null;
    private String anschlussArt = null;
    private String dpoChassis;
    private String geraeteBez;
    private TimeSlotHolder timeSlot = new TimeSlotHolder();

    /**
     * @return Returns the endstelleOrtB.
     */
    @Override
    public String getEndstelleOrtB() {
        return endstelleOrtB;
    }

    /**
     * @param endstelleOrtB The endstelleOrtB to set.
     */
    @Override
    public void setEndstelleOrtB(String endstelleOrtB) {
        this.endstelleOrtB = endstelleOrtB;
    }

    /**
     * @return Returns the leitungA.
     */
    public String getLeitungA() {
        return leitungA;
    }

    /**
     * @param leitungA The leitungA to set.
     */
    public void setLeitungA(String leitungA) {
        this.leitungA = leitungA;
    }

    /**
     * @return Returns the leitungB.
     */
    public String getLeitungB() {
        return leitungB;
    }

    /**
     * @param leitungB The leitungB to set.
     */
    public void setLeitungB(String leitungB) {
        this.leitungB = leitungB;
    }

    /**
     * @return Returns the schnittstelle.
     */
    public String getSchnittstelle() {
        return schnittstelle;
    }

    /**
     * @param schnittstelle The schnittstelle to set.
     */
    public void setSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
    }

    /**
     * @return Returns the carrier.
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * @param carrier The carrier to set.
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /**
     * @return Returns the carrierBereitstellung.
     */
    public Date getCarrierBereitstellung() {
        return carrierBereitstellung;
    }

    /**
     * @param carrierBereitstellung The carrierBereitstellung to set.
     */
    public void setCarrierBereitstellung(Date carrierBereitstellung) {
        this.carrierBereitstellung = carrierBereitstellung;
    }

    /**
     * @return Returns the vpnNr.
     */
    public Long getVpnNr() {
        return vpnNr;
    }

    /**
     * @param vpnNr The vpnNr to set.
     */
    public void setVpnNr(Long vpnNr) {
        this.vpnNr = vpnNr;
    }

    /**
     * @return Returns the bearbeiter.
     */
    public String getBearbeiter() {
        return bearbeiter;
    }

    /**
     * @param bearbeiter The bearbeiter to set.
     */
    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    /**
     * @return Returns the kreuzung.
     */
    public Boolean getKreuzung() {
        return kreuzung;
    }

    /**
     * @param kreuzung The kreuzung to set.
     */
    public void setKreuzung(Boolean kreuzung) {
        this.kreuzung = kreuzung;
    }

    public String getAnschlussArt() {
        return anschlussArt;
    }

    public void setAnschlussArt(String anschlussArt) {
        this.anschlussArt = anschlussArt;
    }

    public String getGeraeteBez() {
        return geraeteBez;
    }

    public void setGeraeteBez(String geraeteBez) {
        this.geraeteBez = geraeteBez;
    }

    public String getDpoChassis() {
        return dpoChassis;
    }

    public void setDpoChassis(String dpoChassis) {
        this.dpoChassis = dpoChassis;
    }

    public TimeSlotHolder getTimeSlot() {
        return timeSlot;
    }
}


