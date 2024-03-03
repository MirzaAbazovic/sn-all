/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2004 13:28:12
 */
package de.augustakom.hurrican.gui.stammdaten.actions;

import java.awt.event.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.stammdaten.CreateMatrix4ProduktDialog;
import de.augustakom.hurrican.model.cc.Produkt;


/**
 * Action, um eine Rangierungsmatrix fuer ein best. Produkt zu erstellen. Die Action oeffnet einen Dialog zur Auswahl
 * der UEVTs, fuer die die Rangierungsmatrix erstellt werden soll.
 *
 *
 */
public class CreateMatrix4ProduktAction extends AKAbstractAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object actionObj = getValue(OBJECT_4_ACTION);
        if (actionObj instanceof Produkt) {
            CreateMatrix4ProduktDialog dlg = new CreateMatrix4ProduktDialog((Produkt) actionObj);
            DialogHelper.showDialog(
                    HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
        }
    }

}


