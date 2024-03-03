/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2010 14:36:55
 */
package de.mnet.migration.common.dao;

import java.util.*;
import java.util.concurrent.atomic.*;

import de.mnet.migration.common.MigrationDao;


/**
 * Dieses Dao liest nichts aus einer Datenbank aus, sondern liefert nur eine einzelne Resultats-Zeile, die aus einem
 * {@code Void null} besteht. <b>Thread-Safe.</b>
 *
 *
 */
public class SingleRowMigrationDao extends MigrationDao<Void> {

    private static final List<Void> list = Collections.singletonList(null);
    private AtomicBoolean fetched = new AtomicBoolean(false);

    @Override
    protected Iterator<Void> getAllInternal() {
        if (fetched.compareAndSet(false, true)) {
            return list.iterator();
        }
        return null;
    }

    @Override
    protected Iterator<Void> getNextBlockInternal() {
        if (fetched.compareAndSet(false, true)) {
            return list.iterator();
        }
        return null;
    }
}
