/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2009 17:49:37
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.hurrican.model.cc.BrasPool;


/**
 * DAO Definition fuer die Verwaltung von Bras Pool Objekten.
 *
 *
 */
public interface BrasPoolDAO extends ByExampleDAO, FindDAO, FindAllDAO {

    /**
     * Find a Bras Pool whose name starts with the given prefix
     *
     * @return A list of Bras Pools, possibly empty, never {@code null}
     */
    List<BrasPool> findByNamePrefix(String string);

}
