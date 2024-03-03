/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.augustakom.common.gui.utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Simplified Horizontal- and Vertical Layouts.
 */
public class Layout {
    public static JComponent vbox(JComponent... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (JComponent c : components) {
            c.setAlignmentX(0f);
            panel.add(c);
        }
        return panel;
    }

    public static JComponent hbox(JComponent... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        for (JComponent c : components) {
            c.setAlignmentY(0f);
            panel.add(c);
        }
        return panel;
    }

    public static JPanel hspace(int space) {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(new Insets(0, 0, 0, space)));
        return panel;
    }

    public static JPanel vspace(int space) {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(new Insets(0, 0, space, 0)));
        return panel;
    }

}
