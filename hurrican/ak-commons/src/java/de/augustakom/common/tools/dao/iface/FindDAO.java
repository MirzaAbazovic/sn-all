/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 10:00:57
 */
package de.augustakom.common.tools.dao.iface;

import java.io.*;
import java.util.*;

import de.augustakom.common.tools.dao.FilterByProperty;
import de.augustakom.common.tools.dao.OrderByProperty;


/**
 * Interface definiert Methoden fuer DAO-Implementierungen, die ein Objekt ueber dessen eindeutige ID suchen. <br> Evtl.
 * auftretende Exceptions werden von den Implementierungen als RuntimeException weitergeleitet.
 */
public interface FindDAO {


    /**
     * Sucht über eine customized HQL query und einer Liste von Parametern. Die Typen der Parameter werden
     * automatisch der Klassentypen ermittelt. Zusaetzlich kann das Resultset auf eine maximale Groesse begrenzt
     * werden.
     *
     * @param maxResultSize wenn {@code null}, wird keine Result Size angegeben
     */
    <T> List<T> find(String hqlQuery, Integer maxResultSize, Object ... parameters);

    /**
     * Sucht über eine customized HQL query und einer Liste von Parametern. Die Typen der Parameter werden
     * automatisch der Klassentypen ermittelt.
     */
    <T> List<T> find(String hqlQuery, Object ... parameters);

    <T> List<T> find(int maxResult, String hqlQuery, Object ... parameters);

    <T> List<T> find(int maxResult, String hqlQuery, Map<String, Object> params);

    /**
     * Sucht nach einem best. Objekt ueber dessen ID.
     */
    <T> T findById(Serializable id, Class<T> type);

    /**
     * Findet alle Objekte/Datensaetze eines bestimmten Typs. Je nach spezifizierten orderByProperties wird die Liste
     * sortiert
     *
     * @param type               Typ der gesuchten Objekte.
     * @param filterByProperties Angabe der Properties, nach denen gefiltert werden soll (EQUAL Vergleich!)
     * @param orderByProperties  Liste aller Attribute nach denen die Liste der gefundenen Objekten sortiert werden
     *                           muss.
     * @return List mit den gefundenen/geladenen Objekte.
     */
    <T> List<T> findFilteredAndOrdered(Class<T> type, List<FilterByProperty> filterByProperties, OrderByProperty... orderByProperties);

    /**
     * Sucht nach Objekten ueber eine bestimmte Property.
     *
     * @param type     Typ der gesuchten Objekte
     * @param property Name der Property
     * @param value    Wert der Property
     * @return Liste an Objekten des gesuchten Typs, nie {@code null}
     */
    <T> List<T> findByProperty(Class<T> type, String property, Object value);

    void refresh(Object entity);
}


