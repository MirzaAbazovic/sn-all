/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2004
 */
package de.augustakom.common.model;

import java.io.*;
import java.util.*;


/**
 * Abstrakte Klasse fuer Observable-Modelle. <br>
 *
 *
 * @see java.util.Observable
 */
public class AbstractObservable extends Observable implements Serializable {

    /**
     * Benachrichtigt die Observer ueber eine Aenderung des Objekts. <br> Der Parameter <code>force</code> hat folgende
     * Auswirkungen: <ul> <li><code>force = true</code>: Observer werden auf jeden Fall benachrichtigt - auch wenn sich
     * das Modell nicht geaendert hat <li><code>force = false</code>: Observer werden nur dann benachrichtigt, wenn sich
     * das Modell geaendert hat. </ul>
     *
     * @param force Bestimmt, ob die Observer immer benachrichtigt werden sollen.
     * @param arg   any Object
     * @see java.util.Observable#notifyObservers(Object)
     */
    public void notifyObservers(boolean force, Object arg) {
        if (force) {
            setChanged();
        }

        super.notifyObservers(arg);
    }

    /**
     * @see de.augustakom.common.model.AbstractObservable#notifyObservers(boolean, Object)
     */
    public void notifyObservers(boolean force) {
        notifyObservers(force, null);
    }
}
