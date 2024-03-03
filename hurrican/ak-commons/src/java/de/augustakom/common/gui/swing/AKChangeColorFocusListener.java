/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created 01.06.2004 08:02:10
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKSwingConstants;


/**
 * FocusListener fuer das TextField. Ueber den Focus-Listener wird die Hintergrundfarbe des TextFields gewechselt, wenn
 * es den Focus erhaelt bzw. abgibt.
 */
class AKChangeColorFocusListener implements FocusListener {

    AKChangeColorFocusListener() {
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
        if (e.getSource() instanceof AKColorChangeableComponent && e.getSource() instanceof Component) {
            AKColorChangeableComponent ccc = (AKColorChangeableComponent) e.getSource();
            Component c = (Component) e.getSource();

            if (e.getSource() instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) e.getSource();
                if (!tc.isEditable() || !tc.isEnabled()) {
                    c.setBackground(AKSwingConstants.PANEL_BACKGROUND_COLOR);
                }
                else {
                    Color bg = (ccc.getActiveColor() != null) ? ccc.getActiveColor() : Color.white;
                    c.setBackground(bg);
                }
            }
            else {
                if (c.isEnabled()) {
                    Color bg = (ccc.getActiveColor() != null) ? ccc.getActiveColor() : Color.white;
                    c.setBackground(bg);
                }
                else {
                    c.setBackground(AKSwingConstants.PANEL_BACKGROUND_COLOR);
                }
            }
        }
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        if (e.getSource() instanceof AKColorChangeableComponent && e.getSource() instanceof Component) {
            AKColorChangeableComponent ccc = (AKColorChangeableComponent) e.getSource();
            Component c = (Component) e.getSource();

            if (e.getSource() instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) e.getSource();
                if (!tc.isEditable() || !tc.isEnabled()) {
                    c.setBackground(AKSwingConstants.PANEL_BACKGROUND_COLOR);
                }
                else {
                    Color bg = (ccc.getActiveColor() != null) ? ccc.getInactiveColor() : Color.white;
                    c.setBackground(bg);
                }
            }
            else {
                if (c.isEnabled()) {
                    Color bg = (ccc.getActiveColor() != null) ? ccc.getInactiveColor() : Color.white;
                    c.setBackground(bg);
                }
                else {
                    c.setBackground(AKSwingConstants.PANEL_BACKGROUND_COLOR);
                }
            }
        }
    }
}
