/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.03.2007 09:27:42
 */
package de.augustakom.hurrican.gui.tools.stats.actions;

import java.awt.event.*;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.tools.stats.SelectArchPrintSetDialog;
import de.augustakom.hurrican.model.billing.ArchPrintSet;
import de.augustakom.hurrican.service.billing.RechnungsService;


/**
 * Action, um die Druckstatistik fuer die LMF zu erzeugen.
 *
 *
 */
public class PrintStatisticLMFAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(PrintStatisticLMFAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        try {
            SelectArchPrintSetDialog dlg = new SelectArchPrintSetDialog();
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result instanceof ArchPrintSet) {
                ArchPrintSet aps = (ArchPrintSet) result;

                RechnungsService rs = getBillingService(RechnungsService.class);
                JasperPrint jp = rs.reportPrintStatistic(aps.getPrintSetNo(), aps.getName());

                JasperPrintManager.printReport(jp, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}


