/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2005 13:39:48
 */
package de.augustakom.hurrican.model.cc.view;


/**
 * Bauauftrags-View fuer die Abteilung IPS.
 *
 *
 */
public class VerlaufStOnlineView extends AbstractBauauftragView implements TimeSlotAware {

    private Long vpnNr = null;
    private boolean erledigt = false;
    private String bearbeiterIPS = null;
    private String schnittstelle = null;
    private String leitungA = null;
    private String leitungB = null;
    private String dslamProfile = null;
    private Boolean kreuzung = null;
    private String geraeteBez;
    private TimeSlotHolder timeSlot = new TimeSlotHolder();

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
     * @return Returns the erledigt.
     */
    public boolean isErledigt() {
        return erledigt;
    }

    /**
     * @param erledigt The erledigt to set.
     */
    public void setErledigt(boolean erledigt) {
        this.erledigt = erledigt;
    }

    /**
     * @return bearbeiterIPS
     */
    public String getBearbeiterIPS() {
        return bearbeiterIPS;
    }

    /**
     * @param bearbeiterIPS Festzulegender bearbeiterIPS
     */
    public void setBearbeiterIPS(String bearbeiterIPS) {
        this.bearbeiterIPS = bearbeiterIPS;
    }

    /**
     * @return dslamProfile
     */
    public String getDslamProfile() {
        return dslamProfile;
    }

    /**
     * @param dslamProfile Festzulegender dslamProfile
     */
    public void setDslamProfile(String dslamProfile) {
        this.dslamProfile = dslamProfile;
    }

    /**
     * @return schnittstelle
     */
    public String getSchnittstelle() {
        return schnittstelle;
    }

    /**
     * @param schnittstelle Festzulegender schnittstelle
     */
    public void setSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
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
     * @return Returns the kreuzung.
     */
    public Boolean getKreuzung() {
        return kreuzung;
    }

    public void setKreuzung(Boolean kreuzung) {
        this.kreuzung = kreuzung;
    }

    public String getGeraeteBez() {
        return geraeteBez;
    }

    public void setGeraeteBez(String geraeteBez) {
        this.geraeteBez = geraeteBez;
    }

    public TimeSlotHolder getTimeSlot() {
        return timeSlot;
    }
}
