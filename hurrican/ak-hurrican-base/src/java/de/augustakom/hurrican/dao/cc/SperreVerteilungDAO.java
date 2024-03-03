/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2004 11:40:24
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.SperreVerteilung;


/**
 * DAO-Interface fuer Objekte des Typs <code>SperreVerteilung</code>.
 *
 *
 */
public interface SperreVerteilungDAO {

    /**
     * Sucht nach allen Sperre-Verteilungen, die einem best. Produkt zugeordnet sind.
     *
     * @param prodId ID des Produkts, dessen Sperre-Verteilungen gesucht werden.
     * @return Liste mit Objekten des Typs <code>SperreVerteilung</code>.
     */
    public List<SperreVerteilung> findByProduktId(Long prodId);

    /**
     * Loescht alle Sperre-Verteilungen, die einem best. Produkt zugeordnet sind.
     *
     * @param produktId ID des Produkts, dessen Sperre-Verteilungen geloescht werden sollen.
     */
    public void deleteVerteilungen4Produkt(Long produktId);

    /**
     * Speichert die Liste mit den Sperre-Verteilungen.
     *
     * @param toSave Liste mit Objekten des Typs <code>SperreVerteilung</code> die gespeichert werden sollen.
     */
    public void saveSperreVerteilungen(List<SperreVerteilung> toSave);

}


