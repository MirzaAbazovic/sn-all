/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2005 14:34:49
 */
package de.augustakom.hurrican.gui.tools.consistency;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJTabbedPane;


/**
 * Frame fuer div. Konsistenz-Checks der Hurrican-Datenbank.
 *
 *
 */
public class ConsistenceCheckFrame extends AKJAbstractInternalFrame {

    public ConsistenceCheckFrame() {
        super("de/augustakom/hurrican/gui/tools/consistency/resources/ConsistenceCheckFrame.xml");
        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab(getSwingFactory().getText("consistence.check.history"), new HistoryConsistenceCheckPanel());
        tabbedPane.setToolTipTextAt(0, getSwingFactory().getText("consistence.check.history.tooltip"));

        tabbedPane.addTab(getSwingFactory().getText("consistence.check.intaccount"), new IntAccountConsistenceCheckPanel());
        tabbedPane.setToolTipTextAt(1, getSwingFactory().getText("consistence.check.intaccount.tooltip"));

        tabbedPane.addTab(getSwingFactory().getText("consistence.physiktyp"), new PhysiktypConsistenceCheckPanel());
        tabbedPane.setToolTipTextAt(2, getSwingFactory().getText("consistence.physiktyp.tooltip"));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public String getUniqueName() {
        return this.getClass().getName();
    }
}
