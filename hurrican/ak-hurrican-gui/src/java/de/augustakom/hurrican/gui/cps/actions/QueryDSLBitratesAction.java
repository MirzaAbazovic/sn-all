/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2012 13:31:12
 */
package de.augustakom.hurrican.gui.cps.actions;

import java.awt.event.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.cps.QueryDSLBitratesDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;

/**
 * Action, um die DSL Synchraten am CPS anzufragen und im Dialog anzuzeigen
 */
public class QueryDSLBitratesAction extends AbstractServiceAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        AuftragDaten auftragDaten = findModelByType(AuftragDaten.class);
        Produkt produkt = findModelByType(Produkt.class);
        if (auftragDaten == null) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Öffnen des DSL Bitraten Dialogs schlug fehl, da der Auftrag nicht ermittelt werden konnte!",
                    "Abbruch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (produkt == null) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Öffnen des DSL Bitraten Dialogs schlug fehl, da das Produkt nicht ermittelt werden konnte!",
                    "Abbruch", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!BooleanTools.nullToFalse(produkt.getCpsDSLProduct())) {
            MessageHelper.showMessageDialog(getMainFrame(), "Der DSL Bitraten Dialog ist nur fuer DSL-Produkte vorgesehen!",
                    "Abbruch", JOptionPane.WARNING_MESSAGE);
        }
        else {
            showDialog(auftragDaten.getAuftragId());
        }
    }

    private void showDialog(Long auftragId) {
        try {
            QueryDSLBitratesDialog dlg = new QueryDSLBitratesDialog(auftragId);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

    }

}


