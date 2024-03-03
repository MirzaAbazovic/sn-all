/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2006 15:08:09
 */
package de.augustakom.hurrican.gui.tools.rechnung;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJScrollPane;


/**
 * Frame, um den Rechnungskopiervorgang zu starten.
 *
 *
 */
public class RechnungKopierFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Const.
     */
    public RechnungKopierFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle("Rechnungen kopieren");

        RechnungKopierPanel panel = new RechnungKopierPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new AKJScrollPane(panel), BorderLayout.CENTER);
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
    @Override
    public void update(Observable o, Object arg) {
    }

}


