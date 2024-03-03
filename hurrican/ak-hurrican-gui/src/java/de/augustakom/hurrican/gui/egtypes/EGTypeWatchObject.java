/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 13:19:13
 */
package de.augustakom.hurrican.gui.egtypes;

import java.io.Serializable;

/**
 * Watch Object, das Endgerätetypen ohne egGruppen abbildet
 */
public class EGTypeWatchObject implements Serializable {
    private String hersteller;
    private String modell;
    private String certifiedSwitches;

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getModell() {
        return modell;
    }

    public void setModell(String modell) {
        this.modell = modell;
    }

    public String getCertifiedSwitches() {
        return certifiedSwitches;
    }

    public void setCertifiedSwitches(String certifiedSwitches) {
        this.certifiedSwitches = certifiedSwitches;
    }
}


