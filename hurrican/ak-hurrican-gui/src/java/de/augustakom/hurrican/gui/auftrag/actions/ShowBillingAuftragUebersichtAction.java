/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.08.2004 13:02:46
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.billing.BillingAuftragUebersichtDialog;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;


/**
 * Action, um den Uebersichtsdialog der Billing-Auftraege fuer einen Kunden zu oeffnen.
 *
 *
 */
public class ShowBillingAuftragUebersichtAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(ShowBillingAuftragUebersichtAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (getValue(MODEL_OWNER) instanceof AKModelOwner) {
            AKModelOwner mo = (AKModelOwner) getValue(MODEL_OWNER);
            if (mo.getModel() instanceof KundeAdresseView) {
                BillingAuftragUebersichtDialog dlg = new BillingAuftragUebersichtDialog(
                        (KundeAdresseView) mo.getModel());

                DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        dlg, true, true);
            }
            else {
                LOGGER.warn("Das Objekt von AKModelOwner.getModel() ist nicht vom Typ " + KundeAdresseView.class.getName());
            }
        }
        else {
            LOGGER.warn("Objekt mit Key <MODEL_OWNER> ist nicht vom Typ " + AKModelOwner.class.getName());
        }
    }

}


