/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 12:38:28
 */
package de.augustakom.common.service.iface;

import java.io.*;
import java.util.*;


/**
 * Interface definiert Methoden fuer eine Service-Implementierung, die alle Objekte eines bestimmten Typs laden/finden
 * kann.
 *
 *
 */
public interface ISimpleFindService {

    /**
     * Veranlasst den Service dazu, alle Objekte eines best. Typs zu laden.
     *
     * @param clazz Typ, von dem alle Objekte geladen werden sollen
     * @return Liste mit allen gefundenen Objekten.
     * @throws Exception wenn bei der Suche ein Fehler auftritt
     */
    public <T> List<T> findAll(Class<T> clazz) throws Exception;

    /**
     * Sucht nach Objekten des Typs <code>clazz</code> mit den Parametern <code>params</code>. <br><br> Die
     * Implementierung ist dafuer verantwortlich, ob ueber das Example-Objekt auch mit Wildcards gesucht werden kann.
     *
     * @param example Beispiel-Objekt mit den Suchparamtern.
     * @param clazz   zu ermittelnder Typ.
     * @return
     * @throws Exception
     *
     */
    public <T> List<T> findByExample(Object example, Class<T> clazz) throws Exception;

    /**
     * Veranlasst den Service dazu, ein Objekt von einem bestimmten Typ mit einer bestimmten ID zu laden.
     *
     * @param id
     * @param clazz
     * @return
     * @throws Exception
     *
     */
    public <T> T findById(Serializable id, Class<T> clazz) throws Exception;

}


