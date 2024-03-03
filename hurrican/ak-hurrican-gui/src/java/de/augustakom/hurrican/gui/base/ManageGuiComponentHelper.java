/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2009 08:03:01
 */
package de.augustakom.hurrican.gui.base;

import org.apache.log4j.Logger;

import de.augustakom.authentication.model.temp.AKCompBehaviorSummary;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKGUIService;
import de.augustakom.authentication.utils.AKCompBehaviorTools;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;


/**
 * Hilfsklasse, ueber die die Berechtigungen von GUI-Komponenten ermittelt / ausgewertet werden koennen.
 *
 *
 */
public class ManageGuiComponentHelper {

    private static final Logger LOGGER = Logger.getLogger(ManageGuiComponentHelper.class);

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     *
     * @param components
     */
    public static void manageGUI(Object parent, AKManageableComponent[] components) {
        manageGUI(parent.getClass().getName(), components);
    }

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     *
     * @param parent
     * @param components
     */
    public static void manageGUI(String parent, AKManageableComponent[] components) {
        try {
            IServiceLocator serviceLocator = ServiceLocatorRegistry.instance().getServiceLocator(IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKGUIService guiService = serviceLocator.getService(AKAuthenticationServiceNames.GUI_SERVICE, AKGUIService.class, null);

            AKCompBehaviorSummary[] behaviors = guiService.evaluateRights(
                    HurricanSystemRegistry.instance().getSessionId(),
                    AKCompBehaviorTools.createCompBehaviorSummary(parent, components));
            AKCompBehaviorTools.assignUserRights(components, parent, behaviors);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

}


