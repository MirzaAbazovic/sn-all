/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2010 16:33:23
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;

/**
 * Frame fuer die Suche nach freien Rangierungen an einem bestimmten HVT.
 */
public class PortStatisticsFrame extends AKJAbstractInternalFrame {

    /**
     * default constructor
     */
    public PortStatisticsFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle("Portverbrauch");
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new PortStatisticsPanel(), BorderLayout.CENTER);
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
}
