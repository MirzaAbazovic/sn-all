/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2005 11:37:12
 */
package de.augustakom.hurrican.gui.auftrag.views;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractInternalServiceFrame;


/**
 * Frame fuer die Anzeige der offenen Auftraege fuer AM.
 *
 *
 */
public class AmAuftragsFrame extends AbstractInternalServiceFrame {

    /**
     * Default-Konstruktor.
     */
    public AmAuftragsFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("offene Aufträge");

        ScvAuftragsPanel panel = new ScvAuftragsPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    @Override
    public String getUniqueName() {
        return "scv.offene.auftraege";
    }
}


