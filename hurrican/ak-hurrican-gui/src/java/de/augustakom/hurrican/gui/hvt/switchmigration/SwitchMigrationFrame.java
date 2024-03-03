/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2011 14:04:53
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;

/**
 *
 */
public class SwitchMigrationFrame extends AKJAbstractInternalFrame {

    public SwitchMigrationFrame() {
        super("de/augustakom/hurrican/gui/hvt/switchmigration/resources/SwitchMigrationFrame.xml");
        createGUI();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        SwitchMigrationPanel switchMigrationPanel = new SwitchMigrationPanel();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(switchMigrationPanel, BorderLayout.CENTER);
    }

    @Override
    protected void execute(String command) {
    }

}


