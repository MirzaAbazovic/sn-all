/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2005 08:59:21
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame, um von einer best. Endstelle die Rangierung zu 'loeschen'.
 *
 *
 */
public class RemoveRangierungFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konstruktor.
     */
    public RemoveRangierungFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Rangierung entfernen");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new RemoveRangierungPanel(), BorderLayout.CENTER);
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


