/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2007 13:45:42
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Darstellung und Bearbeitung der internen TAL-Bestellungen.
 *
 *
 */
public class CarrierBestellungInternFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konst.
     */
    public CarrierBestellungInternFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected final void createGUI() {
        setTitle("Interne TAL-Bestellung");

        CarrierBestellungInternPanel panel = new CarrierBestellungInternPanel();
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


