/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2008 08:57:59
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Anzeige des Ressourcen-Monitors
 *
 *
 */
public class RsMonitorConfigFrame extends AKJAbstractInternalFrame {

    /**
     * Standardkonstruktor.
     */
    public RsMonitorConfigFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Konfiguration Ressourcenmonitor");

        RsMonitorConfigPanel rsPanel = new RsMonitorConfigPanel();
        getContentPane().add(rsPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


