/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2005 16:07:01
 */
package de.augustakom.hurrican.model.cc.view;


/**
 * View-Klasse fuer den el. Verlauf der Abteilung 'SDH'.
 *
 *
 */
public class VerlaufStConnectView extends AbstractBauauftragView {

    private String produktName = null; //= Produktname Hurrican
    private String bearbeiterSDH = null;
    private String schnittstelle = null;
    private String leitungA = null;
    private String leitungB = null;
    private Long vpnNr = null;
    private String dslamProfile = null;

    /**
     * @return Returns the bearbeiterSDH.
     */
    public String getBearbeiterSDH() {
        return bearbeiterSDH;
    }

    /**
     * @param bearbeiterSDH The bearbeiterSDH to set.
     */
    public void setBearbeiterSDH(String bearbeiterSDH) {
        this.bearbeiterSDH = bearbeiterSDH;
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
     * @return Returns the produktName.
     */
    @Override
    public String getProduktName() {
        return produktName;
    }

    /**
     * @param produktName The produktName to set.
     */
    @Override
    public void setProduktName(String produktName) {
        this.produktName = produktName;
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
     * @return Returns the dslamProfile.
     */
    public String getDslamProfile() {
        return dslamProfile;
    }

    /**
     * @param dslamProfile The dslamProfile to set.
     */
    public void setDslamProfile(String dslamProfile) {
        this.dslamProfile = dslamProfile;
    }
}


