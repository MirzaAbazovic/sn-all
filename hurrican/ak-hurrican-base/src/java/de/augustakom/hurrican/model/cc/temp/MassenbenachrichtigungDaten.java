/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2012 09:03:03
 */
package de.augustakom.hurrican.model.cc.temp;

import de.augustakom.hurrican.model.cc.AuftragDaten;

/**
 * DTO fuer die
 */
public class MassenbenachrichtigungDaten {

    private AuftragDaten auftragDaten;
    private Long port;
    private String karte;
    private String rack;

    public AuftragDaten getAuftragDaten() {
        return auftragDaten;
    }

    public void setAuftragDaten(AuftragDaten auftragDaten) {
        this.auftragDaten = auftragDaten;
    }

    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    public String getKarte() {
        return karte;
    }

    public void setKarte(String karte) {
        this.karte = karte;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

}


