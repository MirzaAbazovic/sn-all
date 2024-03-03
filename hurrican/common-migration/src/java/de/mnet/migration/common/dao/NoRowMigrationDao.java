/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2010 15:23:56
 */
package de.mnet.migration.common.dao;

import java.util.*;


/**
 *
 */
public class NoRowMigrationDao extends ListBackedMigrationDao<Void> {

    /**
     * @see de.mnet.migration.common.dao.ListBackedMigrationDao#getSourceData()
     */
    @Override
    protected List<Void> getSourceData() {
        return Collections.emptyList();
    }

}
