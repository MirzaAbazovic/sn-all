/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2007 11:44:42
 */
package de.augustakom.hurrican.gui.reporting.konfiguration.actions;


import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.reporting.konfiguration.TxtBausteinGruppeDialog;
import de.augustakom.hurrican.model.reporting.TxtBaustein;


/**
 * Action, um die Bausteingruppen für einen Text-Baustein anzuzeigen.
 *
 *
 */
public class ShowGruppen4TxtBausteinAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowGruppen4TxtBausteinAction.class);

    private AKJTable table = null;

    /**
     * Default-Const.
     */
    public ShowGruppen4TxtBausteinAction(AKJTable table) {
        super();
        this.table = table;
        setName("Zeige Bausteingruppen");
        setActionCommand("show.gruppen");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            if ((table != null) && (table.getSelectedRowCount() == 1)) {
                Integer row = table.getSelectedRow();
                AKTableSorter obj = (AKTableSorter) table.getModel();
                TxtBaustein baustein = (TxtBaustein) obj.getDataAtRow(row);
                showGruppen(baustein);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    /*
     * Öffnet ein Dialogfenster mit allen Gruppen denen ein Text-Baustein zugeordnet ist
     */
    private void showGruppen(TxtBaustein baustein) {
        try {
            getMainFrame().setWaitCursor();

            TxtBausteinGruppeDialog dlg = new TxtBausteinGruppeDialog(baustein);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            getMainFrame().setDefaultCursor();
        }
    }

}


