/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 15:50:36
 */
package de.augustakom.hurrican.dao.base.iface;

import java.util.*;

/**
 * DAO-Interface fuer alle DAO-Klassen, die einen Datensatz historisieren koennen/sollen. Die ID ist ein Long.
 */
public interface HistoryUpdateLongDAO {

    /**
     * Aktualisiert den Datensatz mit der ID <code>id</code> und setzt das GueltigBis-Datum auf den Wert von
     * <code>gueltigBis</code>.
     *
     * @param obj4History (optional) das Objekt, von dem eine Historisierung erzeugt werden soll.
     * @param id          ID des zu aktualisierenden Datensatzes
     * @param gueltigBis  Datum fuer das GueltigBis-Feld.
     * @param type        Typ der zu aktualisierenden Klasse.
     */
    public void update4History(Object obj4History, Long id, Date gueltigBis, Class<?> type);
}


