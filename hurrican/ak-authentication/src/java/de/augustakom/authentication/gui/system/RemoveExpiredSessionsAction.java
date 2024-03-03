/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.10.2005 07:53:36
 */
package de.augustakom.authentication.gui.system;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.common.gui.swing.MessageHelper;

/**
 * Action, um alle abgelaufenen Sessions aus der Datenbank zu entfernen.
 */
public class RemoveExpiredSessionsAction extends AbstractAuthenticationServiceAction {

    private static final Logger LOGGER = Logger.getLogger(RemoveExpiredSessionsAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        int opt = MessageHelper.showYesNoQuestion(GUISystemRegistry.instance().getMainFrame(),
                "Sollen die abgelaufenen Sessions wirklich\nentfernt werden?", "Sessions entfernen?");
        if (opt == JOptionPane.YES_OPTION) {
            removeExpiredSessions();
        }
    }

    /* Entfernt abgelaufene Sessions aller Applikationen. */
    private void removeExpiredSessions() {
        try {
            AKLoginService loginService = getAuthenticationService(AKAuthenticationServiceNames.LOGIN_SERVICE, AKLoginService.class);
            List<AKUserSession> removed = loginService.removeExpiredSessions(null, new Date());
            if (removed != null) {
                MessageHelper.showMessageDialog(GUISystemRegistry.instance().getMainFrame(),
                        "Es wurden " + removed.size() + " Sessions entfernt.", "Sessions entfernt",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
    }

}
