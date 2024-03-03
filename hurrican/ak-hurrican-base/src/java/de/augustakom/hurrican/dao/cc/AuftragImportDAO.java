/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2005 11:06:52
 */
package de.augustakom.hurrican.dao.cc;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.model.cc.AuftragImport;
import de.augustakom.hurrican.model.cc.query.AuftragImportQuery;


/**
 * DAO-Interface fuer die Auftrag-Imports.
 *
 *
 */
public interface AuftragImportDAO extends StoreDAO, FindDAO, FindAllDAO, ByExampleDAO {

    /**
     * Sucht nach allen AuftragImports, die den Query-Parametern entsprechen.
     *
     * @param query
     * @return
     */
    public List<AuftragImport> findByQuery(AuftragImportQuery query);

}


