/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2005 08:24:10
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame zur Rangierungs-Kreuzung.
 *
 *
 */
public class RangierungKreuzenFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konstruktor.
     */
    public RangierungKreuzenFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Rangierungen kreuzen");

        this.setLayout(new BorderLayout());
        this.add(new RangierungKreuzenPanel(), BorderLayout.CENTER);
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


