/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 07:52:42
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import javax.swing.*;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.anlage.CreateHurricanAuftragWizard;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Action, um einen neuen Hurrican-Auftrag anzulegen. <br> Die Action oeffnet einen Dialog, in dem die Auftragsart (das
 * Produkt) ausgewaehlt und alle benoetigten Daten eingetragen werden koennen.
 *
 *
 */
public class CreateAuftragAction extends AbstractServiceAction {

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String msg = "Die Kundennummer konnte nicht ermittelt werden.";
        String title = "Problem";

        if (getValue(MODEL_OWNER) instanceof AKModelOwner) {
            AKModelOwner mo = (AKModelOwner) getValue(MODEL_OWNER);
            if ((mo.getModel() instanceof KundenModel) &&
                    (((KundenModel) mo.getModel()).getKundeNo() != null)) {
                Long kNo = ((KundenModel) mo.getModel()).getKundeNo();

                CreateHurricanAuftragWizard wizard = new CreateHurricanAuftragWizard(kNo);

                DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), wizard, true, true);
            }
            else {
                MessageHelper.showMessageDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        msg, title, JOptionPane.WARNING_MESSAGE);
            }
        }
        else {
            MessageHelper.showMessageDialog(HurricanSystemRegistry.instance().getMainFrame(),
                    msg, title, JOptionPane.WARNING_MESSAGE);
        }
    }

}


