/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

/**
 *
 */
public interface CPSTxObservable {

    /**
     * Adds an observer to the set of observers for this object
     *
     * @param observer
     */
    public void addObserver(CPSTxObserver observer);

    /**
     * Removes an observer from the set of observers of this object. Passing null to this method will have no effect.
     *
     * @param observer
     */
    public void removeObserver(CPSTxObserver observer);
}
