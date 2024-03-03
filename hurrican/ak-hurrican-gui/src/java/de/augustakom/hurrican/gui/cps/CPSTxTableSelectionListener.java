/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps;

import javax.swing.event.*;

/**
 * The listener that's notified when a lists selection value changes.
 *
 *
 */
public class CPSTxTableSelectionListener implements ListSelectionListener {

    private CPSTxTable cpsTxTable = null;

    /**
     * @param cpsTxTable
     * @param cpsTxDetailPanel
     */
    public CPSTxTableSelectionListener(CPSTxTable cpsTxTable) {
        this.cpsTxTable = cpsTxTable;

    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
        cpsTxTable.notifyObservers();
    }
}
