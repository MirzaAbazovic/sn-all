/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2005 08:40:34
 */
package de.augustakom.hurrican.gui.tools.dbchecks;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame zur Durchfuehrung von div. DB-Kontrollabfragen fuer die Abteilung AM(vorher SCV).
 *
 *
 */
public class AmDBCheckFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konstruktor.
     */
    public AmDBCheckFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("AM-Kontrollabfragen");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new ScvDBCheckPanel(), BorderLayout.CENTER);
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
    @Override
    public void update(Observable o, Object arg) {
    }

}


