/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 09:00:42
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Frame fuer die Administration der Reports.
 *
 *
 */
public class ReportAdminFrame extends AbstractAdminFrame {

    private ReportAdminPanel adminPanel = null;
    private ReportTablePanel tablePanel = null;

    /**
     * Standardkonstruktor.
     */
    public ReportAdminFrame() {
        super(null, false);
        createGUI();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    protected AbstractAdminPanel[] getAdminPanels() {
        return new AbstractAdminPanel[] { adminPanel, tablePanel };
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Reports");

        adminPanel = new ReportAdminPanel();
        tablePanel = new ReportTablePanel(adminPanel);
        adminPanel.setReportTable(tablePanel.getReportTable());

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tablePanel);
        split.setBottomComponent(adminPanel);

        getChildPanel().add(split, BorderLayout.CENTER);
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


