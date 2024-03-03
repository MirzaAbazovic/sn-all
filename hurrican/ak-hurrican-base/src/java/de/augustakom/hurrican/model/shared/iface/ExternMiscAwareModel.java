/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2006 07:54:23
 */
package de.augustakom.hurrican.model.shared.iface;


/**
 * Interface fuer Modelle, die eine Konfiguration fuer verschiedene Aktionen von externen Systemen besitzen.
 *
 *
 */
public interface ExternMiscAwareModel {

    /**
     * Gibt eine ID fuer ein externes System zurueck.
     *
     * @return
     *
     */
    public Long getExternMiscNo();

}


