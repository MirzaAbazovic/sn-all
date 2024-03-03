/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.2005 13:57:59
 */
package de.augustakom.hurrican.model.shared.iface;


/**
 * Interface fuer Modelle/Views, die einen Auftrags-Status besitzen.
 *
 *
 */
public interface CCAuftragStatusModel {

    /**
     * @param statusId
     */
    public void setAuftragStatusId(Long statusId);

    /**
     * Gibt den Auftrags-Status zurueck.
     *
     * @return
     */
    public Long getAuftragStatusId();

}


