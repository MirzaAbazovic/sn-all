/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2004 08:26:59
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.utils.PrintVerlaufHelper;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Action, um den Bauauftrag zum aktuell gewaehlten Auftrag zu drucken.
 *
 *
 */
public class PrintBAAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(PrintBAAction.class);

    private CCAuftragModel auftragModel = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        auftragModel = findModelByType(CCAuftragModel.class);
        if (auftragModel != null) {
            printVerlauf();
        }
        else {
            LOGGER.error("Das AuftragModel konnte nicht ermittelt werden!");
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Das Anschreiben mit den Online-Daten konnte nicht gedruckt werden, da der Auftrag " +
                            "nicht ermittelt werden konnte!", "Abbruch", JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Gibt an, ob eine Projektierung ({@code true}) oder ein Bauauftrag ({@code false}) erstellt werden soll.
     *
     * @return
     */
    protected boolean printProjektierung() {
        return false;
    }

    /**
     * Veranlasst den Druck des letzten Verlaufs (Bauauftrag oder Projektierung) zu dem Auftrag.
     */
    protected void printVerlauf() {
        try {
            PrintVerlaufHelper helper = new PrintVerlaufHelper();
            helper.printVerlauf4Auftrag(auftragModel.getAuftragId(), printProjektierung(), true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }
}


