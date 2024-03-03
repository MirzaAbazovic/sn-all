/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2009 12:40:44
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Action, um die Dipo ueber die Terminverschiebung eines Auftrags zu informieren.
 *
 *
 */
public class ChangeBARealDateAMAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(ChangeBARealDateAMAction.class);

    private AuftragDaten auftragDaten = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (hasChanges()) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Speichern Sie bitte erst die Änderungen ab, bevor\nSie den Realisierungstermin ändern.",
                    "Änderungen speichern", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        auftragDaten = findModelByType(AuftragDaten.class);
        if (auftragDaten != null) {
            changeRealDate();
        }
        else {
            LOGGER.error("Die AuftragDaten konnten nicht ermittelt werden!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Der Realisierungstermin kann nicht geändert werden, da die\n" +
                            "Auftragsdaten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /* Aendert das Realisierungsdatum fuer den Auftrag (bzw. dessen Bauauftrag) ab. */
    private void changeRealDate() {
        try {
            AKDateSelectionDialog dlg = new AKDateSelectionDialog("Realisierungstermin ändern",
                    "Bitte geben Sie einen neuen Realisierungstermin ein:", "Realisierungstermin:");
            Object option = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (option instanceof Date) {
                BAService bas = getCCService(BAService.class);
                bas.infoDispoChangeRealDate(auftragDaten.getAuftragId(), (Date) option, HurricanSystemRegistry.instance().getSessionId());

                MessageHelper.showMessageDialog(getMainFrame(),
                        "Dispo wurde über die Terminverschiebung informiert.", "Abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}


