/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2005 10:53:16
 */
package de.augustakom.hurrican.gui.cps.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.cps.CPSHistoryDialog;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Action, um die CPS-Historie zu einem best. Auftrag anzuzeigen.
 *
 *
 */
public class ShowCPSHistoryAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowCPSHistoryAction.class);

    private CCAuftragModel auftragModel = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        auftragModel = findModelByType(CCAuftragModel.class);
        if (auftragModel != null) {
            showCPSHistory();
        }
        else {
            LOGGER.error("AKModelOwner.getModel liefert nicht das erwartete Objekt zurueck!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Die CPS-Historie kann nicht angezeigt werden, da die Auftrags-ID " +
                            "nicht ermittelt werden konnte.", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /* Zeigt den Dialog mit der Verlaufs-History an. */
    private void showCPSHistory() {
        try {
            CPSHistoryDialog dlg = new CPSHistoryDialog(auftragModel);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage(), e);
            MessageHelper.showInfoDialog(getMainFrame(), e.getMessage(), null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }
}
