/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2004 09:48:24
 */
package de.augustakom.hurrican.gui.system;

import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;


/**
 * WindowListener fuer das MainFrame.
 *
 *
 */
class MainFrameWindowListener extends WindowAdapter {

    private static final Logger LOGGER = Logger.getLogger(MainFrameWindowListener.class);

    /**
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
        LOGGER.info("Exit application.");
        doLogout();
        System.exit(0);
    }

    /* Fuehrt den Logout durch */
    private void doLogout() {
        // User abmelden
        try {
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);

            AKLoginService loginService = authSL.getService(
                    AKAuthenticationServiceNames.LOGIN_SERVICE, AKLoginService.class, null);
            loginService.logout(HurricanSystemRegistry.instance().getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        // Services schliessen
        Collection locators = ServiceLocatorRegistry.instance().getServiceLocators();
        if (locators != null) {
            Iterator it = locators.iterator();
            while (it.hasNext()) {
                IServiceLocator locator = (IServiceLocator) it.next();
                locator.closeServiceLocator();
            }
        }
    }
}
