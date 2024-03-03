/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 13:59:35
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJScrollPane;


/**
 * Frame zur Darstellung der HVT-Bestellungen.
 *
 *
 */
public class HVTBestellungenFrame extends AKJAbstractInternalFrame {

    /**
     * Standardkonstruktor.
     */
    public HVTBestellungenFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("HVT-Bestellungen");

        HVTBestellungenPanel panel = new HVTBestellungenPanel();
        AKJScrollPane scroll = new AKJScrollPane(panel);
        scroll.setBorder(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scroll, BorderLayout.CENTER);

        pack();
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


