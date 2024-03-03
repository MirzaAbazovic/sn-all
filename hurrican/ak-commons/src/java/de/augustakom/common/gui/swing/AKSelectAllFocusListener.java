/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2005 16:27:56
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;
import javax.swing.text.*;

/**
 * Focus-Listener, um den gesamten Inhalt einer Text-Komponente zu markieren, wenn diese den Focus erhaelt.
 *
 *
 */
public class AKSelectAllFocusListener implements FocusListener {

    /**
     * Konstruktor
     */
    public AKSelectAllFocusListener() {
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        if (e.getSource() instanceof JTextComponent) {
            ((JTextComponent) e.getSource()).selectAll();
        }
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
    }
}

