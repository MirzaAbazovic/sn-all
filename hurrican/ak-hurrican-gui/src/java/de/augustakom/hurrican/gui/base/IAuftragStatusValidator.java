/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2004 10:03:50
 */
package de.augustakom.hurrican.gui.base;


/**
 * Interface fuer GUI-Klassen, die abhaengig vom Auftrags-Status validiert werden muessen.
 *
 *
 */
public interface IAuftragStatusValidator {

    /**
     * Implementierungen validieren abhaengig von <code>auftragStatus</code> die GUI-Komponenten.
     *
     * @param auftragStatus
     */
    public void validate4Status(Long auftragStatus);

}


