/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2006 10:19:44
 */
package de.augustakom.hurrican.service.base.iface;

import java.util.*;

import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Interface fuer generelle Query-Services.
 *
 *
 */
public interface QueryService {

    /**
     * Fuehrt auf der aktuellen DB (abhaengig vom Service-Bereich) eine SQL-Abfrage aus.
     *
     * @param sql    SQL-Statement
     * @param columnNames Namen der abgefragten Spalten im DB-ResultSet
     * @param params Parameter fuer das SQL-Statement
     * @return Liste mit Objekten des Typs <code>Map</code>. Jede Map stellt einen Datensatz des Query-Ergebnisses dar.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Map<String, Object>> query(String sql, String[] columnNames, Object[] params) throws FindException;

}


