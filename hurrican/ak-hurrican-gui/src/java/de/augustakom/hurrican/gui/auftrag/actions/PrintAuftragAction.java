/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2005 17:26:42
 */
package de.augustakom.hurrican.gui.auftrag.actions;


import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.reporting.ReportDialog;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Action, um einen Auftrag zu drucken.
 *
 *
 */
public class PrintAuftragAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(PrintAuftragAction.class);
    public static final String AUFTRAGDATEN = "auftragdaten";

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        AuftragDaten auftragDaten = null;
        try {
            if (hasChanges()) {
                getAuftragDataFrame().saveModel();
            }

            // Ermittle übergebene Modelle/Daten
            auftragDaten = findModelByType(AuftragDaten.class);
            Kunde kunde = findModelByType(Kunde.class);
            if ((auftragDaten == null) && (getValue(AUFTRAGDATEN) instanceof AuftragDaten)) {
                auftragDaten = (AuftragDaten) getValue(AUFTRAGDATEN);
            }

            // Versuche fehlende Daten zu ermitteln
            if ((kunde == null) && (auftragDaten != null)) {
                kunde = search4Kunde(auftragDaten.getAuftragId());
            }

            // Aktion ausführen
            if ((auftragDaten != null) && (kunde != null)) {
                reportAuftrag(auftragDaten.getAuftragId(), auftragDaten.getAuftragNoOrig(), kunde.getKundeNo());
            }
            else {
                LOGGER.error("Die erforderlichen Daten konnten nicht ermittelt werden!");
                MessageHelper.showMessageDialog(getMainFrame(),
                        "Der Auftrag konnte nicht gedruckt werden, da die\n" +
                                "erforderlichen Daten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
                );
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
        finally {
            auftragDaten = null;
        }
    }

    /*
     * Öffnet ein Auswahlmenü mit zur Verfügung stehenden Reports
     */
    protected void reportAuftrag(Long auftragId, Long auftragNo, Long kundeNoOrig) throws HurricanGUIException {
        try {
            // Öffne Dialog für Report-Auswahl
            ReportDialog dlg = new ReportDialog(Report.REPORT_TYPE_AUFTRAG, kundeNoOrig, auftragId);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

    /*
     * Versucht, den Kunden zu laden.
     */
    protected Kunde search4Kunde(Long auftragId) throws HurricanGUIException {
        try {
            if (auftragId != null) {
                CCAuftragService as = getCCService(CCAuftragService.class);
                Auftrag auftrag = as.findAuftragById(auftragId);

                if (auftrag != null) {
                    KundenService ks = getBillingService(KundenService.class);
                    return ks.findKunde(auftrag.getKundeNo());
                }
            }
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
        return null;
    }

}

