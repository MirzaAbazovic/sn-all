/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2010 15:58:20
 */
package de.mnet.migration.common.dao;

import java.util.*;
import java.util.concurrent.atomic.*;

import de.mnet.migration.common.MigrationDao;
import de.mnet.migration.common.util.CollectionUtil;


/**
 *
 */
public abstract class ListBackedMigrationDao<T> extends MigrationDao<T> {

    protected volatile List<T> all;
    protected AtomicInteger start = new AtomicInteger(0);

    @Override
    protected void prepareInternal() {
        all = getSourceData();

        if (blockSize == 0) {
            blockSize = all.size();
        }
    }

    protected abstract List<T> getSourceData();

    @Override
    protected Iterator<T> getAllInternal() {
        if (start.compareAndSet(0, all.size())) {
            return all.iterator();
        }
        return emptyResult;
    }

    @Override
    protected Iterator<T> getNextBlockInternal() {
        Integer localStart = start.getAndAdd(blockSize);
        if (localStart < all.size()) {
            return CollectionUtil.subList(all, localStart, localStart + blockSize).iterator();
        }
        return emptyResult;
    }
}

