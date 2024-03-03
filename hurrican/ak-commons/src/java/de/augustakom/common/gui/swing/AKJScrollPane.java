/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2004 09:50:37
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;


/**
 * AK-Implementierung einer <code>javax.swing.JScrollPane</code>
 *
 *
 */
public class AKJScrollPane extends JScrollPane {

    /**
     * @see javax.swing.JScrollPane()
     */
    public AKJScrollPane() {
        super();
    }

    /**
     * @see javax.swing.JScrollPane(int, int)
     */
    public AKJScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
    }

    /**
     * @see javax.swing.JScrollPane(Component)
     */
    public AKJScrollPane(Component view) {
        super(view);
    }

    /**
     * @see javax.swing.JScrollPane(Component)
     */
    public AKJScrollPane(Component view, Dimension prefSize) {
        super(view);
        setPreferredSize(prefSize);
    }

    /**
     * @see javax.swing.JScrollPane(Component, int, int)
     */
    public AKJScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
    }

}
