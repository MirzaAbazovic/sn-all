/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 09:49:49
 */
package de.augustakom.common.tools.dao.iface;

import java.io.*;

/**
 * Interface definiert Methoden fuer DAO-Implementierungen, die Objekte eines best. Typs speichern. <br> Evtl.
 * auftretende Exceptions werden von den DAOs als Runtime-Exception weitergeleitet.
 *
 *
 */
public interface StoreDAO {

    /**
     * Speichert das Objekt <code>toStore</code>. <br> Ist das Objekt bereits persistent, wird eine Update-Operation
     * durchgefuehrt. Ist das Objekt noch nicht persistent wird eine Insert-Operation verwendet. (Die einzelnen
     * Implementierungen koennen von dieser Beschreibung jedoch evtl. abweichen!)
     *
     * @param toStore zu speicherndes Objekt
     */
    public <T extends Serializable> T store(T toStore);

    /**
     * Speichert das Objekt <code>toMerge</code>. Dabei wird ein hibernate merge ausgeführt.
     * @param toMerge
     */
    public <T extends Serializable> T merge(T toMerge);

    /**
     * Flushing the session and catching the thrown Exceptions.
     */
    public void flushSession();

    /**
     * Flushing the session and propagated Exceptions back to the caller.
     */
    public void flushSessionLoud();

}
