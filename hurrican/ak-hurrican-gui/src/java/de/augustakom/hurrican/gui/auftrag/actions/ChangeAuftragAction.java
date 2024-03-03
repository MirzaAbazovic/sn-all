/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2005 16:55:59
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Action, um einen bestehenden Auftrag zu aendern.
 *
 *
 */
public class ChangeAuftragAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(ChangeAuftragAction.class);

    private AuftragDaten auftragDaten = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (hasChanges()) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Speichern Sie bitte erst die Änderungen ab, bevor\nSie eine Auftragsänderung durchführen wollen.",
                    "Änderungen speichern", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        auftragDaten = findModelByType(AuftragDaten.class);
        if (auftragDaten != null) {
            changeAuftrag();
        }
        else {
            LOGGER.error("Die AuftragDaten konnten nicht ermittelt werden!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Der Auftrag kann nicht geändert werden, da die\n" +
                            "Auftragsdaten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /* Ruft einen Dialog zur Aenderung des Auftrags auf. */
    private void changeAuftrag() {
        try {
            if (changeAllowed()) {
                // keinen Wizard anzeigen, sondern nur Status auf 'Aenderung'
                int selection = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Soll der Auftrag in einen Änderungsstatus versetzt werden?", "Auftrag ändern?");
                if (selection == JOptionPane.YES_OPTION) {
                    CCAuftragService as = getCCService(CCAuftragService.class);
                    auftragDaten.setStatusId(AuftragStatus.AENDERUNG);
                    auftragDaten.setBearbeiter(HurricanSystemRegistry.instance().getCurrentLoginName());
                    as.saveAuftragDaten(auftragDaten, false);

                    refreshFrame();
                }
            }
            else {
                if (auftragDaten.isInAenderung()) {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Auftrag befindet sich bereits in Änderung.\n" +
                                    "Bitte Bauauftrag erstellen", null, true
                    );
                    // evtl. Frage einbauen, ob Bauauftrag erstellt werden soll
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Auftrag kann nicht geändert werden!\n" +
                                    "Es handelt sich entweder um einen neuen Auftrag oder " +
                                    "es befindet sich ein Bauauftrag im Umlauf.", null, true
                    );
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Ueberprueft, ob der Auftrag geaendert werden darf. */
    private boolean changeAllowed() throws ServiceNotFoundException, FindException, HurricanGUIException {
        ProduktService ps = getCCService(ProduktService.class);
        Produkt produkt = ps.findProdukt(auftragDaten.getProdId());
        if (produkt != null) {
            if (BooleanTools.nullToFalse(produkt.getElVerlauf())) {
                if (auftragDaten.isInBetrieb()) {
                    return true;
                }
                return false;
            }
            else {
                return true;
            }
        }
        else {
            throw new HurricanGUIException("Das Produkt zu dem Auftrag konnte nicht ermittelt werden!");
        }
    }
}


