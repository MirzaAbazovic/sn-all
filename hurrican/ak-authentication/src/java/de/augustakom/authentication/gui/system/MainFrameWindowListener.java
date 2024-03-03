/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 10:07:24
 */
package de.augustakom.authentication.gui.system;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;


/**
 * WindowListener fuer das MainFrame.
 */
class MainFrameWindowListener extends WindowAdapter {

    private static final Logger LOGGER = Logger.getLogger(MainFrameWindowListener.class);

    @Override
    public void windowClosing(WindowEvent e) {
        LOGGER.info("Exit application.");
        ExitAction action = new ExitAction();
        action.actionPerformed(new ActionEvent(this, 0, "exit"));
        ((JFrame) e.getSource()).setVisible(false);
    }
}
