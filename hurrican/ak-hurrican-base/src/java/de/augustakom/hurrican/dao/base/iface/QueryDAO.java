/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 13:47:29
 */
package de.augustakom.hurrican.dao.base.iface;

import java.util.*;


/**
 * DAO-Interface, um auf dem Billing-System beliebige Queries auszufuehren.
 *
 *
 */
public interface QueryDAO {

    /**
     * Fuehrt das SQL-Statement <code>sql</code> mit den Parametern <code>params</code> durch. <br> Bei dem
     * SQL-Statement muss es sich um ein Select-Statement handeln!
     *
     * @param sql    auszufuehrendes SQL-Statement
     * @param parameters (optional) Parameter fuer das SQL-Statement
     * @return Liste mit Objekten des Typs <code>Map</code>. Jede Map stellt dabei einen Datensatz des Ergebnisses dar.
     */
    public List<Object[]> query(String sql, Object[] parameters);

}


