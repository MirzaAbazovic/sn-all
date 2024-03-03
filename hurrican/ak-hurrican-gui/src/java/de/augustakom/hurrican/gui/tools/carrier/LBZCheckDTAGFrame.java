/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2005 09:01:26
 */
package de.augustakom.hurrican.gui.tools.carrier;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer den LBZ-DTAG Check.
 *
 *
 */
public class LBZCheckDTAGFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konstruktor.
     */
    public LBZCheckDTAGFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("LBZ-Check DTAG");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new LBZCheckDTAGPanel(), BorderLayout.CENTER);
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


