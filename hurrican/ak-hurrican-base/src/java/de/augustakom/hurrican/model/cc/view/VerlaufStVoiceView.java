/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2005 16:20:35
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.RNModel;


/**
 * View-Klasse fuer den el. Verlauf der Abteilung EWSD.
 *
 *
 */
public class VerlaufStVoiceView extends AbstractBauauftragView implements RNModel {

    private String bearbeiterSTVoice = null;
    private String switchEsA = null;
    private String switchEsB = null;
    private String hauptRN = null;
    private String carrier = null;
    private Date carrierBereitstellung = null;
    private Boolean voIP = null;

    /**
     * @return Returns the hauptRN.
     */
    public String getHauptRN() {
        return hauptRN;
    }

    /**
     * @param hauptRN The hauptRN to set.
     */
    public void setHauptRN(String hauptRN) {
        this.hauptRN = hauptRN;
    }

    /**
     * @return Returns the switchEsA.
     */
    public String getSwitchEsA() {
        return switchEsA;
    }

    /**
     * @param switchEsA The switchEsA to set.
     */
    public void setSwitchEsA(String switchEsA) {
        this.switchEsA = switchEsA;
    }

    /**
     * @return Returns the switchEsB.
     */
    public String getSwitchEsB() {
        return switchEsB;
    }

    /**
     * @param switchEsB The switchEsB to set.
     */
    public void setSwitchEsB(String switchEsB) {
        this.switchEsB = switchEsB;
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
     * Flag gibt an, ob der zugehoerige Auftrag VoIP-Daten besitzt.
     *
     * @return Returns the voIP.
     */
    public Boolean getVoIP() {
        return voIP;
    }

    /**
     * @param voIP The voIP to set.
     */
    public void setVoIP(Boolean voIP) {
        this.voIP = voIP;
    }

    public String getBearbeiterSTVoice() {
        return bearbeiterSTVoice;
    }

    public void setBearbeiterSTVoice(String bearbeiterSTVoice) {
        this.bearbeiterSTVoice = bearbeiterSTVoice;
    }

}


