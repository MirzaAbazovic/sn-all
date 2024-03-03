/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2006 10:21:48
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.iface.QueryService;


/**
 * Query-Service fuer die CC-Datenbank.
 *
 *
 */
public interface QueryCCService extends ICCService, QueryService, ISimpleFindService {

    /**
     * Ermittelt alle Objekte, die dem Example-Objekt entsprechen.
     *
     * @param example
     * @param clazz
     * @param orderAsc  Parameter, die eine aufsteigende Sortierung erhalten sollen
     * @param orderDesc Parameter, die eine absteigende Sortierung erhalten sollen
     * @return
     * @throws FindException
     */
    public <T> List<T> findByExample(Object example, Class<T> clazz, String[] orderAsc, String[] orderDesc)
            throws FindException;

    /**
     * @param example Example-Objekt
     * @param clazz   Example-Klasse
     * @return 'passendes' Objekt
     * @throws FindException wenn ein Fehler auftritt oder mehr als ein Datensatz gefunden wird.
     * @see findByExample(Object, Class) Die Methode geht davon aus, dass ueber das Example-Objekt nur ein Objekt
     * gefunden wird. Falls mehr als ein Objekt ermittelt wird, wird eine FindException generiert.
     */
    public <T> T findUniqueByExample(Object example, Class<T> clazz) throws FindException;
}


