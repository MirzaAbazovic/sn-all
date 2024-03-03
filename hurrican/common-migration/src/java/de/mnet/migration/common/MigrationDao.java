/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2009 11:58:53
 */
package de.mnet.migration.common;

import java.util.*;

import de.mnet.migration.common.result.TransformationResult;


/**
 * Allgemeines DAO für Migrationen.
 * <p/>
 * Parameter: <ul> <li>dataSource</li> <li>blockSize (default: all)</li> </ul>
 *
 *
 */
public abstract class MigrationDao<T> {

    protected static final int defaultBlockSize = 1000;

    protected final Iterator<T> emptyResult = Collections.<T>emptyList().iterator();
    protected volatile int blockSize = 0;
    protected final List<TransformationResult> preparationResult = new ArrayList<TransformationResult>();
    private volatile boolean prepared = false;


    private void checkPrepared() {
        if (!prepared) {
            throw new RuntimeException("Uninitialized Dao");
        }
    }

    /**
     * Laedt alle Daten. <b>Muss</b> vor allen anderen Methoden aufgerufen werden.
     */
    public final void prepare() {
        prepared = true;
        prepareInternal();
        if (blockSize == 0) {
            blockSize = defaultBlockSize;
        }
    }

    /**
     * Liefert alle Daten zurueck.
     *
     * @return ein Iterator, der ueber die Daten iteriert, oder {@code null}
     */
    public final Iterator<T> getAll() {
        checkPrepared();
        return getAllInternal();
    }

    /**
     * Laedt den naechsten, noch nicht zurueck gelieferten Block an Daten.
     *
     * @return ein Iterator, der ueber die Daten iteriert, oder {@code null}
     */
    public final Iterator<T> getNextBlock() {
        checkPrepared();
        return getNextBlockInternal();
    }

    /**
     * Liefert eine Liste von TransformationResults zurueck, falls bei der Datenaufbereitung fuer den Transformator
     * Fehler passiert sind.
     */
    public final List<TransformationResult> getPreparationResult() {
        return preparationResult;
    }

    protected void prepareInternal() {
    }

    protected abstract Iterator<T> getAllInternal();

    protected abstract Iterator<T> getNextBlockInternal();


    /**
     * Injected
     */
    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }
}
