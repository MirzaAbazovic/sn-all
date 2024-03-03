/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2009 11:02:47
 */
package de.augustakom.common.gui.swing;

/**
 * Observer-Interface fuer die Benachrichtigung ueber Aenderungen am AKReferenceField.
 *
 *
 */
public interface AKReferenceFieldObserver {

    /**
     * A class can implement the AKReferenceFieldObserver interface when it wants to be informed of changes in
     * observable objects.
     *
     * @param akReferenceFieldEvent
     * @throws Exception
     */
    public void update(AKReferenceFieldEvent akReferenceFieldEvent) throws Exception;
}
