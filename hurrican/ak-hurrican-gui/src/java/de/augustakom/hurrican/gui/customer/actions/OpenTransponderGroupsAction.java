/*
 * Copyright (c) 2011 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 14:17:31
 */
package de.augustakom.hurrican.gui.customer.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.customer.TransponderGroupFrame;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;


/**
 * Action, um das Frame mit den Transponder-Gruppen des Kunden zu oeffnen.
 */
public class OpenTransponderGroupsAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(OpenTransponderGroupsAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (getValue(MODEL_OWNER) instanceof AKModelOwner) {
            AKModelOwner mo = (AKModelOwner) getValue(MODEL_OWNER);
            if (mo.getModel() instanceof KundeAdresseView) {
                TransponderGroupFrame frame = new TransponderGroupFrame(((KundeAdresseView) mo.getModel()).getKundeNo());
                getMainFrame().registerFrame(frame, false);
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


