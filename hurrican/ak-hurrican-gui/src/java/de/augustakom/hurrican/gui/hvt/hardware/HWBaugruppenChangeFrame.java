/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2010 11:40:58
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 * Frame fuer die Erfassung / Bearbeitung von Baugruppen-Schwenks.
 */
public class HWBaugruppenChangeFrame extends AKJAbstractInternalFrame {

    /**
     * Default-Const.
     */
    public HWBaugruppenChangeFrame() {
        super(null);
        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle("Baugruppen-Schwenks");
        HWBaugruppenChangePanel panel = new HWBaugruppenChangePanel();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(panel, BorderLayout.CENTER);
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


