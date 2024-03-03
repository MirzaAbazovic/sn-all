/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.2007 13:19:14
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.CCAddress;


/**
 * DAO-Interface fuer die Verwaltung von Objekten des Typs <code>CCAddress</code>.
 *
 *
 */
public interface CCAddressDAO extends StoreDAO, FindDAO, ByExampleDAO {

    /**
     * Filtert ueber ein sog. Example-Objekt. Nutzt zur Suche LIKE und ignoriert Gross-/Kleinschreibung.
     *
     * @param example Example-Objekt, das die gesuchten Parameter enthaelt.
     * @param type    Typ der gesuchten Objekte.
     * @return Liste mit Objekten des Typs <code>type</code>
     */
    public <T> List<T> queryByExampleLike(CCAddress example, Class<T> type);

}


