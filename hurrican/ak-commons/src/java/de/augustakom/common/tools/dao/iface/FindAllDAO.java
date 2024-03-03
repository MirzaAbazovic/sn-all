/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 10:02:51
 */
package de.augustakom.common.tools.dao.iface;

import java.util.*;

import de.augustakom.common.tools.dao.OrderByProperty;

/**
 * Interface definiert Methoden fuer DAO-Implementierungen, die alle Objekte/Datensaezte eines best. Typs finden/laden.
 * <br> Evtl. auftretende Exceptions werden als RuntimeExceptions weitergeleitet.
 *
 *
 */
public interface FindAllDAO {

    /**
     * Findet alle Objekte/Datensaetze eines best. Typs.
     *
     * @param type Typ der gesuchten Objekte.
     * @return List mit den gefundenen/geladenen Objekte.
     */
    <T> List<T> findAll(Class<T> type);

    /**
     * Findet alle Objekte/Datensaetze eines bestimmten Typs. Je nach spezifizierten orderByProperties wird die Liste
     * sortiert
     *
     * @param type              Typ der gesuchten Objekte.
     * @param orderByProperties Liste aller Attribute nach denen die Liste der gefundenen Objekten sortiert werden
     *                          muss.
     * @return List mit den gefundenen/geladenen Objekte.
     */
    <T> List<T> findAllOrdered(Class<T> type, OrderByProperty... orderByProperties);

}
