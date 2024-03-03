/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 12:27:07
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.base.iface.HistoryUpdateDAO;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>Rangierungsmatrix</code>.
 *
 *
 */
public interface RangierungsmatrixDAO extends StoreDAO, FindDAO, HistoryUpdateDAO<Rangierungsmatrix> {

    /**
     * Sucht nach allen (aktuellen) Rangierungsmatrix-Eintraegen, die den Query-Parametern entsprechen. <br> Die Liste
     * wird nach der Prioritaet sortiert (descending, nulls last)!  <br/>
     * Dabei hat die Prioritaet der Rangierungsmatrix Vorrang vor der Prioritaet auf dem Produkt/Physiktyp Mapping.
     * (Dies ist notwendig, um weiterhin die Prioritaet standort-abhaengig uebersteuern zu koennen.)
     *
     * @param query Query-Objekt mit den Suchparametern.
     * @return Liste mit Objekten des Typs <code>Rangierungsmatrix</code>.
     */
    public List<Rangierungsmatrix> findByQuery(RangierungsmatrixQuery query);

}


