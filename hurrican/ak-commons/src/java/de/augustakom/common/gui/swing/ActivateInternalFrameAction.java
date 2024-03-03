/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2004 07:54:33
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import org.apache.log4j.Logger;


/**
 * Action, um ein InternalFrame zu aktivieren. <br> Diese Action ist dafuer gedacht, dass mit ihr ein MenuItem erstellt
 * wird, das in ein 'Window'-Menu eingetragen wird.
 *
 *
 */
public class ActivateInternalFrameAction extends AbstractAction {

    private static final Logger LOGGER = Logger.getLogger(ActivateInternalFrameAction.class);

    private AKJInternalFrame frame = null;

    /**
     * Konstruktor fuer die Action.
     *
     * @param frame InternalFrame, das durch die Action wieder aktiviert werden soll.
     */
    public ActivateInternalFrameAction(AKJInternalFrame frame) {
        this.frame = frame;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            frame.setSelected(true);
            frame.toFront();

            // minimiertes Frame wieder 'oeffnen'
            if (frame.isIcon()) {
                frame.setIcon(false);
                frame.setMaximum(false);
            }
        }
        catch (PropertyVetoException ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
    }

    /**
     * Gibt das InternalFrame zurueck, fuer das die Action verantwortlich ist.
     *
     * @return
     */
    public AKJInternalFrame getFrame() {
        return frame;
    }
}
