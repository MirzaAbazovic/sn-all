/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2007 16:39:02
 */
package de.augustakom.hurrican.model.exmodules.tal;


/**
 * Abstraktes Klasse fuer alle Modelle, die eine Referenz auf eine bestimmte TAL-Bestellung besitzen.
 *
 *
 */
public class AbstractTALBestellungModel extends AbstractTALModel {

    private Long talBestellungId = null;

    /**
     * @return Returns the talBestellungId.
     */
    public Long getTalBestellungId() {
        return talBestellungId;
    }

    /**
     * @param talBestellungId The talBestellungId to set.
     */
    public void setTalBestellungId(Long talBestellungId) {
        this.talBestellungId = talBestellungId;
    }

}


