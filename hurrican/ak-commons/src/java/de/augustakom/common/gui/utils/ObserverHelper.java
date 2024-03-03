/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2005 14:39:25
 */
package de.augustakom.common.gui.utils;

import java.util.*;


/**
 * Hilfsklasse fuer das Arbeiten mit dem Observer-Pattern.
 *
 *
 */
public class ObserverHelper {

    /**
     * Durchlaeuft die Liste <code>observables</code> und fuegt jedem Objekt den Observer <code>observer</code> hinzu.
     *
     * @param observer    Observer, der den Objekten aus <code>observables</code> zugeordnet werden soll.
     * @param observables Liste mit den Observables.
     */
    public static <T extends Observable> void addObserver2Objects(Observer observer, Collection<T> observables) {
        if (observer != null && observables != null) {
            for (T o : observables) {
                o.addObserver(observer);
            }
        }
    }

    /**
     * @param observer
     * @param observables
     * @see addObserver2Objects(java.util.Observer, java.util.Collection<java.util.Observer>)
     */
    public static void addObserver2Objects(Observer observer, Observable[] observables) {
        if (observer != null && observables != null) {
            for (int i = 0; i < observables.length; i++) {
                observables[i].addObserver(observer);
            }
        }
    }
}


