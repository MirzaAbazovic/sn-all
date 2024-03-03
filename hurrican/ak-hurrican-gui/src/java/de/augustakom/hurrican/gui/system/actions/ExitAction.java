/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2004 11:51:32
 */
package de.augustakom.hurrican.gui.system.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;


/**
 * Action, um die gesamte Applikation zu beenden.
 *
 *
 */
public class ExitAction extends AKAbstractAction {

    private static final Logger LOGGER = Logger.getLogger(ExitAction.class);
    private static final long serialVersionUID = 5783908962199219135L;

    private AKLoginService loginService;

    /**
     * Default constructor for dependency injection
     *
     * @throws ServiceNotFoundException
     */
    public ExitAction() throws ServiceNotFoundException {
        IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);

        loginService = authSL.getService(
                AKAuthenticationServiceNames.LOGIN_SERVICE, AKLoginService.class, null);
    }


    public ExitAction(AKLoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("DM_EXIT")
    public void actionPerformed(ActionEvent e) {
        try {
            // User abmelden
            if (loginService != null) {
                loginService.logout(HurricanSystemRegistry.instance().getSessionId());
            }

            // Services schliessen
            String[] locators = ServiceLocatorRegistry.instance().getServiceLocatorNames();
            if (locators != null) {
                for (String locator : locators) {
                    LOGGER.info("Remove/close ServiceLocator: " + locator);
                    ServiceLocatorRegistry.instance().removeServiceLocator(locator);
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        finally {
            System.exit(0);
        }
    }

}
