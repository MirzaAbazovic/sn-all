/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2004 11:38:09
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Action, um den Verlauf zu einem best. Auftrag zu stornieren.
 *
 *
 */
public class StornoBAAction extends AbstractAuftragAction {

    private static final Logger LOGGER = Logger.getLogger(StornoBAAction.class);

    private CCAuftragModel auftragModel = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (hasChanges()) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Speichern Sie bitte erst die Änderungen ab, bevor\nSie den Bauauftrag stornieren.",
                    "Änderungen speichern", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        auftragModel = findModelByType(CCAuftragModel.class);
        if (auftragModel != null) {
            verlaufStornieren();
        }
        else {
            LOGGER.error("AKModelOwner.getModel liefert nicht das erwartete Objekt zurueck!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Der Bauauftrag konnte nicht storniert werden, da die\n" +
                            "Auftragsdaten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /* Storniert den aktuellen Bauauftrag des Auftrags - wenn moeglich. */
    private void verlaufStornieren() {
        try {
            BAService bas = getCCService(BAService.class);
            Verlauf actVerl = bas.findActVerlauf4Auftrag(auftragModel.getAuftragId(), false);
            if (actVerl != null) {
                boolean sendMail = false;
                StringBuilder msg = new StringBuilder();
                if (DateTools.isTodayOrNextWorkDay(actVerl.getRealisierungstermin())) {
                    sendMail = true;
                    msg.append("Der Bauauftrag wird heute oder morgen realisiert.");
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    msg.append("Bei Storno wird eine EMail an die Dispo versendet.");
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    msg.append("Soll der Bauauftrag trotzdem zurück gerufen werden?");
                }
                else {
                    msg.append("Soll der Bauauftrag wirklich storniert werden?");
                }

                int storno = MessageHelper.showConfirmDialog(getMainFrame(),
                        msg.toString(), "Bauauftrag stornieren?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (storno == JOptionPane.YES_OPTION) {
                    // Verlauf stornieren!
                    AKWarnings warnings = bas.verlaufStornieren(
                            actVerl.getId(), sendMail, HurricanSystemRegistry.instance().getSessionId());
                    if ((warnings != null) && warnings.isNotEmpty()) {
                        MessageHelper.showInfoDialog(getMainFrame(), warnings.getWarningsAsText(), null, true);
                    }
                    else {
                        MessageHelper.showMessageDialog(getMainFrame(),
                                "Bauauftrag wurde storniert.", "Abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
                    }
                    refreshFrame();
                }
            }
            else {
                MessageHelper.showMessageDialog(getMainFrame(),
                        "Es wurde kein aktiver Bauauftrag zu dem aktuellen Auftrag gefunden.",
                        "Storno nicht möglich", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}
