/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2006 17:02:00
 */
package de.augustakom.hurrican.gui.tools.ewsd;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Darstellung von Dialern.
 *
 *
 */
public class DialerFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Const.
     */
    public DialerFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Dialer (Sperrkennzahlen)");
        setIcon("de/augustakom/hurrican/gui/images/phone.gif");

        DialerPanel dp = new DialerPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(dp, BorderLayout.CENTER);
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


