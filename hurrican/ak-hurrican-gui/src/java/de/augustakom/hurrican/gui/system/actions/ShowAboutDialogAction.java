/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2004 11:14:23
 */
package de.augustakom.hurrican.gui.system.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.system.HurricanAboutDialog;


/**
 * Action, um den About-Dialog fuer die Hurrican-GUI anzuzeigen.
 *
 *
 */
public class ShowAboutDialogAction extends AKAbstractAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        HurricanAboutDialog dlg = new HurricanAboutDialog();
        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
    }


}


