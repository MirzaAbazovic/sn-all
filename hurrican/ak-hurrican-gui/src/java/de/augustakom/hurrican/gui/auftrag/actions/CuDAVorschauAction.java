/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2005 19:09:56
 */
package de.augustakom.hurrican.gui.auftrag.actions;


import java.awt.event.*;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.auftrag.CuDAVorschauDialog;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;


/**
 * Oeffnet eine Dialog zur Anzeige der CuDA-Vorschau.
 *
 *
 */
public class CuDAVorschauAction extends AbstractServiceAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        CuDAVorschauDialog dlg = new CuDAVorschauDialog();
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
    }

}


