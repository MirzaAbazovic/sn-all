/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.2007 10:26:19
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Suche von Rangierungs-Budgets (Budgets fuer die Erweiterung von HVTs).
 *
 *
 */
public class RangierungBudgetSearchFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Const.
     */
    public RangierungBudgetSearchFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("HVT-Erweiterung Budgets");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new RangierungBudgetSearchPanel(), BorderLayout.CENTER);
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


