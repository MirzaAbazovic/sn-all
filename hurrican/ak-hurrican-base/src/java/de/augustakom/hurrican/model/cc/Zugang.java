/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2004 15:09:01
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Modell bildet Zugangsdaten fuer Remote-Konfigurationen ab.
 *
 *
 */
public class Zugang extends AbstractCCIDModel implements CCAuftragModel {

    private Long auftragId = null;
    private String einwahlrufnummer = null;
    private String art = null;

    /**
     * @return Returns the art.
     */
    public String getArt() {
        return art;
    }

    /**
     * @param art The art to set.
     */
    public void setArt(String art) {
        this.art = art;
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the einwahlrufnummer.
     */
    public String getEinwahlrufnummer() {
        return einwahlrufnummer;
    }

    /**
     * @param einwahlrufnummer The einwahlrufnummer to set.
     */
    public void setEinwahlrufnummer(String einwahlrufnummer) {
        this.einwahlrufnummer = einwahlrufnummer;
    }

}


