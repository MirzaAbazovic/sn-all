/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.10.2007 11:19:03
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Action, um eine Bündelnummer aus dem Auftrag eines Kunden zu entfernen. <br> <br> Die Action 'fragt', ob eine
 * Buendelnummer aus einem Auftrag entfernt werden soll. <br> - OK: die Buendelnummer wird vom Auftrag entfernt -
 * Abbrechen: zurück zur Übersicht
 *
 *
 */
public class DeleteBuendelAction {

    private static final Logger LOGGER = Logger.getLogger(DeleteBuendelAction.class);

    public void perform(CCKundeAuftragView ccKundeAuftragView) {
        try {
            if (ccKundeAuftragView != null) {
                CCAuftragService as = CCServiceFinder.instance().getCCService(CCAuftragService.class);
                AuftragDaten ad = as.findAuftragDatenByAuftragId(ccKundeAuftragView.getAuftragId());
                if (ad == null) {
                    throw new HurricanGUIException("Auftragsdaten konnten nicht ermittelt werden.");
                }
                deleteBuendel(ad);
            }
            else {
                throw new HurricanGUIException("Auftrag konnte nicht ermittelt werden.");
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }

    /*
     * Funktion, um eine Buendelnummer aus dem ausgewaehlten Auftrag zu entfernen.
     */
    private void deleteBuendel(AuftragDaten auftragDaten) throws HurricanGUIException {
        if (auftragDaten.getBuendelNr() == null) {
            throw new HurricanGUIException("Der Auftrag ist keinem Bündel zugeordnet!");
        }

        int options = MessageHelper.showOptionDialog(HurricanSystemRegistry.instance().getMainFrame(),
                "Soll die Bündelnummer wirklich entfernt werden?",
                "Bündelnummer entfernen?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (options == JOptionPane.OK_OPTION) {
            deleteBuendelNrFromAuftrag(auftragDaten);
        }
    }

    /*
     * Entfernt die Buendelnummer des Kunden vom Auftrag.
     */
    private void deleteBuendelNrFromAuftrag(AuftragDaten auftragDaten) throws HurricanGUIException {
        try {
            CCAuftragService as = CCServiceFinder.instance().getCCService(CCAuftragService.class);
            auftragDaten.setBuendelNr(null);
            auftragDaten.setBuendelNrHerkunft(null);
            as.saveAuftragDaten(auftragDaten, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Die Entbündelung konnte nicht durchgeführt werden. Grund:\n" + e.getMessage(), e);
        }
    }

}


