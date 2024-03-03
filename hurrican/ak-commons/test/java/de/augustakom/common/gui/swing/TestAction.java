/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

/**
 * Test-Implementierung einer Action.
 *
 *
 */
public class TestAction extends AbstractAction {

    private static final Logger LOGGER = Logger.getLogger(TestAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("action performed of TestAction");
    }

    /**
     * @see javax.swing.Action#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean newValue) {
        super.setEnabled(newValue);
        LOGGER.debug("------> action.setEnabled");
    }
}
