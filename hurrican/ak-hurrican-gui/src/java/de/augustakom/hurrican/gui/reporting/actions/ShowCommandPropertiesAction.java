/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2007 10:44:42
 */
package de.augustakom.hurrican.gui.reporting.actions;


import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.reporting.konfiguration.CommandPropertyDialog;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;


/**
 * Action, um die Property-Datei einer Command-Klasse anzuzeigen.
 *
 *
 */
public class ShowCommandPropertiesAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowCommandPropertiesAction.class);

    private AKJTable table = null;

    /**
     * Default-Const.
     */
    public ShowCommandPropertiesAction(AKJTable table) {
        super();
        this.table = table;
        setName("Zeige Properties");
        setActionCommand("show.properties");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            if ((table != null) && (table.getSelectedRowCount() == 1)) {
                Integer row = table.getSelectedRow();
                AKTableSorter obj = (AKTableSorter) table.getModel();
                ServiceCommand cmd = (ServiceCommand) obj.getDataAtRow(row);
                showProperties(cmd);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    /*
     * Öffnet ein Dialogfenster mit allen Properties einer Command-Klasse
     */
    private void showProperties(ServiceCommand cmd) {
        try {
            getMainFrame().setWaitCursor();

            CommandPropertyDialog dlg = new CommandPropertyDialog(cmd);
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


