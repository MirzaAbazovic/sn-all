/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2005 13:55:37
 */
package de.augustakom.hurrican.gui.tools.account;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die GUI, um einen Int-Account auf einen anderen Auftrag zu verschieben.
 *
 *
 */
public class MoveIntAccountFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Konstruktor.
     */
    public MoveIntAccountFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Account verschieben");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new MoveIntAccountPanel(), BorderLayout.CENTER);
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


