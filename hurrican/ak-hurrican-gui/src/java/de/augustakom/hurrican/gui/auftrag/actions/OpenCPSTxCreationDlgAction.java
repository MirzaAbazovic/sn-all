/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2009 15:52:55
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.cps.CPSTxCreationDialog;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Action oeffnet einen Dialog, ueber den eine CPS-Transaction fuer den aktuellen Auftrag ausgeloest werden kann.
 *
 *
 */
public class OpenCPSTxCreationDlgAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(OpenCPSTxCreationDlgAction.class);
    private static final long serialVersionUID = 7774034445869617210L;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (hasChanges()) {
                // evtl. Auftragsaenderungen speichern
                getAuftragDataFrame().saveModel();
            }

            CCAuftragModel auftragModel = findModelByType(CCAuftragModel.class);
            if (auftragModel == null) {
                throw new HurricanGUIException("Auftrag konnte nicht ermittelt werden!");
            }

            CPSTxCreationDialog dlg = new CPSTxCreationDialog(auftragModel.getAuftragId());
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

}


