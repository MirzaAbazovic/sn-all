/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2010 13:32:26
 */
package de.mnet.migration.common.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;


/**
 * Diese Map ist Thread-Safe. Sie kann transaktionale Writes, d.h. bei einem Rollback werden neu in die Map geschriebene
 * Werte und geaenderte Werte nicht uebernommen.<br/> <br/> Writes sind gelockt, d.h. falls ein anderer Thread den Wert
 * lesen oder schreiben moechte, so blockiert dieser so lange, bis der gerade schreibende Thread fertig ist
 * (commit/rollback).<br/> <br/> Eine Transaktion wird automatisch begonnen, sobald ein Wert in die Map geschrieben
 * wird.</br> <br/> Achtung: Fragiler Thread-Safe Code, sehr vorsichtig sein bei Aenderungen!
 *
 *
 */
public class SimpleTransactionalMap<K, V> {

    /**
     * In dieser Map muessen alle lokalen Aenderungen gespeichert werden. Fuer alle Werte in dieser Map muss ein Lock
     * gehalten werden!
     */
    private final ThreadLocal<Map<K, V>> threadModifications;

    /**
     * Map fuer Locks. Wenn ein Lock gelockt wird, muss auch ein Eintrag in den ThreadModifications vorhanden sein!
     */
    private final ConcurrentHashMap<K, ReentrantLock> locks;

    /**
     * Map fuer finale Werte
     */
    private final ConcurrentHashMap<K, V> map;

    /**
     * To create a copy of a value, only needed if getOrCreateForModification is called
     */
    private final Duplicate<V> duplicate;


    public SimpleTransactionalMap() {
        this(null, null);
    }

    public SimpleTransactionalMap(Duplicate<V> duplicate) {
        this(null, duplicate);
    }

    public SimpleTransactionalMap(Map<K, V> initialValues) {
        this(initialValues, null);
    }

    public SimpleTransactionalMap(Map<K, V> initialValues, Duplicate<V> duplicate) {
        this.duplicate = duplicate;
        if (initialValues != null) {
            map = new ConcurrentHashMap<K, V>(initialValues);
        }
        else {
            map = new ConcurrentHashMap<K, V>();
        }
        locks = new ConcurrentHashMap<K, ReentrantLock>();
        threadModifications = new ThreadLocal<Map<K, V>>() {
            @Override
            protected Map<K, V> initialValue() {
                return new HashMap<K, V>();
            }
        };
    }


    /**
     * @return Die Anzahl der derzeit in der Map committeten Datensaetze, plus die Anzahl der derzeit nicht committeten
     * neuen Datensaetze des aktuellen Threads.
     */
    public int size() {
        int size = 0;
        for (K threadModificationKey : threadModifications.get().keySet()) {
            if (!map.containsKey(threadModificationKey)) {
                size++;
            }
        }
        return map.size() + size;
    }


    /**
     * Liefert den angefragten Wert zurueck. Falls der Wert nicht existiert, wird der Callback ausgefuehrt, um ihn zu
     * erstellen und mit dem gegebenen Key in die Map zu schreiben. Diese Operation ist atomar.<br/> <br/> <b>Der
     * zurueckgelieferte Wert darf nicht mehr modifiziert werden!</b>
     *
     * @return Der alte oder neu erstellte Wert
     */
    public V getOrCreate(K key, Callable<V> create) {
        V value = threadModifications.get().get(key);
        if (value == null) { // schon lokal verfuegbar?
            value = map.get(key);
            if (value == null) {
                createOrGetLock(key);
                value = map.get(key);
                if (value == null) {
                    try {
                        value = create.call();
                        threadModifications.get().put(key, value);
                    }
                    catch (Exception e) {
                        releaseLock(key);
                        throw new RuntimeException("getOrCreateForModification() - Exception while executing callback", e);
                    }
                }
                else {
                    releaseLock(key);
                }
            }
        }
        return value;
    }


    /**
     * Liefert den angefragten Wert zurueck. Falls der Wert nicht existiert, wird der Callback ausgefuehrt, um ihn zu
     * erstellen und mit dem gegebenen Key in die Map zu schreiben.
     *
     * @return Der alte oder neu erstellte Wert
     */
    public V getOrCreateForModification(K key, Callable<V> create) {
        if (duplicate == null) {
            throw new RuntimeException("getOrCreateForModification - can only be used if duplicate is set");
        }
        createOrGetLock(key);
        V value = threadModifications.get().get(key);
        if (value == null) {
            value = map.get(key);
            if (value == null) {
                try {
                    value = create.call();
                }
                catch (Exception e) {
                    threadModifications.get().remove(key);
                    releaseLock(key);
                    throw new RuntimeException("getOrCreateForModification() - Exception while executing callback", e);
                }
            }
            else {
                value = duplicate.duplicate(value);
            }
            threadModifications.get().put(key, value);
        }
        return value;
    }


    /**
     * Potentially blocking call
     */
    private ReentrantLock createOrGetLock(K key) {
        ReentrantLock newLock = new ReentrantLock();
        newLock.lock();
        ReentrantLock lock = locks.putIfAbsent(key, newLock);
        if (lock == null) {
            lock = newLock;
        }
        else if (!lock.isHeldByCurrentThread()) {
            lock.lock(); // potentially blocks
        }
        return lock;
    }


    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            justification = "exception soll mitgeteilt werden",
            value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private void releaseLock(K key) {
        ReentrantLock lock = locks.get(key);
        lock.unlock();
    }


    /**
     * Speichert die modifizierten Werte fest in der Map und gibt alle Locks frei.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            justification = "exception soll mitgeteilt werden",
            value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void commit() {
        for (Map.Entry<K, V> modification : threadModifications.get().entrySet()) {
            map.put(modification.getKey(), modification.getValue());
            ReentrantLock lock = locks.get(modification.getKey());
            lock.unlock();
        }
        threadModifications.get().clear();
    }


    /**
     * Verwirft die Modifikationen und gibt alle Locks frei.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            justification = "exception soll mitgeteilt werden",
            value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void rollback() {
        for (Map.Entry<K, V> modification : threadModifications.get().entrySet()) {
            ReentrantLock lock = locks.get(modification.getKey());
            lock.unlock();
        }
        threadModifications.get().clear();
    }


    public static interface Duplicate<D> {
        D duplicate(D value);
    }
}
