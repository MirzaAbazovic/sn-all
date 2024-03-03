/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 13:29:09
 */
package de.augustakom.authentication.gui.system;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.locator.ServiceLocator;

/**
 * Action, um die Applikation zu beenden.
 */
public class ExitAction extends AbstractAction {

    private static final Logger LOGGER = Logger.getLogger(ExitAction.class);

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DM_EXIT", justification = "ExitAction soll VM tatsaechlich beenden")
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Shutting down ServiceLocator");
        ServiceLocator.instance().shutdownServiceLocator();
        System.exit(0);
    }
}
