/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2005 10:12:37
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame zur Konfiguration von Service-Chains.
 *
 *
 */
public class ServiceChainConfigFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konstruktor
     */
    public ServiceChainConfigFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("ServiceChain-Konfiguration");

        ServiceChainConfigPanel panel = new ServiceChainConfigPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
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


