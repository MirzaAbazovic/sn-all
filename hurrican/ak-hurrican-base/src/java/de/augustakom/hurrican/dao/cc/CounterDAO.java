/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2004 10:42:11
 */
package de.augustakom.hurrican.dao.cc;


/**
 * DAO-Interface fuer die Verwaltung von Countern.
 *
 *
 */
public interface CounterDAO {

    /**
     * Liest den aktuellen Integer-Wert fuer den Counter <code>counterName</code> aus, erhoeht diesen und gibt den neuen
     * Wert zurueck.
     *
     * @param counterName Name des Counters
     * @return neuer Counter-Wert vom Typ Integer.
     */
    public Integer incrementIntValue(String counterName);

}


