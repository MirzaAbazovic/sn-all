/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2005 11:05:41
 */
package de.augustakom.hurrican.gui.auftrag.views;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractInternalServiceFrame;


/**
 * Frame fuer die Darstellung aller Auftraege, die noch zu realisieren sind.
 *
 *
 */
public class AuftragsVorlaufFrame extends AbstractInternalServiceFrame {

    /**
     * Default-Konstruktor.
     */
    public AuftragsVorlaufFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected final void createGUI() {
        setTitle("Auftrags-Vorlauf");

        AuftragsVorlaufPanel panel = new AuftragsVorlaufPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    public String getUniqueName() {
        return "Auftrags-Vorlauf";
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


