/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2005 10:53:16
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.verlauf.VerlaufHistoryDialog;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Action, um die Verlaufs-Historie zu einem best. Auftrag anzuzeigen.
 *
 *
 */
public class ShowVerlaufsHistoryAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowVerlaufsHistoryAction.class);

    private CCAuftragModel auftragModel = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        auftragModel = findModelByType(CCAuftragModel.class);
        if (auftragModel != null) {
            showVerlaufsHistory();
        }
        else {
            LOGGER.error("AKModelOwner.getModel liefert nicht das erwartete Objekt zurueck!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Die Verlaufs-Historie kann nicht angezeigt werden, da die Auftrags-ID " +
                            "nicht ermittelt werden konnte.", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /* Zeigt den Dialog mit der Verlaufs-History an. */
    private void showVerlaufsHistory() {
        try {
            VerlaufHistoryDialog dlg = new VerlaufHistoryDialog(auftragModel.getAuftragId());
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


