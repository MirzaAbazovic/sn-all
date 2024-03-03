/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2004 12:34:30
 */
package de.augustakom.common.gui.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;


/**
 * AK-Implementierung einer JSplitPane.
 *
 *
 */
public class AKJSplitPane extends JSplitPane {

    /**
     * @see javax.swing.JSplitPane()
     */
    public AKJSplitPane() {
        super();
    }

    /**
     * @see javax.swing.JSplitPane(int)
     */
    public AKJSplitPane(int newOrientation) {
        super(newOrientation);
    }

    /**
     * @param newOrientation
     * @param border
     */
    public AKJSplitPane(int newOrientation, Border border) {
        super(newOrientation);
        setBorder(border);
    }

    /**
     * @see javax.swing.JSplitPane(int, boolean)
     */
    public AKJSplitPane(int newOrientation, boolean newContinuousLayout) {
        super(newOrientation, newContinuousLayout);
    }

    /**
     * @see javax.swing.JSplitPane(int, boolean, Component, Component)
     */
    public AKJSplitPane(int newOrientation, boolean newContinuousLayout,
            Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
    }

    /**
     * @see javax.swing.JSplitPane(int, Component, Component)
     */
    public AKJSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newLeftComponent, newRightComponent);
    }
}


