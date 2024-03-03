/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2004 14:56:17
 */
package de.augustakom.hurrican.dao.base.iface;

import java.io.*;
import java.util.*;

import de.augustakom.common.model.HistoryModel;

/**
 * DAO-Interface fuer alle DAO-Klassen, die einen Datensatz historisieren koennen/sollen. Die ID ist ein Integer.
 *
 *
 */
public interface HistoryUpdateDAO<T extends HistoryModel> {

    /**
     * Aktualisiert den Datensatz mit der ID <code>id</code> und setzt das GueltigBis-Datum auf den Wert von
     * <code>gueltigBis</code>.
     *
     * @param obj4History (optional) das Objekt, von dem eine Historisierung erzeugt werden soll.
     * @param id          ID des zu aktualisierenden Datensatzes
     * @param gueltigBis  Datum fuer das GueltigBis-Feld.
     */
    public T update4History(T obj4History, Serializable id, Date gueltigBis);

}


