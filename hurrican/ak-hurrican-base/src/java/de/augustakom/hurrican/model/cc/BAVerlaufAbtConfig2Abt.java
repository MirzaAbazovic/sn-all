/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 09:51:58
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell-Klasse, um Abteilungen einer Abteilungs-Konfiguration zuzuordnen.
 *
 *
 */
public class BAVerlaufAbtConfig2Abt extends AbstractCCIDModel {

    private Long abtConfigId = null;
    private Long abtId = null;

    /**
     * @return Returns the abtConfigId.
     */
    public Long getAbtConfigId() {
        return this.abtConfigId;
    }

    /**
     * @param abtConfigId The abtConfigId to set.
     */
    public void setAbtConfigId(Long abtConfigId) {
        this.abtConfigId = abtConfigId;
    }

    /**
     * @return Returns the abtId.
     */
    public Long getAbtId() {
        return this.abtId;
    }

    /**
     * @param abtId The abtId to set.
     */
    public void setAbtId(Long abtId) {
        this.abtId = abtId;
    }

}


