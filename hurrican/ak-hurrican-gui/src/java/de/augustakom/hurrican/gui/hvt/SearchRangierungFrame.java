/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2006 16:07:23
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;

/**
 * Frame fuer die Suche nach freien Rangierungen an einem bestimmten HVT.
 *
 *
 */
public class SearchRangierungFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Const.
     */
    public SearchRangierungFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Rangierungssuche");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new SearchRangierungPanel(), BorderLayout.CENTER);
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
