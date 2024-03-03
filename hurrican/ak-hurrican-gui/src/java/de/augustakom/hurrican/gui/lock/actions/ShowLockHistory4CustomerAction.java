/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 12:02:40
 */
package de.augustakom.hurrican.gui.lock.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.lock.LockHistoryDialog;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Action, um die Sperr-History des aktuellen Kunden anzuzeigen.
 *
 *
 */
public class ShowLockHistory4CustomerAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowLockHistory4CustomerAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (getValue(MODEL_OWNER) instanceof AKModelOwner) {
            AKModelOwner modelOwner = (AKModelOwner) getValue(MODEL_OWNER);
            if (modelOwner.getModel() instanceof KundenModel) {
                LockHistoryDialog lockHistoryDialog = new LockHistoryDialog((KundenModel) modelOwner.getModel());
                DialogHelper.showDialog(
                        HurricanSystemRegistry.instance().getMainFrame(), lockHistoryDialog, true, true);
            }
            else {
                LOGGER.warn("Das Objekt von AKModelOwner.getModel() ist nicht vom Typ " + KundenModel.class.getName());
            }
        }
        else {
            LOGGER.warn("Objekt mit Key <MODEL_OWNER> ist nicht vom Typ " + AKModelOwner.class.getName());
        }
    }

}


