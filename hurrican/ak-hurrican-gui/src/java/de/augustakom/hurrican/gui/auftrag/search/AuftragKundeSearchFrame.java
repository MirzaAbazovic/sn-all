/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.06.2004 16:42:24
 */
package de.augustakom.hurrican.gui.auftrag.search;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame zur Suche nach Kunden bzw. Auftraegen.
 *
 *
 */
public class AuftragKundeSearchFrame extends AKJAbstractInternalFrame {

    /**
     * Konstruktor fuer das Frame.
     */
    public AuftragKundeSearchFrame() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragKundeSearchFrame.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected final void createGUI() {
        AuftragKundeSearchPanel searchPanel = new AuftragKundeSearchPanel();

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(searchPanel, BorderLayout.CENTER);
        this.pack();
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


