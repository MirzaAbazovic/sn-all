/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 13:16:52
 */
package de.augustakom.hurrican.gui.tools.physik.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.tools.physik.AnschlussuebernahmeManuellDialog;


/**
 * Action, um einen Dialog fuer eine manuelle Anschlussuebernahme zu oeffnen.
 *
 *
 */
public class AnschlussuebernahmeAction extends AbstractServiceAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        AnschlussuebernahmeManuellDialog dlg = new AnschlussuebernahmeManuellDialog();
        DialogHelper.showDialog(getMainFrame(), dlg, false, true);
    }

}


