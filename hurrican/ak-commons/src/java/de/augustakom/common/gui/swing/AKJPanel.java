/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;


/**
 * AK-Implementierung eines JPanels.
 *
 *
 * @see javax.swing.JPanel
 */
public class AKJPanel extends JPanel {

    /**
     * Erzeugt ein neues Panel mit FlowLayout.
     */
    public AKJPanel() {
        super();
    }

    /**
     * Erzeugt ein neues Panel mit FlowLayout und der angegebenen Buffering-Strategie.
     *
     * @param isDoubleBuffered
     */
    public AKJPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * Erzeugt eine neues Panel mit Angabe des Layout-Managers.
     *
     * @param layout
     */
    public AKJPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * Erzeugt ein neues Panel mit Angabe des Layout-Managers. <br> Das Panel erhaelt einen Titled-Border mit dem Text
     * <code>borderTitle</code>.
     *
     * @param layout
     * @param borderTitle
     */
    public AKJPanel(LayoutManager layout, String borderTitle) {
        super(layout);
        setBorder(BorderFactory.createTitledBorder(borderTitle));
    }

    /**
     * Erzeugt ein neues Panel mit Angabe des Layout-Managers und der zu verwendenden Buffering-Strategie.
     *
     * @param layout
     * @param isDoubleBuffered
     */
    public AKJPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * Erzeugt ein neues Panel mit Angabe der Hintergrundfarbe
     *
     * @param bgColor
     */
    public AKJPanel(Color bgColor) {
        super();
        setBackground(bgColor);
    }

    /**
     * Erzeugt ein neues Panel mit Angabe der Preferred-Size
     *
     * @param prefSize
     */
    public AKJPanel(Dimension prefSize) {
        super();
        setPreferredSize(prefSize);
    }
}
