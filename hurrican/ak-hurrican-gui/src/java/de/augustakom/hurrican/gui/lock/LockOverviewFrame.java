/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.08.2009 11:26:30
 */
package de.augustakom.hurrican.gui.lock;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Darstellung u. Verwaltung von Sperren.
 *
 *
 */
public class LockOverviewFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konst.
     */
    public LockOverviewFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Sperrverwaltung");
        setIcon("de/augustakom/hurrican/gui/images/locked.gif");

        LockOverviewPanel panel = new LockOverviewPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }

}
