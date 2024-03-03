/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2005 10:58:59
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.auftrag.CopyConnectDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;


/**
 * Action, um einen AK-Connect Auftrag zu kopieren.
 *
 *
 */
public class CopyConnectAuftragAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(CopyConnectAuftragAction.class);

    private AuftragDaten auftrag = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (hasChanges()) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Speichern Sie bitte erst die Änderungen ab, bevor\nSie den Auftrag kopieren.",
                    "Änderungen speichern", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        auftrag = findModelByType(AuftragDaten.class);
        if ((auftrag == null) || (auftrag.getAuftragId() == null)) {
            LOGGER.error("Die AuftragDaten konnten nicht ermittelt werden!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Der Auftrag kann nicht kopiert werden, da die\n" +
                            "Auftragsdaten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
        // Connect-Auftraege mit Taifun-Auftragsnummer duerfen nicht kopiert werden
        else if (auftrag.getAuftragNoOrig() != null) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Der aktuelle Auftrag enthält eine Taifun-Auftragsnummer\n" +
                            "und kann deshalb nicht kopiert werden!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
        else {
            copyAuftrag();
        }
    }

    /*
     * Oeffnet einen Dialog, ueber den ein AK-Connect Auftrag kopiert werden kann.
     */
    private void copyAuftrag() {
        CopyConnectDialog dlg = new CopyConnectDialog(auftrag.getAuftragId());
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
    }
}


