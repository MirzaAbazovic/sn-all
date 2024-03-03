/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 15:22:27
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractInternalServiceFrame;


/**
 * Frame fuer die Darstellung aller VPN-Auftraege.
 *
 *
 */
public class VPNVertragsUebersichtFrame extends AbstractInternalServiceFrame {

    /**
     * Konstruktor.
     */
    public VPNVertragsUebersichtFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("VPN-Vertragsübersicht");
        setIcon("de/augustakom/hurrican/gui/images/vpn.gif");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new VPNVertragsUebersichtPanel(), BorderLayout.CENTER);

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


