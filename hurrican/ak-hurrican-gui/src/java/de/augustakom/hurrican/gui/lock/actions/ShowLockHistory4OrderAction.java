/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 12:02:40
 */
package de.augustakom.hurrican.gui.lock.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.lock.LockHistoryDialog;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Action, um die Sperr-History des aktuellen Auftrags anzuzeigen.
 *
 *
 */
public class ShowLockHistory4OrderAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowLockHistory4OrderAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        CCAuftragModel auftragModel = findModelByType(CCAuftragModel.class);
        if (auftragModel != null) {
            LockHistoryDialog lockHistoryDialog = new LockHistoryDialog(auftragModel);
            DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), lockHistoryDialog, true, true);
        }
        else {
            LOGGER.warn(String.format("Das Model %s konnte nicht ermittelt werden!", CCAuftragModel.class.getName()));
        }
    }

}


