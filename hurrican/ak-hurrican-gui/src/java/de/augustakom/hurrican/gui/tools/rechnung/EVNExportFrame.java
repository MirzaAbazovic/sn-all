/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 11:58:17
 */
package de.augustakom.hurrican.gui.tools.rechnung;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJScrollPane;


/**
 * Frame fuer den Export der Maxi-Rechnungen.
 *
 *
 */
public class EVNExportFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Const.
     */
    public EVNExportFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("EVN-Export");

        EVNExportPanel panel = new EVNExportPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new AKJScrollPane(panel), BorderLayout.CENTER);
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


