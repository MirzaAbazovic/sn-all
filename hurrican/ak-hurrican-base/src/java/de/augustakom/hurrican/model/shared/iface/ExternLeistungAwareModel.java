/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2006 07:54:23
 */
package de.augustakom.hurrican.model.shared.iface;


/**
 * Interface fuer Modelle, die eine Konfiguration fuer eine externe Leistung besitzen.
 *
 *
 */
public interface ExternLeistungAwareModel {

    /**
     * Gibt die Leistungs-ID fuer ein externes System zurueck.
     *
     * @return Die Leistungs-ID fuer ein externes System.
     *
     */
    public Long getExternLeistungNo();

}


