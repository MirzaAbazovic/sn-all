/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2004 08:02:47
 */
package de.augustakom.hurrican.model.shared.iface;


/**
 * Interface fuer alle Modelle, die ein Long-Objekt als ID besitzen.
 *
 *
 */
public interface LongIdModel {

    /**
     * Setzt die ID des Modells.
     *
     * @param id
     */
    public void setId(Long id);

    /**
     * Gibt die ID des Modells zurueck.
     *
     * @return
     */
    public Long getId();

}


