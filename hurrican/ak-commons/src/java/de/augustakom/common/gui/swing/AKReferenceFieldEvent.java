package de.augustakom.common.gui.swing;

/**
 * An observable object can have one or more observers. An observer may be any object that implements interface
 * AKReferenceFieldEvent. After an observable instance changes, an application calling the Observable's notifyObservers
 * method causes all of its observers to be notified of the change by a call to their update method.
 *
 *
 */

public interface AKReferenceFieldEvent {

    /**
     * Adds an observer to the set of observers for this object, provided that it is not the same as some observer
     * already in the set.
     *
     * @param o
     */
    public void addObserver(AKReferenceFieldObserver o);

    /**
     * Deletes an observer from the set of observers of this object.
     *
     * @param o
     */
    public void removeObserver(AKReferenceFieldObserver o);
}
