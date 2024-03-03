/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.05.2004
 */
package de.augustakom.common.gui.swing;

import javax.swing.*;


/**
 * AK-Implementierung einer JTabbedPane
 *
 *
 * @see javax.swing.JTabbedPane
 */
public class AKJTabbedPane extends JTabbedPane {

    /**
     * @see javax.swing.JTabbedPane()
     */
    public AKJTabbedPane() {
        super();
    }

    /**
     * @param tabPlacement
     * @see javax.swing.JTabbedPane(int)
     */
    public AKJTabbedPane(int tabPlacement) {
        super(tabPlacement);
    }

    /**
     * @param tabPlacement
     * @param tabLayoutPolicy
     * @see javax.swing.JTabbedPane(int, int)
     */
    public AKJTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
    }

}
