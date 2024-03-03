/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 07:54:03
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.wizards.abgleich.AuftragAbgleichWizard;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Oeffnet einen Wizard, ueber den die CC- und Billing-Auftraege abgeglichen werden koennen.
 *
 *
 */
public class ShowAuftragsMonitorAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowAuftragsMonitorAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getValue(MODEL_OWNER) instanceof AKModelOwner) {
            try {
                AKModelOwner mo = (AKModelOwner) getValue(MODEL_OWNER);
                if (mo.getModel() instanceof KundenModel) {
                    AuftragAbgleichWizard dlg = new AuftragAbgleichWizard(
                            ((KundenModel) mo.getModel()).getKundeNo(), withTaifunOrderSelection());

                    DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(),
                            dlg, true, true);
                }
                else {
                    LOGGER.warn("Das Objekt von AKModelOwner.getModel() ist nicht vom Typ " + KundenModel.class.getName());
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
            finally {
                SwingHelper.moveToScreenFront(HurricanSystemRegistry.instance().getMainFrame());
            }
        }
        else {
            LOGGER.warn("Objekt mit Key <MODEL_OWNER> ist nicht vom Typ " + AKModelOwner.class.getName());
        }
    }

    /**
     * Angabe, ob der Wizard mit einer Taifun Auftragsauswahl (true) gestartet oder ob der gesamte Kunde (false)
     * abgeglichen werden soll.
     */
    protected boolean withTaifunOrderSelection() {
        return false;
    }

}


