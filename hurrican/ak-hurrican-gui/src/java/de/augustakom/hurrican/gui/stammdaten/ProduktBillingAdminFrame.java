/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2006 14:54:02
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Admin-Frame für die Verwaltung der Produkte aus dem Billing-System
 *
 *
 */
public class ProduktBillingAdminFrame extends AbstractAdminFrame {

    private ProduktBillingTablePanel tableBillingPanel = null;
    private ProduktBillingAdminPanel produktBillingPanel = null;

    /**
     * Default-Const.
     */
    public ProduktBillingAdminFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    protected AbstractAdminPanel[] getAdminPanels() {
        return new AbstractAdminPanel[] { produktBillingPanel, tableBillingPanel };
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected void createGUI() {
        setTitle("Billing-Produkte");
        getNewButton().setVisible(false);

        produktBillingPanel = new ProduktBillingAdminPanel();
        tableBillingPanel = new ProduktBillingTablePanel(produktBillingPanel);
        produktBillingPanel.setProdukteTable(tableBillingPanel.getProdukteTable());

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tableBillingPanel);
        split.setBottomComponent(produktBillingPanel);

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
    public void update(Observable arg0, Object arg1) {
    }

}


