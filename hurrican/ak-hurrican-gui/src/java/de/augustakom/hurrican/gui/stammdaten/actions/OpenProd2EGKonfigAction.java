/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2006 09:39:22
 */
package de.augustakom.hurrican.gui.stammdaten.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.stammdaten.Produkt2EGKonfigDialog;
import de.augustakom.hurrican.model.cc.Produkt;


/**
 * Action, um den Dialog zur Endgeraete-Konfiguration zum Produkt zu oeffnen.
 *
 *
 */
public class OpenProd2EGKonfigAction extends AKAbstractAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object actionObj = getValue(OBJECT_4_ACTION);
        if (actionObj instanceof Produkt) {
            Produkt2EGKonfigDialog dlg = new Produkt2EGKonfigDialog((Produkt) actionObj);
            DialogHelper.showDialog(
                    HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
        }
    }

}


