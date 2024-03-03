/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 10:58:19
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Action, um die Online-Daten eines Auftrags zu drucken.
 *
 *
 */
public class PrintOnlineAction extends PrintAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(PrintOnlineAction.class);
    private static final long serialVersionUID = -3103810106191015643L;

    private AuftragDaten auftragDaten = null;
    private Kunde kunde = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            auftragDaten = findModelByType(AuftragDaten.class);
            kunde = findModelByType(Kunde.class);
            Object value = getValue(MODEL_OWNER);
            if ((auftragDaten == null)
                    && (kunde == null)
                    && value instanceof AKModelOwner
                    && ((AKModelOwner) value).getModel() instanceof CCAuftragModel) {
                search4Auftrag(((CCAuftragModel) ((AKModelOwner) value).getModel()).getAuftragId());
            }

            // Versuche fehlende Daten zu ermitteln
            if ((kunde == null) && (auftragDaten != null)) {
                kunde = search4Kunde(auftragDaten.getAuftragId());
            }

            // Öffne Report-Dialog
            if ((auftragDaten != null) && (kunde != null)) {
                reportAuftrag(auftragDaten.getAuftragId(), auftragDaten.getAuftragNoOrig(), kunde.getKundeNo());
            }
            else {
                LOGGER.error("Die AuftagDaten/Kunde konnten nicht ermittelt werden!");
                MessageHelper.showMessageDialog(getMainFrame(), "Das Anschreiben konnte nicht gedruckt "
                                + "werden, da der Auftrag nicht ermittelt werden konnte!", "Abbruch",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }

    /* Ermittelt das AuftragDaten und Kunden Objekt */
    private void search4Auftrag(Long auftragId) throws HurricanGUIException {
        try {
            CCAuftragService service = getCCService(CCAuftragService.class);
            auftragDaten = service.findAuftragDatenByAuftragId(auftragId);
            Auftrag auftrag = service.findAuftragById(auftragId);

            if (auftrag != null) {
                KundenService kdService = getBillingService(KundenService.class);
                kunde = kdService.findKunde(auftrag.getKundeNo());
            }
        }
        catch (Exception e) {
            throw new HurricanGUIException(e.getMessage(), e);
        }
    }

}
