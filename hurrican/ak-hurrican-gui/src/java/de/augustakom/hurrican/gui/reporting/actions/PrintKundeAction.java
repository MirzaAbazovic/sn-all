/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.03.2007 12:18:42
 */
package de.augustakom.hurrican.gui.reporting.actions;


import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.reporting.ReportDialog;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Action, um einen Report für einen Kunden zu drucken.
 *
 *
 */
public class PrintKundeAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(PrintKundeAction.class);

    private Kunde kunde = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            if (getValue(MODEL_OWNER) instanceof AKModelOwner) {
                AKModelOwner mo = (AKModelOwner) getValue(MODEL_OWNER);
                if (mo.getModel() instanceof KundeAdresseView) {
                    KundeAdresseView model = (KundeAdresseView) mo.getModel();

                    KundenService kundeService = getBillingService(KundenService.class);
                    kunde = kundeService.findKunde(model.getKundeNo());

                }

                // Aktion ausführen
                if (kunde != null) {
                    if (e.getActionCommand().equals("report")) {
                        reportAuftrag();
                    }
                }
                else {
                    LOGGER.error("AKModelOwner.getModel liefert nicht das erwartete Objekt zurueck!");
                    MessageHelper.showMessageDialog(getMainFrame(),
                            "Der Auftrag konnte nicht gedruckt werden, da die\n" +
                                    "erforderlichen Daten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    /*
     * Öffnet ein Auswahlmenü mit zur Verfügung stehenden Reports
     */
    private void reportAuftrag() {
        try {
            getMainFrame().setWaitCursor();

            ReportDialog dlg = new ReportDialog(Report.REPORT_TYPE_KUNDE, kunde.getKundeNo(), null);
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


