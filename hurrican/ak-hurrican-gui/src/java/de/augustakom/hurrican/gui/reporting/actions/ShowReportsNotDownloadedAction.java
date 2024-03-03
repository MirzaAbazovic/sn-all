/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2007 16:00:42
 */
package de.augustakom.hurrican.gui.reporting.actions;


import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.hurrican.gui.reporting.ReportHistoryFrame;


/**
 * Action, um nicht heruntergeladene Reports anzuzeigen.
 *
 *
 */
public class ShowReportsNotDownloadedAction extends AKAbstractAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ReportHistoryFrame.openFrame(null);
    }
}

