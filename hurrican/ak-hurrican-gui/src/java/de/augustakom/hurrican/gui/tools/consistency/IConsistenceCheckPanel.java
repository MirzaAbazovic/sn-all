/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2005 14:48:04
 */
package de.augustakom.hurrican.gui.tools.consistency;


/**
 * Interface fuer ConsistenceCheck-Panels.
 *
 *
 */
public interface IConsistenceCheckPanel {

    /**
     * Fuehrt die eigentlich Konsistenz-Pruefung mit Hilfe des SwingWorker durch.
     */
    void executeCheckConsistence();

    /**
     * Bricht die Konsistenz-Pruefung ab.
     */
    void cancelCheckConsistence();
}


