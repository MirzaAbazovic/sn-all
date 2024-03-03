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
public class CPSLogTableSelectionListener implements ListSelectionListener {

    private CPSTable logTable = null;
    private CPSTxTextArea logDetailTextArea = null;

    /**
     * DOCUMENT ME!!!
     *
     * @param logTable
     * @param logDetailTextAres
     */
    public CPSLogTableSelectionListener(CPSTable logTable, CPSTxTextArea logDetailTextArea) {
        this.logTable = logTable;
        this.logDetailTextArea = logDetailTextArea;
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {

        if (logTable.getSelectedRow() < 0) {
            logDetailTextArea.setText("");
        }
    }
}
