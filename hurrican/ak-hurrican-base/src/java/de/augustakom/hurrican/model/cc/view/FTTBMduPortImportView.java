/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.07.2009 09:43:08
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;


/**
 * View-Modell, fuer den Import der MDU-Ports
 *
 *
 */
public class FTTBMduPortImportView {

    /**
     * Konstanten
     */
    public static final int PORTS_FIRST_ROW = 1;
    public static final int PORTS_POS_MDU = 0;
    public static final int PORTS_POS_HWVARIANTE = 1;
    public static final int PORTS_POS_PORT = 2;
    public static final int PORTS_POS_SCHNITTSTELLE = 3;
    public static final int PORTS_POS_LEISTE = 4;
    public static final int PORTS_POS_STIFT = 5;
    public static final int PORTS_POS_GUELTIGAB = 6;


    private String mdu = null;
    private String bgTyp = null;
    private String modulNummer = null;
    private String hwVariante = null;
    private String port = null;
    private String schnittstelle = null;
    private String leiste = null;
    private String stift = null;
    private Date gueltigAb = null;

    public String getBgTyp() {
        return bgTyp;
    }

    public void setBgTyp(String bgTyp) {
        this.bgTyp = bgTyp;
    }

    public String getModulNummer() {
        return modulNummer;
    }

    public void setModulNummer(String modulNummer) {
        this.modulNummer = modulNummer;
    }

    /**
     * @return the gueltigAb
     */
    public Date getGueltigAb() {
        return gueltigAb;
    }

    /**
     * @return the mdu
     */
    public String getMdu() {
        return mdu;
    }

    /**
     * @param mdu the mdu to set
     */
    public void setMdu(String mdu) {
        this.mdu = mdu;
    }

    /**
     * @return the hwVariante
     */
    public String getHwVariante() {
        return hwVariante;
    }

    /**
     * @param hwVariante the hwVariante to set
     */
    public void setHwVariante(String hwVariante) {
        this.hwVariante = hwVariante;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the schnittstelle
     */
    public String getSchnittstelle() {
        return schnittstelle;
    }

    /**
     * @param schnittstelle the schnittstelle to set
     */
    public void setSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
    }

    /**
     * @return the leiste
     */
    public String getLeiste() {
        return leiste;
    }

    /**
     * @param leiste the leiste to set
     */
    public void setLeiste(String leiste) {
        this.leiste = leiste;
    }

    /**
     * @return the stift
     */
    public String getStift() {
        return stift;
    }

    /**
     * @param stift the stift to set
     */
    public void setStift(String stift) {
        this.stift = stift;
    }

    /**
     * @param gueltigAb the gueltigAb to set
     */
    public void setGueltigAb(Date gueltigAb) {
        this.gueltigAb = gueltigAb;
    }

}


