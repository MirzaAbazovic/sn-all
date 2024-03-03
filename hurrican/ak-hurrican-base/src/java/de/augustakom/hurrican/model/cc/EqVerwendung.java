/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.02.2010 13:37:57
 */
package de.augustakom.hurrican.model.cc;


/**
 * Verwendungszwecke fuer Ports
 *
 *
 */
public enum EqVerwendung {
    // Bei Erweiterung Trigger TRBIU_EQUIPMENT (equipmentTrigger.sql) anpassen

    /**
     * Normaler Port: STANDARD oder null
     */
    STANDARD("Standard"),
    /**
     * Mit CONNECT werden DTAG-Ports markiert, die fuer Connect-Verbindungen vorgesehen sind
     */
    CONNECT("Connect");

    private final String display;

    private EqVerwendung(String display) {
        this.display = display;
    }

    @Override
    /** @return spezielle Bezeichnung zum Anzeigen des Enum */
    public String toString() {
        return display;
    }
}
