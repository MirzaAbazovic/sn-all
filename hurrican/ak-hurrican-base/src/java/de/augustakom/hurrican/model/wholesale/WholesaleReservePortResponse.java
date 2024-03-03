/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 16:29:09
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;

/**
 * Response-Objekt fuer die reservePort Methode des Wholesale-Services.
 */
public class WholesaleReservePortResponse {

    /**
     * MNet generierte eindeutige Bezeichnung fuer die TAL
     */
    private String lineId;

    /**
     * Port wird zu jetzt reserviert, der {@code executionDate} gibt Hurrican die {@code desiredExecutionDate} wieder
     * mit
     */
    private LocalDate executionDate;

    /**
     * Generierte eindeutige AuftragId in Hurrican.
     */
    private String hurricanAuftragId;

    private String a10nsp;

    private String a10nspPort;

    private String svlanEkp;

    private boolean dpoNochNichtVerbaut;

    /**
     * Flag, ob es sich um eine manuelle Portzuweisung handelt.
     */
    private boolean manuellePortzuweisung;

    public boolean isDpoNochNichtVerbaut() {
        return dpoNochNichtVerbaut;
    }

    public void setDpoNochNichtVerbaut(boolean dpoNochNichtVerbaut) {
        this.dpoNochNichtVerbaut = dpoNochNichtVerbaut;
    }

    public String getA10nsp() {
        return a10nsp;
    }

    public void setA10nsp(String a10nsp) {
        this.a10nsp = a10nsp;
    }

    public String getA10nspPort() {
        return a10nspPort;
    }

    public void setA10nspPort(String a10nspPort) {
        this.a10nspPort = a10nspPort;
    }

    public String getSvlanEkp() {
        return svlanEkp;
    }

    public void setSvlanEkp(String svlanEkp) {
        this.svlanEkp = svlanEkp;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public String getHurricanAuftragId() {
        return hurricanAuftragId;
    }

    public void setHurricanAuftragId(String hurricanAuftragId) {
        this.hurricanAuftragId = hurricanAuftragId;
    }

    public boolean isManuellePortzuweisung() {
        return manuellePortzuweisung;
    }

    public void setManuellePortzuweisung(boolean manuellePortzuweisung) {
        this.manuellePortzuweisung = manuellePortzuweisung;
    }
}
