/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 17:13:36
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;

import de.augustakom.hurrican.gui.base.AbstractInternalServiceFrame;


/**
 * Frame fuer die Konfiguration der Bauauftragssteuerung.
 *
 *
 */
public class BAVerlaufConfigFrame extends AbstractInternalServiceFrame {

    /**
     * Default-Konstruktor.
     *
     * @param resource
     */
    public BAVerlaufConfigFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected final void createGUI() {
        setTitle("Bauauftrags-Konfiguration");
        setIcon("de/augustakom/hurrican/gui/images/verlauf.gif");

        BAVerlaufConfigPanel panel = new BAVerlaufConfigPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
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


