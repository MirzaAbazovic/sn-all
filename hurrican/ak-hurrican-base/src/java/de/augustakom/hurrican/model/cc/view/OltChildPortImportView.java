/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.augustakom.hurrican.model.cc.view;

/**
 *
 */
public abstract class OltChildPortImportView {

    private String oltChild = null;
    private String schnittstelle = null;
    private String port = null;

    public String getOltChild() {
        return oltChild;
    }

    public void setOltChild(String oltChild) {
        this.oltChild = oltChild;
    }

    public String getSchnittstelle() {
        return schnittstelle;
    }

    public void setSchnittstelle(String schnittstelle) {
        this.schnittstelle = schnittstelle;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

}
