/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2005 16:55:59
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Action, um einen Auftrag zu kuendigen - Auftrag wird in Status 'Kuendigung Erfassung' gesetzt. <br> Einschraenkung
 * fuer die Action: die Kuendigung geht nur fuer Auftraege/Produkte die auch direkt in Hurrican erfasst werden.
 *
 *
 */
public class AuftragKuendigungAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(AuftragKuendigungAction.class);

    private AuftragDaten auftragDaten = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (hasChanges()) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Speichern Sie bitte erst die Änderungen ab, bevor\nSie die Kuendigung durchführen.",
                    "Änderungen speichern", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        auftragDaten = findModelByType(AuftragDaten.class);
        if (auftragDaten != null) {
            auftragKuendigen();
        }
        else {
            LOGGER.error("Die AuftragDaten konnten nicht ermittelt werden!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Der Auftrag kann nicht gekuendigt werden, da die\n" +
                            "Auftragsdaten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /* Setzt den Auftrag auf Status 'Kuendigung erfassen'. */
    private void auftragKuendigen() {
        try {
            if (NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.IN_BETRIEB)) {
                throw new HurricanGUIException("Nur aktive Auftraege koennen gekuendigt werden!");
            }
            else if (auftragDaten.isInKuendigung()) {
                throw new HurricanGUIException("Auftrag befindet sich bereits im Status Kuendigung!");
            }

            ProduktService produktService = getCCService(ProduktService.class);
            Produkt produkt = produktService.findProdukt(auftragDaten.getProdId());
            if (produkt != null) {
                if (!BooleanTools.nullToFalse(produkt.getAuftragserstellung())) {
                    throw new HurricanGUIException("Auftraege von diesem Produkt koennen nur ueber " +
                            "das Billing-System gekuendigt werden!");
                }

                AKDateSelectionDialog dlg = new AKDateSelectionDialog(
                        "Kuendigungsdatum", "Datum fuer die Kuendigung angeben:", "Datum:");
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                Date kuendDate = ((result instanceof Date) ? (Date) result : null);

                CCAuftragStatusService auftragStatusService = getCCService(CCAuftragStatusService.class);
                auftragStatusService.kuendigeAuftrag(auftragDaten.getAuftragId(), kuendDate,
                        HurricanSystemRegistry.instance().getSessionId());

                refreshFrame();
            }
            else {
                throw new HurricanGUIException("Auftrag kann nicht gekuendigt werden, da " +
                        "das Produkt nicht ermittelt werden konnte.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}


