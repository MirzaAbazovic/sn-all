/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2004 15:05:28
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Action, um einen Auftrag auf 'Absage' zu setzen.
 *
 *
 */
public class AuftragAbsageAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(AuftragAbsageAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        AuftragDaten auftragDaten = findModelByType(AuftragDaten.class);
        if (auftragDaten == null) {
            LOGGER.error("AKModelOwner.getModel liefert nicht das erwartete Objekt zurueck!");
        }
        else {
            auftragAbsage(auftragDaten);
            refreshFrame();
        }
    }

    /* Setzt den aktuellen Auftrag auf 'Absage' */
    private void auftragAbsage(AuftragDaten auftragDaten) {
        try {
            CCAuftragStatusService auftragStatusService = CCServiceFinder.instance().getCCService(
                    CCAuftragStatusService.class);

            auftragStatusService.checkAuftragAbsagen(auftragDaten.getAuftragId());

            int question = MessageHelper.showConfirmDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    "Soll der Auftrag wirklich auf Absage gesetzt werden?", "Auftrag auf Absage?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (question == JOptionPane.YES_OPTION) {
                auftragStatusService.performAuftragAbsagen(auftragDaten.getAuftragId(),
                        HurricanSystemRegistry.instance().getSessionId());

                MessageHelper.showMessageDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        "Auftrag wurde auf Absage gesetzt", "Auftrag auf Absage", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }
}
