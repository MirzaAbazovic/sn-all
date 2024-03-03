/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.2005 11:50:16
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKDb;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>AKDb</code>.
 */
public interface AKDbDAO {

    /**
     * Sucht nach allen angelegten DB-Datensaetzen.
     *
     * @return (never {@code null})
     */
    List<AKDb> findAll();

    /**
     * Erzeugt oder aktualisiert das AKDb-Objekt.
     */
    void saveOrUpdate(AKDb db);

    /**
     * Loescht die DB mit der angegebenen ID.
     */
    void delete(final Long id);
}
