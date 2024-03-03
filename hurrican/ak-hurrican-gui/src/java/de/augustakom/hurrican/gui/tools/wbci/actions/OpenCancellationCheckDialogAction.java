/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2012 13:31:12
 */
package de.augustakom.hurrican.gui.tools.wbci.actions;

import java.awt.event.*;
import java.time.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.tools.wbci.CancellationCheckDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;

/**
 * Action, um den {@link de.augustakom.hurrican.gui.tools.wbci.CancellationCheckDialog} zu oeffnen.
 */
public class OpenCancellationCheckDialogAction extends AbstractServiceAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        AuftragDaten auftragDaten = findModelByType(AuftragDaten.class);
        if (auftragDaten == null || auftragDaten.getAuftragNoOrig() == null) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Öffnen des Kündigungs-Checks schlug fehl, da der Auftrag nicht ermittelt werden konnte " +
                            "oder es sich um keinen Auftrag mit Taifun-Zuordnung handelt!",
                    "Abbruch", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        showDialog(auftragDaten.getAuftragNoOrig());
    }

    private void showDialog(Long orderNoOrig) {
        try {
            CancellationCheckDialog dlg = new CancellationCheckDialog(orderNoOrig, LocalDateTime.now(), false);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}
