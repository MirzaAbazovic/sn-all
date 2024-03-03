/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2010 14:51:19
 */
package de.mnet.migration.common.dao;

import java.util.*;
import org.springframework.beans.factory.annotation.Required;


/**
 * Dao, das Daten via einem DataLoader ausliest.<br/> Das Aufteilen in Bloecke geschieht ueber {@link List#subList(int,
 * int)}, so dass dafuer kein signifikanter Overhead produziert wird.<br/> <b>Thread-Safe.</b>
 *
 *
 */
public class DataLoaderMigrationDao<T> extends ListBackedMigrationDao<T> {

    private DataLoader<T> dataLoader;

    /**
     * @see de.mnet.migration.common.dao.ListBackedMigrationDao#getSourceData()
     */
    @Override
    protected List<T> getSourceData() {
        return dataLoader.getSourceData();
    }

    @Required
    public void setDataLoader(DataLoader<T> dataLoader) {
        this.dataLoader = dataLoader;
    }

}
